package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;
import play.Logger;

import views.html.*;

import java.util.*;

import model.User;

public class UserController extends Controller {

    public static Result byId(int id){
        return ok();
    }
    public static Result all(){
        List<User> users = new ArrayList<User>();
        users.add(new User(1,"Sam"));
        users.add(new User(2,"Julie"));
        Logger.info("users = " + Json.toJson(users));
        return Results.ok(Json.toJson(users));
    }
}
