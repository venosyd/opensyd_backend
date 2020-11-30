package com.venosyd.open.backend.websocket;

import java.util.Date;
import java.util.Map;

import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.services.seeker.ServiceSeeker;
import com.venosyd.open.commons.util.DateUtil;
import com.venosyd.open.commons.util.JSONUtil;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 */
public class SessionWebSocket extends WebSocketAdapter implements Debuggable {

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);

        var request = JSONUtil.<String, Object>fromJSONToMap(message);

        if (!request.containsKey("client-id") || !request.containsKey("service")) {
            err.tag(DateUtil.fromDate(new Date())).ln("INVALID WEBSOCKET CONNECTION TEMPTATION");
        }

        // formato de exemplo de um JSON de servico
        // {
        // ____"headers": {
        // ________"...": "..."
        // ____-}
        // ____"client-id": "tagarela-1291defc7d7ab7",
        // ____"service": "login",
        // ____"action": "login",
        // ____"payload": {
        // ________"...": "..."
        // ____-},
        // }

        var clientID = (String) request.get("client-id");

        if (Sessions.INSTANCE.isNotConnected(clientID)) {
            Sessions.INSTANCE.getClientIDSession().put(clientID, getSession());
            Sessions.INSTANCE.getSessionClientID().put(getSession(), clientID);
        }

        if (_isServiceRequest(request)) {
            var service = (String) request.get("service");
            var action = (String) request.get("action");
            var payload = (Map<String, Object>) request.get("payload");
            var headers = (Map<String, String>) request.get("headers");

            ServiceSeeker.builder().service(service).method("post").path(action).headers(headers).body(payload).run();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        err.tag("WEBSOCKET EXCEPTION").ln(cause);
    }

    /** verifica se a requisicao do socket eh para um servico */
    private boolean _isServiceRequest(Map<String, Object> request) {
        return request.containsKey("service") && request.containsKey("action") && request.containsKey("payload");
    }

}
