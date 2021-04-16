package ru.pshiblo.services.http.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.http.dto.CurrentTrack;

import java.io.IOException;
import java.io.OutputStream;

public abstract class RestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestParamValue = null;

        if("GET".equals(exchange.getRequestMethod())) {
            requestParamValue = handleGetRequest(exchange);
        }

        handleResponse(exchange, requestParamValue);
    }

    private String handleGetRequest(HttpExchange httpExchange) {
        return httpExchange.
                getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("=")[1];
    }

    private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        StringBuilder htmlBuilder = new StringBuilder();

        ObjectMapper objectMapper = new ObjectMapper();
        CurrentTrack currentTrack = new CurrentTrack();
        currentTrack.setTrack(Context.getMusicService().getPlayingTrack());
        String json = objectMapper.writeValueAsString(currentTrack);


        // this line is a must
        httpExchange.sendResponseHeaders(200, json.length());

        outputStream.write(json.getBytes());
        outputStream.flush();
        outputStream.close();
    }


    protected abstract String getHandler();
}
