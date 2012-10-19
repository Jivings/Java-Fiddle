package controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import java.io.Reader;
import java.io.StringWriter;
import java.io.InputStreamReader;

/**
 * Compiles Java Source code into an Instance of java.lang.Class
 * so that it can be saved and later executed.
 */ 
public class FiddleCompiler {

  // tools for compiling
  private final JavaCompiler compiler;
  private final DiagnosticCollector<JavaFileObject> diagnostics;
  // for storing compiled files
  private final JavaFileManager fileManager;

  // for storing precompiled source
  // Key is classname, value is Java code.
  private final Map<String, String> sources;

  // keep a reference to the default classloader
  // this is needed for the FileManger chaining. 
  // Can be overriden with setClassLoader()
  private ClassLoader classloader;
 
  /**
   * Constructor
   * @params javasource map of classname and Java source 
   *         code to be compiled.
   */ 
  public FiddleCompiler(Map<String, String> javasources) {
    // get the system Compiler
    this.compiler = ToolProvider.getSystemJavaCompiler();  
    // to save and report compilation errors
    this.diagnostics = new DiagnosticCollector<JavaFileObject>();
    this.sources = javasources;
    this.fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    this.classloader = new ClassLoaderImpl(Compiler.class.getClassLoader());
  }

  public FiddleCompiler() {
    this(new HashMap<String, String>());
  }


  public void addSource(String classname, String source) {
    this.sources.put(classname, source);
  }
  /**
   * Compile all the sources that have been loaded and return a Map of 
   * Classname to Class files.
   */
  public synchronized boolean compile() {
    // for storing precompiled source files
    final List<JavaFileObject> sourceFiles = new ArrayList<JavaFileObject>();
    // for dealing with our virtual .java files
    final FileManagerImpl sourceFileManager = new FileManagerImpl(this.fileManager, (ClassLoaderImpl)this.classloader);
    // create a JavaFileObject for each source file
    JavaFileObjectImpl source;
    // iterate over the Java sources
    for(Entry<String, String> entry : sources.entrySet()) {
      // create a virtual source file
      source = new JavaFileObjectImpl(entry.getKey(), entry.getValue());
      sourceFiles.add(source);
      // store the source file in the FileManager with .java extension
      sourceFileManager.putFileForInput(StandardLocation.SOURCE_PATH, "", entry.getKey() + ".java", source);
    }

    // compile all the sources
    final CompilationTask task = compiler.getTask(null, sourceFileManager, this.diagnostics, null, null, sourceFiles);
    final Boolean result = task.call();
    if (result == null || !result.booleanValue()) {
      System.out.println("Compilation failed");
      return false;
    }
    else {
      return true;
    }
      
  }
 
  public byte[] getCompiledClass(String name) {
    System.out.println("Getting " + name);
    // get the stream from the classloader
    //byte[] clazzBytes = (byte[])((ClassLoaderImpl) this.classloader).getResourceAsStream(name);
    byte[] clazzBytes = ((ClassLoaderImpl) this.classloader).getFile(name).getByteCode();
    // create a string from the stream
    //Reader r = new InputStreamReader(clazz);  
    //byte[] clazzBytes = new byte[15000];
    //try {
      //int n;
      //int i = 0;
      //while ( (n = r.read()) != -1 ) {
      //  clazzBytes[i] = (byte) n;
       // System.out.print(Integer.toHexString(clazzBytes[i++]) + " ");
      //}
    //}
    //catch (IOException e) {
     // e.printStackTrace();
    //  return null;
   // }
    return clazzBytes;
    //StringWriter sw = new StringWriter();  
    // TODO: Not big enough.
    //char[] buffer = new char[1024]; 
    //try {
    //  for (int n; (n = r.read(buffer)) != -1; )  
    //    sw.write(buffer, 0, n);  
    //} catch (IOException e){
    //  e.printStackTrace();
    //  return null;
    //}
  }

  /**
   * If compile returns false, this will tell you why
   */ 
  public String getDiagnostics() {
    StringBuilder sb = new StringBuilder();
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
			sb.append(diagnostic.getCode());
			sb.append(diagnostic.getKind());
			sb.append(diagnostic.getPosition());
			sb.append(diagnostic.getStartPosition());
			sb.append(diagnostic.getEndPosition());
			sb.append(diagnostic.getSource());
			sb.append(diagnostic.getMessage(null));
		}
		return sb.toString();
  }
  
  public ClassLoader getClassLoader() {
    return this.classloader;
  }

  public void setClassLoader(ClassLoader classloader) {
    this.classloader = classloader;
  }
  
  /**
   * Load a single class
   */
  public Class load(String classname) throws ClassNotFoundException {
    return classloader.loadClass(classname);
  }
  
  /**
   * Load all the precompiled sources
   */
  public Map<String, Class> loadAll() throws ClassNotFoundException {
    Map<String, Class> compiled = new HashMap<String, Class>();
    
    // For each class name in the inpput map, get its compiled
    // class and put it in the output map
     
    for (String qualifiedClassName : this.sources.keySet()) {
      final Class newClass = this.load(qualifiedClassName);
      compiled.put(qualifiedClassName, newClass);
    }
    
    return compiled;
  }
}





