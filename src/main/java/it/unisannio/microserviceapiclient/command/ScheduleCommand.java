package it.unisannio.microserviceapiclient.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCommand {
    public boolean restartVehicle;
    public String typeOfDisposal;

}
