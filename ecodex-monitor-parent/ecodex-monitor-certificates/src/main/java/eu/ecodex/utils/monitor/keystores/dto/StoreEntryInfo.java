package eu.ecodex.utils.monitor.keystores.dto;

import lombok.Data;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class StoreEntryInfo {

    String aliasName;

    String certificateType;

    int versionNumber;

    String issuerName;

    String subject;

    BigInteger serialNumber;

    Date notAfter;

    Date notBefore;

    Boolean present = false;

    byte[] certificate;

    //TODO: certificate attributes

}
