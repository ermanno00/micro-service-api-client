package it.unisannio.microserviceapiclient.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {

    private UUID id;
    private String friendlyName;
    private String licensePlate;
    private int length;
    private int height;
    private int depth;
    private int capacity;
    private String state;
}
