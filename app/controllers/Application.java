package controllers;

import play.*;
import play.mvc.*;

import views.html.*;
import controllers.*;

public class Application extends Controller {
  
  public static Result index() {
    return ok(index.render("Shouldn't see this"));
  }
  
}
