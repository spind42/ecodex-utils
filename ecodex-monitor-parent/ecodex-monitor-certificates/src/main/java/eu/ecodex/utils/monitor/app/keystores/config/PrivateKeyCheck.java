package eu.ecodex.utils.monitor.app.keystores.config;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Duration;

@Data
public class PrivateKeyCheck {

    String checkName;

    String aliasName;

    String keyStoreName;

    /**
     *  The Health check should be warn, if the key expires
     *  in equal or less then warnThreshold duration
     *
     *  if null check should be omitted
     */
    @Nullable
    Duration warnThreshold = Duration.ofDays(60);

    /**
     *  The Health check should fail, if the key expires
     *  in equal or less then errorThreshold duration
     *
     *  if null check should be omitted
     */
    @Nullable
    Duration errorThreshold = Duration.ofDays(30);


}
