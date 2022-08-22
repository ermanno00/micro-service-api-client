package it.unisannio.microserviceapiclient.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Component
@ServerEndpoint(value = "/{vehicleId}")
@Slf4j
public class WebSocketOperatorServer {

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private static Map<UUID, Session> sessions = new HashMap<>();

    //Metodo chiamato dal client quando riceve un nuovo messaggio
    public void onNewData(String message){
        if(deserialize(message).isPresent()) this.sendItineraries(deserialize(message).get());
    }

    private Optional<List<ItineraryWebSocket>> deserialize(String message){
        if(message == null) return Optional.ofNullable(null);
        try {
            return Optional.ofNullable(Arrays.asList(new ObjectMapper().readValue(message, ItineraryWebSocket[].class)));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return Optional.ofNullable(null);
    }




    private void sendItineraries(Collection<ItineraryWebSocket> itineraryWebSockets, Session session) {
        executor.submit(() -> {
            itineraryWebSockets.stream()
                    .filter(itineraryWebSocket -> itineraryWebSocket.getVehicleId().equals(session.getPathParameters().get("vehicleId")))
                    .collect(Collectors.toList());
            try {
                session.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(itineraryWebSockets));
                log.info("Sent "+itineraryWebSockets.size()+" itineraries at vehicle "+session.getPathParameters().get("vehicleId"));

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }



    private void sendItineraries(Collection<ItineraryWebSocket> itineraryWebSockets) {
        executor.submit(() -> {
            Map<UUID,List<ItineraryWebSocket>>itineraries = new HashMap<>();
            itineraryWebSockets.forEach(itineraryWebSocket -> {
                List<ItineraryWebSocket>buffer;
                if(itineraries.containsKey(itineraryWebSocket.getVehicleId())){
                    buffer= itineraries.get(itineraryWebSocket.getVehicleId());
                }else{
                    buffer= new ArrayList<>();
                    itineraries.put(itineraryWebSocket.getVehicleId(),buffer);
                }
                buffer.add(itineraryWebSocket);
            });

            for(UUID vehicleId: sessions.keySet()){
                if(itineraries.containsKey(vehicleId)){
                    try {
                        sessions.get(vehicleId)
                                .getBasicRemote()
                                .sendText(new ObjectMapper()
                                        .writeValueAsString(itineraries.get(vehicleId))
                                );
                        log.info("Sent "+itineraries.get(vehicleId).size()+" itineraries at vehicle "+sessions.get(vehicleId));

                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        });
    }

    @OnOpen
    public void start(Session session) {
        UUID vehicleId = (UUID.fromString(session.getPathParameters().get("vehicleId")));
        sessions.put(vehicleId, session);
        log.info("vehicle with id: " + session.getPathParameters().get("vehicleId") + " connected");


        Set<ItineraryWebSocket> itineraries = new HashSet<>();

        if(deserialize(WebSocketAdminClientDynamic.message).isPresent()) itineraries.addAll(deserialize(WebSocketAdminClientDynamic.message).get());
        if(deserialize(WebSocketAdminClientStatic.message).isPresent()) itineraries.addAll(deserialize(WebSocketAdminClientStatic.message).get());
        itineraries = itineraries.stream()
                .filter(itinerary -> itinerary.getVehicleId().equals(vehicleId))
                .filter(itinerary -> itinerary.getState().equals("ASSIGNED") || itinerary.getState().equals("RUNNING"))
                .collect(Collectors.toSet());
        this.sendItineraries(itineraries, session);

    }


    /**
     * Viene rimossa la sessione dalla lista delle sessioni
     */
    @OnClose
    public void end(Session session) {
        UUID idToRemove = null;
        for (UUID id : sessions.keySet()) {
            if (sessions.get(id).equals(session)) {
                idToRemove = id;
                break;
            }
        }
        if (idToRemove != null)
            sessions.remove(idToRemove);
        log.info("vehicle with id: " + session.getPathParameters().get("vehicleId") + " disconnected");
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
    }

    public void endAll(CloseReason closeReason) throws IOException {
        for(Session session: sessions.values()){
            session.close(closeReason);
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItineraryWebSocket {
        private UUID id;
        private long timestamp;
        private UUID vehicleId;
        private List<MapLatLng> coordinates = new LinkedList<>();
        private double cost;
        private String state;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MapLatLng {
        private double latitude;
        private double longitude;
    }
}