/******************************************************************************/
/*    End of Compiler - Depending classes after this point.
/*



/**
 * A JavaFileManager which manages Java source and classes. This FileManager
 * delegates to the JavaFileManager and the ClassLoaderImpl provided in the
 * constructor. The sources are all in memory CharSequence instances and the
 * classes are all in memory byte arrays.
 */
final class FileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {
   // the delegating class loader (passed to the constructor)
   private final ClassLoaderImpl classLoader;

   // Internal map of filename URIs to JavaFileObjects.
   private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

   /**
    * Construct a new FileManager which forwards to the <var>fileManager</var>
    * for source and to the <var>classLoader</var> for classes
    * 
    * @param fileManager
    *           another FileManager that this instance delegates to for
    *           additional source.
    * @param classLoader
    *           a ClassLoader which contains dependent classes that the compiled
    *           classes will require when compiling them.
    */
   public FileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader) {
      super(fileManager);
      this.classLoader = classLoader;
   }

   /**
    * @return the class loader which this file manager delegates to
    */
   public ClassLoader getClassLoader() {
      return classLoader;
   }

   /**
    * For a given file <var>location</var>, return a FileObject from which the
    * compiler can obtain source or byte code.
    * 
    * @param location
    *           an abstract file location
    * @param packageName
    *           the package name for the file
    * @param relativeName
    *           the file's relative name
    * @return a FileObject from this or the delegated FileManager
    * @see javax.tools.ForwardingJavaFileManager#getFileForInput(javax.tools.JavaFileManager.Location,
    *      java.lang.String, java.lang.String)
    */
   @Override
   public FileObject getFileForInput(Location location, String packageName,
         String relativeName) throws IOException {
      FileObject o = fileObjects.get(uri(location, packageName, relativeName));
      if (o != null)
         return o;
      return super.getFileForInput(location, packageName, relativeName);
   }

   /**
    * Store a file that may be retrieved later with
    * {@link #getFileForInput(javax.tools.JavaFileManager.Location, String, String)}
    * 
    * @param location
    *           the file location
    * @param packageName
    *           the Java class' package name
    * @param relativeName
    *           the relative name
    * @param file
    *           the file object to store for later retrieval
    */
   public void putFileForInput(StandardLocation location, String packageName,
         String relativeName, JavaFileObject file) {
      fileObjects.put(uri(location, packageName, relativeName), file);
   }

   /**
    * Convert a location and class name to a URI
    */
   private URI uri(Location location, String packageName, String relativeName) {
      return URI.create(location.getName() + '/' + packageName + '/'
            + relativeName);
   }

   /**
    * Create a JavaFileImpl for an output class file and store it in the
    * classloader.
    * 
    * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location,
    *      java.lang.String, javax.tools.JavaFileObject.Kind,
    *      javax.tools.FileObject)
    */
   @Override
   public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName,
         Kind kind, FileObject outputFile) throws IOException {
      JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
      classLoader.add(qualifiedName, file);
      return file;
   }

   @Override
   public ClassLoader getClassLoader(JavaFileManager.Location location) {
      return classLoader;
   }

   @Override
   public String inferBinaryName(Location loc, JavaFileObject file) {
      String result;
      // For our JavaFileImpl instances, return the file's name, else
      // simply run the default implementation
      if (file instanceof JavaFileObjectImpl)
         result = file.getName();
      else
         result = super.inferBinaryName(loc, file);
      return result;
   }

   @Override
   public Iterable<JavaFileObject> list(Location location, String packageName,
         Set<Kind> kinds, boolean recurse) throws IOException {
      Iterable<JavaFileObject> result = super.list(location, packageName, kinds,
            recurse);
      ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();
      if (location == StandardLocation.CLASS_PATH
            && kinds.contains(JavaFileObject.Kind.CLASS)) {
         for (JavaFileObject file : fileObjects.values()) {
            if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName))
               files.add(file);
         }
         files.addAll(classLoader.files());
      } else if (location == StandardLocation.SOURCE_PATH
            && kinds.contains(JavaFileObject.Kind.SOURCE)) {
         for (JavaFileObject file : fileObjects.values()) {
            if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName))
               files.add(file);
         }
      }
      for (JavaFileObject file : result) {
         files.add(file);
      }
      return files;
   }
}


/**
 * A custom ClassLoader which maps class names to JavaFileObjectImpl instances.
 */
final class ClassLoaderImpl extends ClassLoader {
   private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

   ClassLoaderImpl(final ClassLoader parentClassLoader) {
      super(parentClassLoader);
   }

   /**
    * @return An collection of JavaFileObject instances for the classes in the
    *         class loader.
    */
   Collection<JavaFileObject> files() {
      return Collections.unmodifiableCollection(classes.values());
   }

