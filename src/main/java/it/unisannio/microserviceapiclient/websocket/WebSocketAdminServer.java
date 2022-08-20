package it.unisannio.microserviceapiclient.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Component
@ServerEndpoint(value = "/itineraries/{type}")
public class WebSocketAdminServer {

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private static Set<Session> staticSessions = new HashSet<>();
    private static Set<Session> dynamicSessions = new HashSet<>();
    private static Set<Session> completedSessions = new HashSet<>();


    public void sendItineraries(String message, Type type) {
        executor.submit(() -> {
            switch (type) {
                case STATIC:
                    staticSessions.forEach(session -> {
                        try {
                            session.getBasicRemote().sendText(message);
                            log.info("Sent some " +session.getPathParameters().get("type")+" itineraries at user "+session.getId());
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
                    break;
                case DYNAMIC:
                    dynamicSessions.forEach(session -> {
                        try {
                            session.getBasicRemote().sendText(message);
                            log.info("Sent some " +session.getPathParameters().get("type")+" itineraries at user "+session.getId());
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
                    break;
                case COMPLETED:
                    completedSessions.forEach(session -> {
                        try {
                            session.getBasicRemote().sendText(message);
                            log.info("Sent some " +session.getPathParameters().get("type")+" itineraries at user "+session.getId());
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
                    break;
                default:
                    break;

            }


        });


    }

    public void sendItineraries(String message, Session session) {
        executor.submit(() -> {
            try {
                session.getBasicRemote().sendText(message);
                log.info("Sent some " +session.getPathParameters().get("type")+" itineraries at user "+session.getId());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }


    @OnOpen
    public void start(Session session) {
        if (session.getPathParameters().containsValue("static")) {
            log.info("User " + session.getId() + " connected to the the static scheduling websocket");
            staticSessions.add(session);
            sendItineraries(WebSocketAdminClientStatic.message, session);
        }
        if (session.getPathParameters().containsValue("completed")) {
            log.info("User " + session.getId() + " connected to the the complete scheduling websocket");
            completedSessions.add(session);
            sendItineraries(WebSocketAdminClientCompleted.message, session);
        }
        if (session.getPathParameters().containsValue("dynamic")) {
            dynamicSessions.add(session);
            log.info("User " + session.getId() + " connected to the the dynamic scheduling websocket");
            sendItineraries(WebSocketAdminClientDynamic.message, session);
        }
    }


    /**
     * Viene rimossa la sessione dalla lista delle sessioni
     */
    @OnClose
    public void end(Session session) {
        staticSessions.remove(session);
        dynamicSessions.remove(session);
        completedSessions.remove(session);
        log.info("User: "+session.getId() + " disconnected");
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
    }

    public void endAll(CloseReason closeReason, Type type) throws IOException {

        switch (type) {
            case STATIC:
                staticSessions.forEach(session -> {
                    try {
                        session.close(closeReason);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
                break;
            case DYNAMIC:
                dynamicSessions.forEach(session -> {
                    try {
                        session.close(closeReason);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
                break;
            case COMPLETED:
                completedSessions.forEach(session -> {
                    try {
                        session.close(closeReason);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
                break;
            default:
                break;

        }

    }

    enum Type {
        STATIC,
        DYNAMIC,
        COMPLETED
    }

}
