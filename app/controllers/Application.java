package controllers;

import play.*;
import play.mvc.*;
import play.Logger;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    public static Result info(){
        Logger.info("info requested");
        return ok(index.render("return info."));
    }

}
