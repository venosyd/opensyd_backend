package com.venosyd.open.backend.lib;

import java.util.ArrayList;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.services.seeker.ServiceSeeker;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 * 
 *         Carrega o servlet de acordo com as instrucoes passadas pelo objeto
 *         VenosydService.
 * 
 *         (1) Cada modulo tem direito a uma pastinha dentro de static/ para
 *         arquivos web
 * 
 *         (2) o nome do modulo serve de base para sua chamada, ex:
 *         venosyd.com/login (GET, POST ...) e venosyd.com/login/index.html para
 *         arquivos web
 */
public class ServletContextHandlerBuilder implements Debuggable {

    /**
     * retorna context handler de todos os serviços fornecidos por este servidor
     */
    public static ServletContextHandler build(Service service) {
        return build(service, true);
    }

    /**
     * retorna context handler de todos os serviços fornecidos por este servidor
     */
    public static ServletContextHandler build(Service service, boolean registerInSSK) {
        var servletCtx = new ServletContextHandler();
        servletCtx.setContextPath("/");

        // (1) servidor de arquivos estaticos para website
        var indexFileHolder = new ServletHolder("default", new DefaultServlet());
        indexFileHolder.setInitParameter("resourceBase", "./static/" + service.getUrl() + "/");
        servletCtx.addServlet(indexFileHolder, "/*");

        // (2)
        var servletHolder = new ServletHolder(ServletContainer.class);
        if (service.getRestClass() != null)
            servletHolder.setInitParameter("javax.ws.rs.Application", service.getRestClass());
        servletCtx.addServlet(servletHolder, "/" + service.getUrl() + "/*");

        if (registerInSSK) {
            // a arquitetura modular do servidor backend venosyd consiste em 3 entidades:
            // API Gateway, que redireciona as chamadas, o ServiceSeeker (Seeker)
            // e os modulos. Quando um modulo eh carregado, ele se registra no serviceseeker
            // para caso seja solicitado por algum outro modulo interno ou algum cliente de
            // fora possa ser encontrado
            var privateURL = "http://" + service.getIp() + ":" + service.getPort() + "/" + service.getName();
            ServiceSeeker.register(service.getName(), privateURL, service.getPublicURLs());

            var subs = service.getSubs() != null ? service.getSubs() : new ArrayList<String>();
            for (var sub : subs) {
                var subURL = privateURL + "/" + sub;
                out.tag("SUBSERVICES").tag(sub).ln(ServiceSeeker.register(sub, subURL, service.getPublicURLs()));
            }
        }

        // baboseira protocolar do caralho a quatro
        var filterHolder = servletCtx.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, PUT, POST, DELETE, OPTIONS");
        filterHolder.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        return servletCtx;
    }
}
