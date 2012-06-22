package controllers;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;
import models.*;

import java.security.SecureRandom;

public class Project extends Controller {

  public static Form<ProjectModel> projectForm = form(ProjectModel.class);

  /**
   * Get the default project
   */
  public static Result getDefault() {
    return get("default");
  }

  /**
   * Get a saved Project
   */
  public static Result get(String uuid) {
    // get project from the database
    ProjectModel p = ProjectModel.find.byId(uuid);
    // fill the form model and display
    return ok(index.render(projectForm.fill(p), p)); // GET views.index.html
  }

  /**
   * Get a project revision
   */
  public static Result patch(String uuid, Long revisionId) {
    return TODO; 
  }

  /**
   * Save a project revision
   */
  public static Result save(String uuid) {
    return TODO;

  }

  /**
   * Create a new Project
   */
  public static Result newProject() {
    // fill the form Object with POST values
    Form<ProjectModel> filledForm = projectForm.bindFromRequest();
    // construct the model
    ProjectModel p = filledForm.get();
    // create a Unique Identifier
    p.uuid = createUUID();    
    // save to the database
    p.save();
    return redirect("/"+p.uuid);
  }

  private static String compile() {
  }

  public static Result debug() {
    ProjectModel p = new ProjectModel();
    p.uuid = "default";
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
   * BEWARE: Birthday paradox
   * TODO: Check that value doesn't exist.
   */
  public static String createUUID() {
    final char[] id = new char[6];
    for (int i = 0; i < id.length; i++) {
      id[i] = available[random.nextInt(52)];
    }
    return new String(id);
  }
}
