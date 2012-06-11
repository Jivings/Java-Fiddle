package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Project extends Controller {
  
  /**
   * Get a saved Project
   */
  public static Result get(String uuid) {
    return ok("get");
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
  public static Result new() {
    return ok("new");
  }

}
