package controllers;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by mbp-sm on 3/13/15.
 */
@Api(
        value="/customers",
        description="API for creation, modification, and administration of customers",
        basePath="/customers-base"
)
public class CustomerController extends Controller {
    @Path("/users")
    @Produces({"application/json", "application/xml"})
    @ApiOperation(
            value = "List of customers",
            nickname = "All customers",
            notes = "",
            responseContainer = "Array",
            response = model.customer.Customer.class,
            httpMethod = "GET",
            position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = Http.Status.OK, message = "Customer list", response = model.customer.Customer.class),
            @ApiResponse(code = Http.Status.NOT_FOUND, message = "No users found")})
    public static Result findAll() {
        return Results.ok(Json.toJson("[{\"test\":\"test\"}]"));
    }
}
