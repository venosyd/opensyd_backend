package com.venosyd.open.backend.websocket;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author sergio e. lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 */
@ApplicationPath(WebSocketRS.WEBSOCKET_BASE_URI)
public class WebSocketRESTfulAPI extends Application {

    public Set<Class<?>> getClasses() {
        var classes = new HashSet<Class<?>>();

        classes.add(WebSocketRSImpl.class);

        return classes;
    }
}
