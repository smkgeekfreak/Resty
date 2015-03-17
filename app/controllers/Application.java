package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

    /**
     * Redirect index directly to main API listing, redirecting to API documentation
     * @return
     */
    public static Result index() {
        return temporaryRedirect("/api");
    }

    /**
     * Redirect to Swagger interactive API documentation pages
     * @return
     */
    public static Result api() {
        return redirect("/assets/swagger-ui/index.html");
    }

}
