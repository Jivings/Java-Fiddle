package controllers;

import play.*;
import play.mvc.*;
import play.data.DynamicForm;
import play.mvc.Http.RequestBody;

import views.html.*;
import models.*;

import org.codehaus.jackson.JsonNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.bytecode.ClassFile;

public class Run extends Controller {

  private static FiddleCompiler c = new FiddleCompiler();

  /**
   * Get the project details for execution, compiling if necessary
   */
  public static Result getSaved(String uuid, Long revision) {
    ProjectModel p = ProjectModel.find.byId(uuid);
    String args = p.arguments;
    String classname = p.classname;
    return ok(run.render(uuid, classname, args));
  }

  
  public static Result get(String id) {
    CompiledClassModel clazz = CompiledClassModel.find.byId(id);
    return ok(run.render(clazz.id, clazz.classname, clazz.arguments));
  }

  public static Result compile() {
    DynamicForm requestData = DynamicForm.form().bindFromRequest();
    String classname = requestData.get("classname");
    String code = requestData.get("code");
    String args = requestData.get("args");
    
    if (classname == null || code == null) {
      return badRequest("Missing required parameter");
    }

    c.addSource(classname, code);
    if (c.compile()) {
      byte[] clazz = c.getCompiledClass(classname + ".class");
      if (clazz != null) {
        
        CompiledClassModel clazzModel = new CompiledClassModel();
        clazzModel.classdata = clazz;
        clazzModel.classname = classname;
        clazzModel.arguments = args;
        clazzModel.save();

        // TODO, perhaps c.getCompiledClass is returning null?
        //return ok(clazz);
        return ok(clazzModel.id);

      }
      return ok("Success");
    }
    else {
      return ok(c.getDiagnostics());
    }

  }

  static void setFormat() { 
  } 

  public static Result getClassFile(String id, String classname) {
    CompiledClassModel clazz = CompiledClassModel.find.byId(id);
    response().setContentType("application/x-java-class");
    /*File file = new File(classname);
    try {
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(clazz.classdata);
    }
    catch (IOException ioe) {
    	ioe.printStackTrace();
    }*/
    
    return ok(clazz.classdata);
  }
  
  private static class RunClassLoader extends ClassLoader {
	  public Class<?> makeClass(final String name, byte[] b) {
		   return defineClass(name, b, 0, b.length);
	   }
  }

}
