package eu.ecodex.utils.monitor.gw.service;


import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.dto.AccessPointStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint(id = "gateways")
public class GatewayReachableEndpoint {

    @Autowired
    ConfiguredGatewaysService configuredGatewaysService;

    @Autowired
    GatewaysCheckerService checkerService;

    @ReadOperation
    List<AccessPointStatusDTO> accessPointStatusList() {
        ArrayList<AccessPointStatusDTO> apStatus = new ArrayList<>();
        return configuredGatewaysService
                .getConfiguredGatewaysWithSelf()
                .stream()
                .map(ap -> checkerService.getGatewayStatus(ap))
                .collect(Collectors.toList());
    }


    @ReadOperation
    public AccessPointStatusDTO getStoreEntryInfo(@Selector String endpointName) {
        AccessPointStatusDTO dto = new AccessPointStatusDTO();
        AccessPoint byName = configuredGatewaysService.getByName(endpointName);
        if (byName == null) {
            return dto;
        }
        return checkerService.getGatewayStatus(byName);
    }


}
