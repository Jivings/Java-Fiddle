package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.mvc.Http.RequestBody;

import views.html.*;
import models.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.Arrays;

import difflib.*;

public class Project extends Controller {

  public static Form<ProjectModel> projectForm = Form.form(ProjectModel.class);

  /**
   * Get the default project
   */
  public static Result getDefault() {
    try {
      return get("default");
    }
    catch (NullPointerException e) {
      debug();
      return get("default");
    }

  }

  /**
   * Get a saved Project
   */
  public static Result get(String uuid) {
    // get project from the database
    ProjectModel p = ProjectModel.find
      .where()
      .eq("uuid", uuid)
      .eq("revision", 0)
      .findUnique();
    System.out.println(p.classname);
    // fill the form model and display
    return ok(index.render(projectForm.fill(p))); // GET views.index.html
  }

  /**
   * Get a project revision
   */
  public static Result patch(String uuid, Long revisionId) {
    ProjectModel p = ProjectModel.find.where().eq("uuid", uuid).eq("revision", revisionId).findUnique();
    // TODO can get the patch and use it here...

    return ok(index.render(projectForm.fill(p))); // GET views.index.html 
  }

  /**
   * Save a project revision
   */
  public static Result save(String uuid) {
    Form<ProjectModel> filledForm = projectForm.bindFromRequest();
    if (filledForm.hasErrors()) {
      return badRequest(
        views.html.index.render(filledForm)
      );
    }
    else {
      ProjectModel p = filledForm.get();
      ProjectModel old = ProjectModel.find.where().eq("uuid", uuid).orderBy("revision desc").findList().get(0);
      System.out.println("Saving a new revision of " + old.classname);
      // create the diff
      List<String> oldCode = toLines(old.code);
      List<String> newCode = toLines(p.code);
      
      p.patch = DiffUtils.diff(oldCode, newCode);
      // increment the revision number
      p.revision = old.revision + 1;
      p.uuid = old.uuid;
      p.save();
      // redirect to the revision!
      return redirect("/"+uuid+"/"+p.revision);

    }
  }

  public static Result saveRevision(String uuid, Long revision) {
    return TODO;
  }

  /**
   * Create a new Project
   */
  public static Result newProject() {
    Form<ProjectModel> filledForm = projectForm.bindFromRequest();
    if (filledForm.hasErrors()) {
      return badRequest(
        views.html.index.render(filledForm)
      );
    }
    else {
      ProjectModel p = filledForm.get();
      // create a Unique Identifier
      p.uuid = createUUID();    
      // save to the database
      p.save();
      return redirect("/"+p.uuid);
    }
  }

  @BodyParser.Of(BodyParser.Json.class)
  public static Result compileTemp(String classname, String code) {
    RequestBody body = request().body();
    return ok("got json: " + body.asJson());
  }

  private static boolean compile(String classname, String code) {
    final FiddleCompiler c = new FiddleCompiler();
    c.addSource(classname, code);

    if(c.compile()) {/*
      try {
        p.clazz = c.load(p.classname);
      }
      catch(ClassNotFoundException cnfe) {
        System.out.println("Class not found: " + p.classname);
        return false;
      }*/
      return true;
    }
    //p.compilationErrors = c.getDiagnostics();
    return false;
  }

  public static Result debug() {
    ProjectModel p = new ProjectModel();
    p.uuid = "default";
    p.classname = "HelloWorld";
    p.code = 
      "public class HelloWorld { \n" +
      "  public static void main(String... args) { \n" +
      "    System.out.println(\"Hello World\"); \n" +
      "  } \n" + 
      "}";
    p.save();
    return ok("Debug sample created");
  }

  private static SecureRandom random = new SecureRandom();

  
  static char[] available = new char[52];
  // initialise values for UUID
  static {
    int idx = 0, upper = 65, lower = 97;
    for (int x = upper; x < 123; x++, idx++) {
      if (x == 91) {
        x = lower;
      }
      available[idx] = (char) x;
    }
  }

  /**
   * Create a Unique Identifier from the available 
   * chars.
   * This gives 52^6 different values.
   *
   * FIXME: Birthday paradox
   * TODO: Check that value doesn't exist.
   */
  public static String createUUID() {
    final char[] id = new char[6];
    for (int i = 0; i < id.length; i++) {
      id[i] = available[random.nextInt(52)];
    }
    return new String(id);
  }

  private static List<String> toLines(String code) {
    return Arrays.asList(code.split("\\r?\\n"));
  }
}

