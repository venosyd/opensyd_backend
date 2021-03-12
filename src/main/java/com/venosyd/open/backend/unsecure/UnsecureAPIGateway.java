package com.venosyd.open.backend.unsecure;

import java.net.InetSocketAddress;
import java.util.Map;

import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.util.Config;
import com.venosyd.open.gatekeeper.GateKeeper;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 * 
 *         carrega uma instancia do GATEKEEPER insegura, usada geralmente para
 *         desenvolvimento
 */
public class UnsecureAPIGateway implements Debuggable {

    public void init() {
        try {
            // pega ip e porta
            int port = Integer.parseInt((Config.INSTANCE.<Map<String, String>>get("locations")).get("http-dev-port"));

            // cria o socket
            var add = new InetSocketAddress("0.0.0.0", port);
            var server = new Server(add);

            var ctx = new ServletContextHandler();
            ctx.setContextPath("/");

            server.setHandler(new GateKeeper());

            server.start();

            out.tag("GATEKEEPER").ln("ONLINE");

        } catch (Exception e) {
            err.exception("GATEKEEPER EXCEPTION", e);
        }
    }
}
