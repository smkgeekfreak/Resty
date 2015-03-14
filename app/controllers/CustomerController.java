package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordnik.swagger.annotations.*;
import model.customer.CacheDAO;
import model.customer.Customer;
import play.Logger;
import play.libs.Json;
import play.mvc.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by mbp-sm on 3/13/15.
 */
@Path("/customers")
@Api(
        value="/customers",
        description="API for creation, modification, and administration of customers",
        basePath="/customers-base"
)
public class CustomerController extends Controller {
    @Path("/customers")
    @Produces("application/json")
    @ApiOperation(
            value = "List of customers",
            nickname = "All customers",
            notes = "Return all customers",
            responseContainer = "Array",
            response = model.customer.Customer.class,
            httpMethod = "GET",
            position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = Http.Status.OK, message = "Customer list", response = model.customer.Customer.class),
            @ApiResponse(code = Http.Status.NOT_FOUND, message = "No customers found")})
    public static Result findAll() {
        return Results.ok(Json.toJson(CacheDAO.findAll()));
    }
    @Path("/customers")
    @Produces("application/json")
    @ApiOperation(
            value = "Return customer based on unique id",
            nickname = "customer_by_uid",
            notes = "Return customer based on the uid in the path",
            response = Customer.class,
            httpMethod = "GET",
            position = 0)
    @ApiResponses(value = {
            @ApiResponse(code = Http.Status.OK, message = "Returning customer", response = Customer.class),
            @ApiResponse(code = Http.Status.BAD_REQUEST, message = "Invalid endpoint"),
            @ApiResponse(code = Http.Status.NOT_FOUND, message = "Customer could not be found for (id)")})
    @ApiImplicitParams(@ApiImplicitParam(dataType = "int", name = "id", paramType = "path"))
    public static Result findById(Long id) {
        Customer found = CacheDAO.find(id);
        if( found == null )
            return notFound("Customer could not be found for ("+id+")");
        Logger.info("Customer= " + Json.toJson(found));
        return Results.ok(Json.toJson(found));
    }
    @POST
    @Path("/customers")
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(
            value = "Create or modify a customer",
            nickname = "add_customer",
            notes = "Primary usage to create a new customer. Can be used for " +
                    "modification of an existing customer (not recommended)." +
                    "To create a new customer the uid property should be omitted. " +
                    "If the uid is provided and a customer exists with that uid the " +
                    "information for that will be updated.",
            response = Customer.class,
            httpMethod = "POST",
            position = 2)
    @ApiResponses(value =
            {
                    @ApiResponse(code = Http.Status.CREATED, message = "Customer Created", response = Customer.class),
                    @ApiResponse(code = Http.Status.BAD_REQUEST, message = ""),
                    @ApiResponse(code = Http.Status.EXPECTATION_FAILED, message = "Could not parse input"),
            }
    )
    @BodyParser.Of(BodyParser.Json.class)
    @ApiImplicitParams(@ApiImplicitParam(dataType = "model.customer.Customer", name = "customer_data", paramType = "body"))
    public static Result post() {
        try {
            int returnCode = OK;
            Logger.info("Request to create Customer" + request().body().asJson());
            Logger.info("headers = " + Json.toJson(request().headers()));

            JsonNode json = request().body().asJson();
            //TODO: Validation logic, there has to be a better way
            if(json.findPath("companyName").asText().isEmpty() ) {
                return badRequest("Company name not provided");
            }
            CacheDAO dao = Json.fromJson(json, CacheDAO.class);
            Logger.debug("attempting cache:" + Json.toJson(dao));
            if (dao.save()) {
                returnCode = Http.Status.CREATED;
            }
            String annotatedPath = CustomerController.class.getMethod("post").getAnnotation(Path.class).value();
            Logger.debug("annotation path:" + annotatedPath);
            response().setHeader("Location", annotatedPath + "/" +dao.uid);
            return status(returnCode, Json.toJson(dao));
        } catch( Exception e ) {
            Logger.error("Caused: "+ Json.toJson(e.getMessage()));
            return status(EXPECTATION_FAILED);
        }
    }
}
