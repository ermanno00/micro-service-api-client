package it.unisannio.microserviceapiclient.controller;

import it.unisannio.microserviceapiclient.DTO.CollectionPointDTO;
import it.unisannio.microserviceapiclient.DTO.OperatorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/collectionPoints")
@CrossOrigin("*")
public class CollectionPointController {

    @Autowired
    private EurekaDiscoveryClient client;


    @GetMapping
    public List<CollectionPointDTO> getAll(@RequestParam(required = false, defaultValue = "false")boolean depot,
                                           @RequestParam(required = false, defaultValue = "false")boolean rubbishDump){
        ServiceInstance serviceInstance = client.getInstances("itineraries-micro-service-server").get(0);
        return Arrays.asList(new RestTemplate().getForObject(serviceInstance.getUri()+"/wcomp/api/collectionPoints?depot="+depot+"&rubbishDump="+rubbishDump, CollectionPointDTO[].class));

    }
}
