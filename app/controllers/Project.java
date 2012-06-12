package controllers;

import play.*;
import play.mvc.*;

import views.html.*;
import models.*;

public class Project extends Controller {

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
    return ok(index.render(p));
  }
  /**
   * Get a project revision
   */
  public static Result patch(String uuid, Long revisionId) {

    return ok("patch");
  }

  /**
   * Save a project revision
   */
  public static Result save(String uuid) {
    return ok("save");

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
    p.code = "Code";
    p.save();
    return ok("Debug sample created");
  }
}
