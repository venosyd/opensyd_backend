package com.venosyd.open.backend.websocket;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.venosyd.open.commons.util.RESTService;

/**
 * @author sergio e. lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 */
public interface WebSocketRS extends RESTService {

    String WEBSOCKET_BASE_URI = "/ws-rs";

    String WEBSOCKET_SEND_MESSAGE = "/send";

    String WEBSOCKET_BROADCAST_MESSAGE = "/broadcast";

    /**
     * { token: message: } manda uma mensagem especifica
     */
    @POST
    @Path(WEBSOCKET_SEND_MESSAGE)
    @Produces({ MediaType.APPLICATION_JSON })
    Response send(String json);

    /**
     * { service: message: } manda mensagem pra todo mundo
     */
    @POST
    @Path(WEBSOCKET_BROADCAST_MESSAGE)
    @Produces({ MediaType.APPLICATION_JSON })
    Response broadcast(String json);

}