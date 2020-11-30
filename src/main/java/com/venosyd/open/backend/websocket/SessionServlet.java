package com.venosyd.open.backend.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 * 
 *         integracao do servidor de websocket com o sistema
 */
public class SessionServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1839992605314672379L;

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(SessionWebSocket.class);
    }

}
