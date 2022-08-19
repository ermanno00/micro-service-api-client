package it.unisannio.microserviceapiclient.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public void sendNewData(String message){

    }


//
//    public void sendItineraries(Collection<Itinerary> itineraryList, Session session) {
//        executor.submit(() -> {
//            List<ItineraryWebSocket> itineraryWebSockets = convert(itineraryList);
//            try {
//                session.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(itineraryWebSockets));
//            } catch (IOException e) {
//                log.error(e.getMessage());
//            }
//        });
//    }
//
//    private List<ItineraryWebSocket> convert(Collection<Itinerary>itineraryList){
//        return itineraryList.stream().map(i ->
//                new ItineraryWebSocket(
//                        i.getId().getId(),
//                        i.getTimestamp(),
//                        i.getVehicleId().getId(),
//                        i.getCoordinates().stream()
//                                .map(c -> new MapLatLng(c.getLatitude(), c.getLongitude()))
//                                .collect(Collectors.toList()),
//                        i.getCost(),
//                        i.getState().name()
//                )
//        ).collect(Collectors.toList());
//
//    }
//
//
//    public void sendItineraries(Collection<Itinerary> itineraryList) {
//        executor.submit(() -> {
//            List<ItineraryWebSocket> itineraryWebSockets = convert(itineraryList);
//            Map<UUID,List<ItineraryWebSocket>>itineraries = new HashMap<>();
//            itineraryWebSockets.forEach(itineraryWebSocket -> {
//                List<ItineraryWebSocket>buffer;
//                if(itineraries.containsKey(itineraryWebSocket.getVehicleId())){
//                    buffer= itineraries.get(itineraryWebSocket.getVehicleId());
//                }else{
//                    buffer= new ArrayList<>();
//                    itineraries.put(itineraryWebSocket.getVehicleId(),buffer);
//                }
//                buffer.add(itineraryWebSocket);
//            });
//
//            for(Vehicle.VehicleId vehicleId: sessions.keySet()){
//                if(itineraries.containsKey(vehicleId.getId())){
//                    try {
//                        sessions.get(vehicleId)
//                                .getBasicRemote()
//                                .sendText(new ObjectMapper()
//                                        .writeValueAsString(itineraries.get(vehicleId.getId()))
//                                );
//                    } catch (IOException e) {
//                        log.error(e.getMessage());
//                    }
//                }
//            }
//        });
//    }
//
//    @OnOpen
//    public void start(Session session) {
//        Vehicle.VehicleId vehicleId = new Vehicle.VehicleId(UUID.fromString(session.getPathParameters().get("vehicleId")));
//        sessions.put(vehicleId, session);
//        log.info("vehicle with id: " + session.getPathParameters().get("vehicleId") + " connected");
//        Set<Itinerary> itineraries = dynamicStructure.getItineraries().stream().collect(Collectors.toSet());
//        itineraries.addAll(staticStructure.getItineraries());
//        itineraries = itineraries.stream()
//                .filter(itinerary -> itinerary.getVehicleId().equals(vehicleId))
//                .filter(itinerary -> itinerary.getState().equals(ItineraryState.ASSIGNED) || itinerary.getState().equals(ItineraryState.RUNNING))
//                .collect(Collectors.toSet());
//        this.sendItineraries(itineraries, session);
//    }
//
//
//    /**
//     * Viene rimossa la sessione dalla lista delle sessioni
//     */
//    @OnClose
//    public void end(Session session) {
//        Vehicle.VehicleId idToRemove = null;
//        for (Vehicle.VehicleId id : sessions.keySet()) {
//            if (sessions.get(id).equals(session)) {
//                idToRemove = id;
//                break;
//            }
//        }
//        if (idToRemove != null)
//            sessions.remove(idToRemove);
//        log.info(session.getId() + " disconnected");
//    }

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
