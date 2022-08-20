package it.unisannio.microserviceapiclient;

import it.unisannio.microserviceapiclient.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitApplication implements CommandLineRunner {

    @Autowired
    private EurekaDiscoveryClient client;

    @Autowired
    private WebSocketEventServer webSocketEventServer;

    @Autowired
    private WebSocketAdminServer webSocketAdminServer;

    @Override
    public void run(String... args) {


        String wsUri = "ws://"+client.getInstances("itineraries-micro-service-server").get(0).getHost()+":"+client.getInstances("itineraries-micro-service-server").get(0).getPort();

        try {
            WebSocketEventClient ws = new WebSocketEventClient(wsUri + "/wcomp/api/events", webSocketEventServer);
            ws.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            WebSocketAdminClientStatic staticws = new WebSocketAdminClientStatic(wsUri + "/wcomp/api/itineraries/static", webSocketAdminServer);
            staticws.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            WebSocketAdminClientDynamic dynamicws = new WebSocketAdminClientDynamic(wsUri + "/wcomp/api/itineraries/dynamic", webSocketAdminServer);
            dynamicws.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            WebSocketAdminClientCompleted completedws = new WebSocketAdminClientCompleted(wsUri + "/wcomp/api/itineraries/completed", webSocketAdminServer);
            completedws.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
