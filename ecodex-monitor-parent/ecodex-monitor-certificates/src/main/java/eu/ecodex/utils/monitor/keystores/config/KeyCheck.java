package eu.ecodex.utils.monitor.keystores.config;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Duration;

@Data
public class KeyCheck {

    String checkName;

    String aliasName;

    String storeName;

    /**
     * If enabled it will be checked if the given alias
     * is also a private key
     */
    boolean shouldBePrivateKey = false;

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

    /**
     * should the certificate be validated?
     */
    boolean enableValidation = true;

    /**
     * Should the default java trust store also be used to validate certificate?
     *   usually this store is located under $JAVA_HOME/jre/lib/security/cacerts
     *   and can be set by -Djavax.net.ssl.trustStore=...
     */
    boolean useSystemTrustStore = true;

    boolean useCrl = true;

    boolean useOcsp = true;


}
