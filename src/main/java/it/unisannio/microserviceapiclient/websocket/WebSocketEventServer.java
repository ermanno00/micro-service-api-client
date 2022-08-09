package it.unisannio.microserviceapiclient.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import it.unisannio.microserviceapiclient.model.Event;
import it.unisannio.microserviceapiclient.model.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
@Component
@ServerEndpoint(value = "/events")
public class WebSocketEventServer {


    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private static Set<Session> sessions = new HashSet<>();



    public void sendEvent(String message) {

        executor.submit(() -> {

            for(Session s: this.sessions) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch ( IOException e) {
                    log.error(e.getMessage());
                }
            }

        });

    }


    @OnOpen
    public void start(Session session)  throws IOException {

        this.sessions.add(session);
        session.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(new Event(EventType.INFO, "In ascolto di eventi...")));
    }

    /**
     * Viene rimossa la sessione dalla lista delle sessioni
     */
    @OnClose
    public void end(Session session) {

        this.sessions.remove(session);
        log.info(session.getId() + " disconnected");
    }

    @OnMessage
    public void receive(String message, Session session) {
        log.info("Received message from "+session.getId()+": "+message);
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        log.error(t.getMessage());
    }

}
