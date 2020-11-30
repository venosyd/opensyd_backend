package com.venosyd.open.backend.unsecure;

import java.net.InetSocketAddress;
import java.util.Map;

import com.venosyd.open.backend.websocket.SessionServlet;
import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.util.Config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 */
public class UnsecureWebSocketServer implements Debuggable {

    /**
     * servidor de desenvolvimento, sem interferencia de https
     */
    public void init() {
        try {
            // pega ip e porta
            int websocketport = Integer
                    .parseInt((Config.INSTANCE.<Map<String, String>>get("locations")).get("ws-port"));

            // cria o socket
            var add = new InetSocketAddress("0.0.0.0", websocketport);
            var server = new Server(add);

            var ctx = new ServletContextHandler();
            ctx.setContextPath("/");

            // websocket
            var websocketHolder = new ServletHolder("ws-events", SessionServlet.class);
            ctx.addServlet(websocketHolder, "/ws/*");

            server.setHandler(ctx);
            server.start();

            out.tag("WEBSOCKET-SERVER").ln("ONLINE");

        } catch (Exception e) {
            err.exception("WEBSOCKET-SERVER SERVER EXCEPTION", e);
        }
    }
}
