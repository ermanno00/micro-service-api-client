package it.unisannio.microserviceapiclient.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItineraryDTO {
    private UUID id;
    private long timestamp;
    private UUID vehicleId;
    private List<CoordinatesDTO> coordinatesDTO;
}
