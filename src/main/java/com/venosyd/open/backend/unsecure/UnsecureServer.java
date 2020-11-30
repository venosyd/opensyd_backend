package com.venosyd.open.backend.unsecure;

import java.net.InetSocketAddress;

import com.venosyd.open.backend.lib.Service;
import com.venosyd.open.backend.lib.ServletContextHandlerBuilder;
import com.venosyd.open.commons.log.Debuggable;

import org.eclipse.jetty.server.Server;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 * 
 *         levanta um servico em modo inseguro, seja para desenvolvimento ou
 *         quando esta em contexto quando tem um api gateway seguro fazendo a
 *         comunicacao com o mundo exterior
 */
public class UnsecureServer implements Debuggable {

    /**
     * servidor de desenvolvimento, sem interferencia de https
     */
    public void init(String serviceName) {
        try {
            var service = Service.build(serviceName);

            // cria o socket
            var add = new InetSocketAddress(service.getIp(), service.getPort());

            var server = new Server(add);
            server.setHandler(ServletContextHandlerBuilder.build(service));
            server.start();

            out.tag(serviceName.toUpperCase()).ln("ONLINE");

        } catch (Exception e) {
            err.exception(serviceName + " SERVER EXCEPTION", e);
        }
    }
}
