package it.unisannio.microserviceapiclient;

import it.unisannio.microserviceapiclient.websocket.WebSocketEventClient;
import it.unisannio.microserviceapiclient.websocket.WebSocketEventServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

@Component
public class InitApplication implements CommandLineRunner {

    @Autowired
    private EurekaDiscoveryClient client;

    @Autowired
    private WebSocketEventServer webSocketEventServer;

    @Override
    public void run(String... args) {


        String wsUri = "ws://"+client.getInstances("itineraries-micro-service-server").get(0).getHost()+":"+client.getInstances("itineraries-micro-service-server").get(0).getPort();

        try {
            WebSocketEventClient ws = new WebSocketEventClient(wsUri, webSocketEventServer);
            ws.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
