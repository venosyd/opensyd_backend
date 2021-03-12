package com.venosyd.open.backend.secure;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.venosyd.open.backend.lib.Service;
import com.venosyd.open.backend.lib.ServletContextHandlerBuilder;
import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.util.Config;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 * 
 *         Instancia um servico seguro, bastante usado em caso de servidor
 *         monolitico
 */
public class SecureServer implements Debuggable {

    public void init(String serviceName) {
        try {
            var service = Service.build(serviceName);

            // pega ip e porta
            int httpPort = Integer.parseInt((Config.INSTANCE.<Map<String, String>>get("locations")).get("http-port"));
            int httpsPort = Integer.parseInt((Config.INSTANCE.<Map<String, String>>get("locations")).get("https-port"));

            // cria o socket
            var server = new Server();

            var httpConf = new HttpConfiguration();
            httpConf.setSecurePort(httpsPort);
            httpConf.setSecureScheme("https");

            var httpsConf = new HttpConfiguration(httpConf);
            httpsConf.addCustomizer(new SecureRequestCustomizer());

            var httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConf));
            httpConnector.setName("unsecured");
            httpConnector.setPort(httpPort);

            var passwd = Config.INSTANCE.<Map<String, String>>get("sec").get("key");
            var sslContextFactory = new SslContextFactory.Client();
            sslContextFactory.setKeyStorePath(new File("assets/sec/keystore.pkcs12").getCanonicalPath());
            sslContextFactory.setKeyStorePassword(new String(Base64.decodeBase64(passwd)));
            sslContextFactory.setKeyManagerPassword(new String(Base64.decodeBase64(passwd)));

            var httpsConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(httpsConf));
            httpsConnector.setName("secured");
            httpsConnector.setPort(httpsPort);

            server.setConnectors(new Connector[] { httpConnector, httpsConnector });

            String[] secureHosts = new String[] { "@secured" };
            var servletCtx = ServletContextHandlerBuilder.build(service);
            servletCtx.setVirtualHosts(secureHosts);

            var redirectHandler = new ContextHandler();
            redirectHandler.setContextPath("/");
            redirectHandler.setHandler(new SecureSchemeHandler());
            redirectHandler.setVirtualHosts(new String[] { "@unsecured" });

            // Establish all handlers that have a context
            var contextHandlers = new ContextHandlerCollection();
            contextHandlers.setHandlers(new Handler[] { redirectHandler, servletCtx });

            // Create server level handler tree
            var handlers = new HandlerList();
            handlers.addHandler(contextHandlers);
            handlers.addHandler(new DefaultHandler()); // round things out

            server.setHandler(handlers);
            server.start();

            out.tag(serviceName.toUpperCase()).ln("ONLINE");

        } catch (Exception e) {
            err.exception(serviceName + " SERVER EXCEPTION", e);
        }
    }

    /**
     * redirecionador achado aqui
     * https://gist.github.com/joakime/8fe05b0f57fc32df4546 com modificacoes daqui
     * https://stackoverflow.com/a/40698198/6083059
     */
    public static class SecureSchemeHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
            var httpConfig = HttpConnection.getCurrentConnection().getHttpChannel().getHttpConfiguration();

            if (baseRequest.isSecure()) {
                return;
            }

            if (httpConfig.getSecurePort() > 0) {
                var scheme = httpConfig.getSecureScheme();
                int port = httpConfig.getSecurePort();

                var url = URIUtil.newURI(scheme, baseRequest.getServerName(), port, baseRequest.getRequestURI(),
                        baseRequest.getQueryString());

                response.setContentLength(0);
                response.sendRedirect(url);
            } else {
                response.sendError(HttpStatus.FORBIDDEN_403, "Acesso desautorizado");
            }

            baseRequest.setHandled(true);
        }
    }
}
