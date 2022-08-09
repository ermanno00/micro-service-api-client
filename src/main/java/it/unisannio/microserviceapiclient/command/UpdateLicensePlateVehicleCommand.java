package it.unisannio.microserviceapiclient.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateLicensePlateVehicleCommand {
    @NotNull
    @NotBlank
    private String licensePlate;
}