   @Override
   protected Class<?> findClass(final String qualifiedClassName)
         throws ClassNotFoundException {
      JavaFileObject file = classes.get(qualifiedClassName);
      if (file != null) {
         byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
         
         for (byte b : bytes) {
          System.out.print(Integer.toHexString(b) + " ");
         }

         return defineClass(qualifiedClassName, bytes, 0, bytes.length);
      }
      // Workaround for "feature" in Java 6
      // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6434149
      try {
         Class<?> c = Class.forName(qualifiedClassName);
         return c;
      } catch (ClassNotFoundException nf) {
         // Ignore and fall through
      }
      return super.findClass(qualifiedClassName);
   }
   
   protected Class<?> makeClass(final String name, byte[] b) {
	   return defineClass(name, b, 0, b.length);
   }

   /**
    * Add a class name/JavaFileObject mapping
    * 
    * @param qualifiedClassName
    *           the name
    * @param javaFile
    *           the file associated with the name
    */
   void add(final String qualifiedClassName, final JavaFileObject javaFile) {
      System.out.println("added " + qualifiedClassName);
      classes.put(qualifiedClassName, javaFile);
   }

   @Override
   protected synchronized Class<?> loadClass(final String name, final boolean resolve)
         throws ClassNotFoundException {
      return super.loadClass(name, resolve);
   }

   @Override
   public InputStream getResourceAsStream(String name) {
      if (name.endsWith(".class")) {
         String qualifiedClassName = name.substring(0,
               name.length() - ".class".length()).replace('/', '.');
         JavaFileObjectImpl file = (JavaFileObjectImpl) classes.get(qualifiedClassName);
         if (file != null) {
            for (byte b : file.getByteCode()) {
              System.out.print(Integer.toHexString(b) + " ");
            }
            System.out.println();
            return new ByteArrayInputStream(file.getByteCode());
         }
      }
      return super.getResourceAsStream(name);
   }

   public JavaFileObjectImpl getFile(String name) {
      if (name.endsWith(".class")) {
         name = name.substring(0,
               name.length() - ".class".length()).replace('/', '.');
      }
      JavaFileObjectImpl file = (JavaFileObjectImpl) classes.get(name);
      return file;
        
   }
}

/**
 * A JavaFileObject which contains either the source text or the compiler
 * generated class. This class is used in two cases.
 * <ol>
 * <li>This instance uses it to store the source which is passed to the
 * compiler. This uses the
 * {@link JavaFileObjectImpl#JavaFileObjectImpl(String, CharSequence)}
 * constructor.
 * <li>The Java compiler also creates instances (indirectly through the
 * FileManagerImplFileManager) when it wants to create a JavaFileObject for the
 * .class output. This uses the
 * {@link JavaFileObjectImpl#JavaFileObjectImpl(String, JavaFileObject.Kind)}
 * constructor.
 * </ol>
 * This class does not attempt to reuse instances (there does not seem to be a
 * need, as it would require adding a Map for the purpose, and this would also
 * prevent garbage collection of class byte code.)
 */
final class JavaFileObjectImpl extends SimpleJavaFileObject {
   // If kind == CLASS, this stores byte code from openOutputStream
   private ByteArrayOutputStream byteCode;

   // if kind == SOURCE, this contains the source text
   private final CharSequence source;

   /**
    * Construct a new instance which stores source
    * 
    * @param baseName
    *           the base name
    * @param source
    *           the source code
    */
   JavaFileObjectImpl(final String baseName, final CharSequence source) {
      super(URI.create(baseName + Kind.SOURCE.extension), Kind.SOURCE);
      this.source = source;
   }

   /**
    * Construct a new instance
    * 
    * @param name
    *           the file name
    * @param kind
    *           the kind of file
    */
   JavaFileObjectImpl(final String name, final Kind kind) {
      super(URI.create(name + Kind.SOURCE.extension), kind);
      source = null;
   }

   /**
    * Return the source code content
    * 
    * @see javax.tools.SimpleJavaFileObject#getCharContent(boolean)
    */
   @Override
   public CharSequence getCharContent(final boolean ignoreEncodingErrors)
         throws UnsupportedOperationException {
      if (source == null)
         throw new UnsupportedOperationException("getCharContent()");
      return source;
   }

   /**
    * Return an input stream for reading the byte code
    * 
    * @see javax.tools.SimpleJavaFileObject#openInputStream()
    */
   @Override
   public InputStream openInputStream() {
      return new ByteArrayInputStream(getByteCode());
   }

   /**
    * Return an output stream for writing the bytecode
    * 
    * @see javax.tools.SimpleJavaFileObject#openOutputStream()
    */
   @Override
   public OutputStream openOutputStream() {
      byteCode = new ByteArrayOutputStream();
      return byteCode;
   }

   /**
    * @return the byte code generated by the compiler
    */
   byte[] getByteCode() {
      return byteCode.toByteArray();
   }
}


