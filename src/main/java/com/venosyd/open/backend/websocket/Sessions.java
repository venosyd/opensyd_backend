package com.venosyd.open.backend.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.venosyd.open.commons.log.Debuggable;

import org.eclipse.jetty.websocket.api.Session;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2020
 * 
 *         Objeto que armazena as sessoes dos websockets abertos. Facilita a
 *         comunicacao entre eles e o servidor
 */
public enum Sessions implements Debuggable {

    INSTANCE;

    /**
     * colecao clientID -> sessao (websocket)
     */
    private Map<String, Session> clientIDSession = new ConcurrentHashMap<>();

    /**
     * colecao websocket -> clientID
     */
    private Map<Session, String> sessionClientID = new ConcurrentHashMap<>();

    public Map<String, Session> getClientIDSession() {
        return clientIDSession;
    }

    public Map<Session, String> getSessionClientID() {
        return sessionClientID;
    }

    /**
     * envia mensagem para um websocket (session) clientIDentificado por seu
     * clientID
     */
    public void sendMessage(String clientID, String message) throws IOException {
        if (isConnected(clientID)) {
            getClientIDSession().get(clientID).getRemote().sendString(message);
        }

        _removeClosedWebSocket(clientID);
    }

    /**
     * manda mensagem pra galerinha toda
     * 
     * ex: login-123abd3bdf362 como clientID de varias e essa funcao pega todas
     * com o nome login no inicio
     */
    public void broadcastMessage(String service, String message) throws IOException {
        List<String> clientIDs = getClientIDSession().keySet().stream().filter(k -> isConnected(k))
                .filter(k -> k.contains(service)).collect(Collectors.toList());

        for (String clientID : clientIDs) {
            getClientIDSession().get(clientID).getRemote().sendString(message);
        }
    }

    /**
     * Verifica se um cliente esta conectado
     */
    public boolean isConnected(String clientID) {
        Session session = getClientIDSession().get(clientID);
        return session != null && session.isOpen();
    }

    /**
     * Verifica se um cliente esta conectado
     */
    public boolean isNotConnected(String clientID) {
        return !isConnected(clientID);
    }

    /**
     * remove do cache websockets desconectados
     */
    private void _removeClosedWebSocket(String clientID) {
        if (isNotConnected(clientID)) {
            getClientIDSession().remove(clientID);

            out._ln_("removing closed websocket: " + clientID);
        }
    }

}
