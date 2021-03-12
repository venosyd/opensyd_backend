package com.venosyd.open.backend.unsecure;

import java.net.InetSocketAddress;

import com.venosyd.open.backend.lib.Service;
import com.venosyd.open.backend.lib.ServletContextHandlerBuilder;
import com.venosyd.open.commons.log.Debuggable;

import org.eclipse.jetty.server.Server;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 * 
 *         levanta um servico em modo inseguro, seja para desenvolvimento ou
 *         quando esta em contexto quando tem um api gateway seguro fazendo a
 *         comunicacao com o mundo exterior
 */
public class UnsecureSeeker implements Debuggable {

    /**
     * servidor de desenvolvimento, sem interferencia de https
     */
    public void init() {
        try {
            var service = Service.build("seeker");

            // cria o socket
            var add = new InetSocketAddress(service.getIp(), service.getPort());

            var server = new Server(add);
            server.setHandler(ServletContextHandlerBuilder.build(service, false));
            server.start();

            out.tag("SEEKER").ln("ONLINE");

        } catch (Exception e) {
            err.exception("SEEKER SERVER EXCEPTION", e);
        }
    }
}
