package it.unisannio.microserviceapiclient.controller;

import io.swagger.v3.oas.annotations.Operation;
import it.unisannio.microserviceapiclient.DTO.OperatorDTO;
import it.unisannio.microserviceapiclient.DTO.VehicleDTO;
import it.unisannio.microserviceapiclient.command.CreateOperatorCommand;
import it.unisannio.microserviceapiclient.command.CreateVehicleCommand;
import it.unisannio.microserviceapiclient.command.UpdateLicensePlateVehicleCommand;
import it.unisannio.microserviceapiclient.command.UpdateTelephoneNumberCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/vehicles")
public class VehicleClientController {

    @Autowired
    private EurekaDiscoveryClient client;

    @Operation(description = "add an vehicle")
    @PostMapping
    public void get(@Valid @RequestBody CreateVehicleCommand command) {
        ServiceInstance serviceInstance = client.getInstances("vehicles-micro-service-server").get(0);
        new RestTemplate().postForLocation(serviceInstance.getUri()+"/wcomp/api/vehicles", command);
    }

    @Operation(description = "get a List of Vehicles")
    @GetMapping
    public List<VehicleDTO> get() {
        ServiceInstance serviceInstance = client.getInstances("vehicles-micro-service-server").get(0);
        return Arrays.asList(new RestTemplate().getForObject(serviceInstance.getUri()+"/wcomp/api/vehicles", VehicleDTO[].class));
    }

    @Operation(description = "get Vehicle from UUID")
    @GetMapping("/{id}")
    public VehicleDTO get(@PathVariable("id") String uuid) {
        ServiceInstance serviceInstance = client.getInstances("vehicles-micro-service-server").get(0);
        return new RestTemplate().getForObject(serviceInstance.getUri()+"/wcomp/api/vehicles/"+uuid, VehicleDTO.class);

    }

    @Operation(description = "delete Vehicle by UUID")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String uuid) {
        ServiceInstance serviceInstance = client.getInstances("vehicles-micro-service-server").get(0);
        new RestTemplate().delete(serviceInstance.getUri()+"/wcomp/api/vehicles/"+uuid);
    }

    @Operation(description = "update License Plate of a Vehicle identified by his UUID")
    @PutMapping("/{id}")
    public void update(@PathVariable("id") String uuid, @Valid @RequestBody UpdateLicensePlateVehicleCommand command) {
        ServiceInstance serviceInstance = client.getInstances("vehicles-micro-service-server").get(0);
        new RestTemplate().postForLocation(serviceInstance.getUri()+"/wcomp/api/vehicles/"+uuid, command);
    }




}
