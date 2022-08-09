package it.unisannio.microserviceapiclient.command;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateVehicleCommand {
    @NotNull
    @NotBlank
    private String friendlyName;
    @NotNull
    @NotBlank
    private String licensePlate;
    @NotNull
    @Min(value = 0, message = "length can't be less than 0")
    private Integer length;
    @NotNull
    @Min(value = 0, message = "height can't be less than 0")
    private Integer height;
    @NotNull
    @Min(value = 0, message = "depth can't be less than 0")
    private Integer depth;
    @NotNull
    @Min(value = 0, message = "capacity can't be less than 0")
    private Integer capacity;

}
