package com.venosyd.open.backend.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.util.JSONUtil;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 * 
 *         Objeto que carrega e armazena as informacoes dos servicos venosyd
 */
public class Service implements Debuggable {

    /** */
    private String name;

    /** */
    private String restClass;

    /** */
    private String url;

    /** */
    private String ip;

    /** */
    private int port;

    /** */
    private List<String> subs;

    /** */
    private List<String> publicURLs;

    /** construtor estatico */
    public static Service build(String name) {
        try {
            var service = ServicesLoader.INSTANCE.<Map<String, Object>>get(name);
            return new Service(name, service);
        } catch (Exception e) {
            err.exception("BUILDING SERVICE OBJ", e);

            var service = new Service();

            service.setName("EMPTY SERVICE");
            service.setRestClass("");
            service.setUrl("0.0.0.0");
            service.setPort(9999);
            service.setSubs(new ArrayList<>());
            service.setPublicURLs(new ArrayList<>());

            return service;
        }
    }

    public Service() {
    }

    @SuppressWarnings("unchecked")
    public Service(String name, Map<String, Object> service) {
        this.name = name;
        this.restClass = service.containsKey("class") ? (String) service.get("class") : "";
        this.url = (String) service.get("url");
        this.ip = (String) service.get("ip");
        this.port = Integer.parseInt((String) service.get("port"));
        this.subs = (List<String>) service.get("subs");
        this.publicURLs = (List<String>) service.get("public_urls");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Service))
            return false;
        Service service = (Service) o;
        return Objects.equals(getName(), service.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return JSONUtil.toJSON(this);
    }

    public List<String> getSubs() {
        return subs;
    }

    public void setSubs(List<String> subs) {
        this.subs = subs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestClass() {
        return restClass;
    }

    public void setRestClass(String restClass) {
        this.restClass = restClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getPublicURLs() {
        return publicURLs;
    }

    public void setPublicURLs(List<String> publicURLs) {
        this.publicURLs = publicURLs;
    }

}