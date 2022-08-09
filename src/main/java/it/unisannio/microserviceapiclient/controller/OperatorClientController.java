package it.unisannio.microserviceapiclient.controller;

import io.swagger.v3.oas.annotations.Operation;
import it.unisannio.microserviceapiclient.DTO.OperatorDTO;
import it.unisannio.microserviceapiclient.command.CreateOperatorCommand;
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
@RequestMapping("/operators")
public class OperatorClientController {

    @Autowired
    private EurekaDiscoveryClient client;

    @Operation(description = "add an operator")
    @PostMapping
    public void get(@Valid @RequestBody CreateOperatorCommand command) {
        ServiceInstance serviceInstance = client.getInstances("operators-micro-service-server").get(0);
        new RestTemplate().postForLocation(serviceInstance.getUri()+"/wcomp/api/operators", command);
    }

    @Operation(description = "delete an operator")
    @DeleteMapping("/{fiscalCode}")
    public void delete(@PathVariable("fiscalCode") String fiscalCode) {
        ServiceInstance serviceInstance = client.getInstances("operators-micro-service-server").get(0);
        new RestTemplate().delete(serviceInstance.getUri()+"/wcomp/api/operators/"+fiscalCode);
    }

    @Operation(description = "update telephone number of an operator")
    @PutMapping("/{fiscalCode}")
    public void update(@PathVariable("fiscalCode") String fiscalCode, @Valid @RequestBody UpdateTelephoneNumberCommand command) {
        ServiceInstance serviceInstance = client.getInstances("operators-micro-service-server").get(0);
        new RestTemplate().postForLocation(serviceInstance.getUri()+"/wcomp/api/operators/"+fiscalCode, command);
    }

    @Operation(description = "get an operator by fiscal code")
    @GetMapping("/{fiscalCode}")
    public OperatorDTO get(@PathVariable("fiscalCode") String fiscalCode) {
        ServiceInstance serviceInstance = client.getInstances("operators-micro-service-server").get(0);
        return new RestTemplate().getForObject(serviceInstance.getUri()+"/wcomp/api/operators/"+fiscalCode, OperatorDTO.class);

    }

    @Operation(description = "get a list of all operators")
    @GetMapping
    public List<OperatorDTO> get() {
        ServiceInstance serviceInstance = client.getInstances("operators-micro-service-server").get(0);
        return Arrays.asList(new RestTemplate().getForObject(serviceInstance.getUri()+"/wcomp/api/operators", OperatorDTO[].class));
    }
}
