import play.GlobalSettings;
import play.Logger;
import play.api.mvc.Handler;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader requestHeader, String error) {
        Logger.info("Bad request:" + requestHeader.toString());
        return F.Promise.pure(Results.badRequest(requestHeader.toString()));
    }
    @Override
    public Handler onRouteRequest(Http.RequestHeader requestHeader) {
        Logger.debug("REQUEST HEADERS:" + requestHeader.acceptedTypes());
        Logger.debug("REQUEST URI:" + requestHeader.uri());
        Logger.debug("REQUEST QUERY:" + requestHeader.getQueryString("matchCriteria"));
        return super.onRouteRequest(requestHeader);
    }
}
