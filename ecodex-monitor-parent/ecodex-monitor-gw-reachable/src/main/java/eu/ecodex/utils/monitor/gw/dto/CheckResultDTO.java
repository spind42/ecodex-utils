package eu.ecodex.utils.monitor.gw.dto;

import lombok.Data;

import java.io.PrintWriter;
import java.io.StringWriter;

@Data
public class CheckResultDTO {

    String name;

    String message;

    String details;

    public void writeStackTraceIntoDetails(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        this.setDetails(stringWriter.getBuffer().toString());
    }

}
