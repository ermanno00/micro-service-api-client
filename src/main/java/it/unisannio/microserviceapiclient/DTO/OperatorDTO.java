package it.unisannio.microserviceapiclient.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperatorDTO {
    private String fiscalCode;
    private String name;
    private String surname;
    private String telephoneNumber;
}
