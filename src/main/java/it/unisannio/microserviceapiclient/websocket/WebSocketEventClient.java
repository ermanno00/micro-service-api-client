package it.unisannio.microserviceapiclient.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketEventClient {

    private static WebSocketEventServer myWebSocketEventServer;
    private static MyClient myClient = null;


    public WebSocketEventClient(String url, WebSocketEventServer webSocketEventServer) throws Exception {
        myWebSocketEventServer=webSocketEventServer;
        myClient = new MyClient(new URI(url+"/wcomp/api/events"), true);
    }
    public void run() {
        myClient.run();
    }

    class MyClient {

        private ScheduledExecutorService sss = Executors.newSingleThreadScheduledExecutor();

        MyClient(URI uri, boolean autoReconnect) {
            this.uri = uri;
            this.autoReconnect = autoReconnect;
        }

        private final URI uri;
        private boolean autoReconnect;
        String message;


        void run() {
            try {
                connect();
            } catch (Exception e) {
                log.error(e.getMessage());
                // NOTICE: should not call `reconnect` here, since this exception (from `connectBlocking`) will trigger `onClose` method
                // reconnect();
            }
        }

        private void myReconnect() {
            if (!autoReconnect) {
                return;
            }
            sss.schedule(() -> {
                log.error("WS Events Client - Reconnect");
                MyClient.this.run();
            }, 3, TimeUnit.SECONDS);
        }

        private void connect() throws Exception {
            WebSocketClient client = createClient();
            client.connectBlocking();

//        client.send("{" +
//                "  \"cmd\": \"sub\", " +
//                "  \"args\": [\"ticker.ftusdt\"]" +
//                "}");
        }

        private WebSocketClient createClient() {
            WebSocketClient client = new WebSocketClient(uri, new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    log.info("onOpen");
                }

                @Override
                public void onMessage(String message) {
                    myWebSocketEventServer.sendEvent(message);
                    MyClient.this.message = message;
                }

                @Override
                public void onError(Exception ex) {
                    log.error("onError - "+ex.getMessage());
                    myReconnect();
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info(String.format("onClose(code: %s, reason: %s, remote: %s)", code, reason, remote));
                    myReconnect();
                }
            };
            // If not receive any message from server more than 10s, close the connection
            //client.setConnectionLostTimeout(10);
            return client;
        }
    }


}

