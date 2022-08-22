package it.unisannio.microserviceapiclient.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketEventClient {

    private static WebSocketEventServer myWebSocketEventServer;

    protected static String message;

    private static ScheduledExecutorService sss = Executors.newSingleThreadScheduledExecutor();

    private static WebSocketClient client;

    public static boolean connected = false;


    public WebSocketEventClient(String url, WebSocketEventServer webSocketEventServer) throws Exception {
        myWebSocketEventServer = webSocketEventServer;
        URI uri = new URI(url);

       this.client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {

                log.info("Connected to Event WebSocket");
                connected = true;
            }

            @Override
            public void onMessage(String message) {
                log.info("Received event message from Event WS");
                myWebSocketEventServer.sendEvent(message);
                WebSocketEventClient.message = message;
            }

            @Override
            public void onError(Exception ex) {
                log.error("onError - " + ex.getMessage());
                connected = false;
                try {
                    myWebSocketEventServer.endAll(new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, ex.getMessage()));
                } catch (IOException ex1) {
                    throw new RuntimeException(ex1);
                }
                myReconnect();

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info(String.format("onClose(code: %s, reason: %s, remote: %s)", code, reason, remote));
                connected = false;
                try {
                    myWebSocketEventServer.endAll(new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, reason));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                myReconnect();
            }
        };
    }



    private void myReconnect() {
        sss.schedule(() -> {
            log.error("WS Events Client - Reconnect");
            try {
                client.reconnectBlocking();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }, 15, TimeUnit.SECONDS);
    }

    public void connect()  {
        try {
            log.info("Connecting");
            client.connectBlocking();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }



}

