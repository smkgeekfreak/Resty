package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.*;
import play.mvc.*;
import play.libs.Json;
import play.Logger;

import views.html.*;

import java.util.*;

import model.User;

import com.wordnik.swagger.annotations.*;
import com.wordnik.swagger.annotations.ApiResponse;

import javax.ws.rs.*;

@Api(value = "/user", description = "API for creation and administration of users")
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
            position = 0)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid endpoint", response=model.User.class),
            @ApiResponse(code = 404, message = "No users found")})
    public static Result all(){
        List<User> users = new ArrayList<>();
        users.add(new User(1,"Sam"));
        users.add(new User(2,"Julie"));
        users.add(new User(3,"Parson"));
        users.add(new User(4,"Kasandra"));
        Logger.info("users = " + Json.toJson(users));
        return Results.ok(Json.toJson(users));
    }

    @BodyParser.Of(BodyParser.Json.class)
    @PUT
    @Path("/user")
    @Produces({"application/json", "application/xml"})
    @ApiOperation(
            value = "Return collection of all users",
            nickname = "users",
            notes = "Return list of all users",
            response = model.User.class,
            httpMethod = "PUT",
            position = 0)
    @ApiResponses(value =
            {
                    @ApiResponse(code = Http.Status.BAD_REQUEST, message = "Invalid endpoint", response=model.User.class),
                    @ApiResponse(code = Http.Status.NOT_FOUND, message = "No users found", response=model.User.class)
            }
    )
    public static Result createUser(
            @ApiParam(value = "User Id", required = true) @PathParam("user") String marker_name
    ){
        Logger.info(request().body().asJson().asText());
        JsonNode json = request().body().asJson();
        User user = Json.fromJson(json, User.class);

        //beer.save();

        return ok(Json.toJson(user));
    }
}
