package eu.ecodex.utils.monitor.gw.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ConfiguredGatewaysITCase {


    @Autowired
    ConfiguredGatewaysService configuredGateways;


}