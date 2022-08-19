package it.unisannio.microserviceapiclient.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionPointDTO {
    private double latitude;
    private double longitude;
}
