package it.unisannio.microserviceapiclient.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateOperatorCommand {

    @NotNull
    @NotBlank
    private String fiscalCode;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String surname;
    @NotNull
    @NotBlank
    private String telephoneNumber;

}
