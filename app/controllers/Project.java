package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;

import views.html.*;
import models.*;

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
    ProjectModel p = ProjectModel.find.byId(uuid);
    return ok(index.render(p, projectForm)); // GET views.index.html
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
    ProjectModel p = new ProjectModel();
    
    p.save();
    return ok("new");
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
}
