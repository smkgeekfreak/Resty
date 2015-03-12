package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.UserCache;
import model.UserDAO;
import play.*;
import play.mvc.*;
import play.libs.Json;
import play.Logger;

import java.util.*;

import model.User;

import com.wordnik.swagger.annotations.*;


import javax.ws.rs.*;

@Api(value = "/user", description = "creation and administration of users")
public class UserController extends Controller {

    @GET
    @Path("/users")
    @Produces({"application/json", "application/xml"})
    @ApiOperation(
            value = "Return collection of all users",
            nickname = "users",
            notes = "Return list of all users",
            responseContainer = "Array",
            response = model.User.class,
            httpMethod = "GET",
            position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = Http.Status.OK, message = "Returning users", response = model.User.class),
            @ApiResponse(code = Http.Status.BAD_REQUEST, message = "Invalid endpoint"),
            @ApiResponse(code = Http.Status.NOT_FOUND, message = "No users found")})
    public static Result all() {
//        List<User> users = new ArrayList<>();
//        users.add(new User(1, "A"));
//        users.add(new User(2, "Julie"));
//        users.add(new User(3, "Parson"));
//        users.add(new User(4, "Kasandra"));
//        Logger.info("users = " + Json.toJson(users));
        return Results.ok(Json.toJson(UserCache.findAll()));
    }

    @GET
    @Path("/user")
    @Produces({"application/json", "application/xml"})
    @ApiOperation(
            value = "Return user",
            nickname = "user",
            notes = "Return user",
            response = model.User.class,
            httpMethod = "GET",
            position = 0)
    @ApiResponses(value = {
            @ApiResponse(code = Http.Status.OK, message = "Returning user", response = model.User.class),
            @ApiResponse(code = Http.Status.BAD_REQUEST, message = "Invalid endpoint"),
            @ApiResponse(code = Http.Status.NOT_FOUND, message = "No users found")})
    @ApiImplicitParams(@ApiImplicitParam(dataType = "int", name = "id", paramType = "path"))
    public static Result findById(int id) {
        User found = UserCache.find(id);
        Logger.info("user = " + Json.toJson(found));
        return Results.ok(Json.toJson(found));
    }

    @PUT
    @Path("/user")
    @Produces({"application/json", "application/xml"})
    @ApiOperation(
            value = "Create a user based on name",
            nickname = "add_user",
            notes = "Creates a new users and returns it",
            response = model.User.class,
            httpMethod = "PUT",
            position = 2)
    @ApiResponses(value =
            {
                    @ApiResponse(code = Http.Status.CREATED, message = "User Created", response = model.User.class),
                    @ApiResponse(code = Http.Status.FOUND, message = "User already exists"),
                    @ApiResponse(code = Http.Status.BAD_REQUEST, message = "Invalid endpoint"),
                    @ApiResponse(code = Http.Status.NOT_FOUND, message = "No users found"),
                    @ApiResponse(code = Http.Status.EXPECTATION_FAILED, message = "Could not parse input"),
            }
    )
    @BodyParser.Of(BodyParser.Json.class)
    @Consumes("application/json")
    @ApiImplicitParams(@ApiImplicitParam(dataType = "model.User", name = "user_data", paramType = "body"))
    public static Result createUser() {
//        UserDAO user = null;
        UserCache cache = null;
        Logger.info("Request to create User" + request().body().asJson());
        Logger.info("headers = " + Json.toJson(request().headers()));

//        try {
            JsonNode json = request().body().asJson();
//            user = Json.fromJson(json, UserDAO.class);
            cache = Json.fromJson(json, UserCache.class);
//            user.save();
            Logger.info("attempting cache");
            cache.save();
            Logger.info("successful cache");
//        } catch (Exception e) {
//            Logger.error("Problem parsing input from " + request().body().asJson() );
//            return status(Http.Status.EXPECTATION_FAILED, "Could not parse input from " + request().body().asJson() );
//        }

        int returnCode = Http.Status.NOT_MODIFIED;

        if (cache != null)
            returnCode = Http.Status.CREATED;
        else {
            returnCode = Http.Status.FOUND;
        }
        switch (returnCode) {
            case Http.Status.NOT_MODIFIED:
                return status(returnCode, cache.name + " was not created");
            default:
                return status(returnCode, Json.toJson(cache));
        }
    }
}
