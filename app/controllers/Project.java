package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

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
    return ok(index.render("get"));
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
    return ok("new");
  }

}
