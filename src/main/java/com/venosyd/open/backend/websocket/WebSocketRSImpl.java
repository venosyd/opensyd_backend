package com.venosyd.open.backend.websocket;

import java.util.HashMap;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author sergio e. lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 */
@Path("/")
public class WebSocketRSImpl implements WebSocketRS {

    @Override
    public Response send(String json) {
        return process(json, (request) -> {
            var clientID = request.get("client-id");
            var message = request.get("message");

            var result = new HashMap<String, String>();

            try {
                Sessions.INSTANCE.sendMessage(clientID, message);

                result.put("status", "ok");
                return makeResponse(result);
            } catch (Exception e) {
                result.put("status", "error");
                result.put("message", e.getLocalizedMessage());

                return makeResponse(result);
            }
        }, "WS-RS Send");
    }

    @Override
    public Response broadcast(String json) {
        return process(json, (request) -> {
            var service = request.get("service");
            var message = request.get("message");

            var result = new HashMap<String, String>();

            try {
                Sessions.INSTANCE.broadcastMessage(service, message);

                result.put("status", "ok");
                return makeResponse(result);
            } catch (Exception e) {
                result.put("status", "error");
                result.put("message", e.getLocalizedMessage());
                
                return makeResponse(result);
            }
        }, "WS-RS Broadcast");
    }
}