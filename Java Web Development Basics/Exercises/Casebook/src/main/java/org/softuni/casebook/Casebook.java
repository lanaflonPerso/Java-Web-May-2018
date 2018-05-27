package org.softuni.casebook;

import org.softuni.casebook.controllers.BaseController;
import org.softuni.casebook.controllers.fixed.ResourceController;
import org.softuni.casebook.routes.ControllerMethodEntry;
import org.softuni.casebook.routes.RoutesManager;
import org.softuni.javache.api.RequestHandler;
import org.softuni.javache.http.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class Casebook implements RequestHandler {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpSessionStorage sessionStorage;
    private boolean hasIntercepted;
    private RoutesManager routesManager;

    public Casebook(HttpSessionStorage sessionStorage, RoutesManager routesManager) {
        this.sessionStorage = sessionStorage;
        this.hasIntercepted = false;
        this.routesManager = routesManager;
        this.routesManager.initializeRoots(this.sessionStorage);
    }

    private byte[] processGetRequest() throws InvocationTargetException, IllegalAccessException {
        String url = this.httpRequest.getRequestUrl();
        Map<String, ControllerMethodEntry<Method, BaseController>> getMappingRoutes = this.routesManager.getGetMappingRoutes();
        if (getMappingRoutes.containsKey(url)) {
            ControllerMethodEntry<Method, BaseController> entry = getMappingRoutes.get(url);
            return (byte[]) entry.getKey().invoke(entry.getValue(), this.httpRequest, this.httpResponse);
        } else {
            return new ResourceController(this.sessionStorage).processResourceRequest(this.httpRequest, this.httpResponse);
        }
    }

    private byte[] processPostRequest() throws InvocationTargetException, IllegalAccessException {
        String url = this.httpRequest.getRequestUrl();
        Map<String, ControllerMethodEntry<Method, BaseController>> postMappingRoutes = this.routesManager.getPostMappingRoutes();
        if (postMappingRoutes.containsKey(url)) {
            ControllerMethodEntry<Method, BaseController> entry = postMappingRoutes.get(url);
            return (byte[]) entry.getKey().invoke(entry.getValue(), this.httpRequest, this.httpResponse);
        }

        return new byte[0];
    }

    @Override
    public byte[] handleRequest(String requestContent) throws InvocationTargetException, IllegalAccessException, UnsupportedEncodingException {
        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();

        byte[] result = null;

        if(this.httpRequest.getMethod().equals("GET")) {
            result = this.processGetRequest();
        } else if (this.httpRequest.getMethod().equals("POST")) {
            result = this.processPostRequest();
        }

        this.sessionStorage.refreshSessions();

        this.hasIntercepted = true;

        return result;
    }

    @Override
    public boolean hasIntercepted() {
        return this.hasIntercepted;
    }
}
