package eu.ecodex.utils.monitor.keystores.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = CertificateConfigurationProperties.CERTIFICATE_MONITOR_PREFIX)
public class CertificateConfigurationProperties {

    public static final String CERTIFICATE_MONITOR_PREFIX = "monitor.certificates";

    private boolean enabled = false;

    @NestedConfigurationProperty
    List<NamedKeyTrustStore> stores = new ArrayList<>();

    @NestedConfigurationProperty
    List<KeyCheck> keyChecks = new ArrayList<>();


}
