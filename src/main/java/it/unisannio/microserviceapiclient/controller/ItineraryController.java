package it.unisannio.microserviceapiclient.controller;

import it.unisannio.microserviceapiclient.command.ScheduleCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;


@RestController()
@RequestMapping("/itineraries")
@CrossOrigin("*")
public class ItineraryController {

    @Autowired
    private EurekaDiscoveryClient client;

    /**CREO solo gli itinerari  statici che visualizza l amministratore*/
    @PostMapping
    public ResponseEntity<Void> computeStaticItineraries(@RequestBody ScheduleCommand command) {
        ServiceInstance serviceInstance = client.getInstances("itineraries-micro-service-server").get(0);
        return new RestTemplate().postForObject(serviceInstance.getUri()+"/wcomp/api/itineraries", command, ResponseEntity.class);
    }

    /**recupero il file gpx dell'itinerario cercato*/
    @GetMapping(value = "/{itineraryId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> computeGPX(@PathVariable UUID itineraryId) {
        ServiceInstance serviceInstance = client.getInstances("itineraries-micro-service-server").get(0);
        return new RestTemplate().getForObject(serviceInstance.getUri()+"/wcomp/api/itineraries/"+itineraryId.toString(), ResponseEntity.class);
    }


    /**accetto un itinerario per il veicolo, l'operatore lo fa dopo aver ricevuto i vari itinerari
     * oppure completo un itinerario attivo*/
    @PostMapping("/{itineraryId}")
    public ResponseEntity acceptOrCompleteItinerary(@PathVariable UUID itineraryId,
                                                    @RequestParam UUID vehicleId,
                                                    @RequestParam(required = false, defaultValue = "false")Boolean accept,
                                                    @RequestParam(required = false, defaultValue = "false")Boolean complete) {


        ServiceInstance serviceInstance = client.getInstances("itineraries-micro-service-server").get(0);
        return new RestTemplate().postForObject(serviceInstance.getUri()+"/wcomp/api/itineraries/"+itineraryId+"?vehicleId="+vehicleId+"&accept="+accept+"&complete="+complete, null, ResponseEntity.class);
    }


}
