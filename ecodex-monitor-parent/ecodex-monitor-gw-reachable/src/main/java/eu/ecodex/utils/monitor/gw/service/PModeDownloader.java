package eu.ecodex.utils.monitor.gw.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.domibus.ext.domain.PModeArchiveInfoDTO;
import eu.ecodex.configuration.pmode.Configuration;
import eu.ecodex.utils.monitor.gw.config.GatewayRestInterfaceConfiguration;
import eu.ecodex.utils.monitor.gw.domain.AccessPoint;
import eu.ecodex.utils.monitor.gw.domain.AccessPointsConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PModeDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PModeDownloader.class);

    private final GatewayRestInterfaceConfiguration gatewayRestInterfaceConfiguration;

    private RestTemplate restTemplate;
    private ConfigurationWrapper wrappedConfiguration = new ConfigurationWrapper();

    public PModeDownloader(@Autowired GatewayRestInterfaceConfiguration gatewayRestInterfaceConfiguration) {
        this.gatewayRestInterfaceConfiguration = gatewayRestInterfaceConfiguration;
        this.restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(gatewayRestInterfaceConfiguration.getUrl()))
                .basicAuthentication(gatewayRestInterfaceConfiguration.getUsername(), gatewayRestInterfaceConfiguration.getPassword())
                .build();
    }


    public Configuration downloadPModes() {
        return downloadNewPModes(-1).config;
    }

    public ConfigurationWrapper downloadNewPModes(int id) {
        String lineSeperator = "\n\n####################";

        ResponseEntity<String> currentPMode = restTemplate.getForEntity("/rest/pmode/current", String.class);
        String body = currentPMode.getBody();
        //gateway returns json with leading )]}, so replace it with nothing
        body = body.replaceFirst("\\)]}',", "");

        int pmodeId = -1;
        LOGGER.debug("Retrieved json [{}]", body);
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PModeArchiveInfoDTO pModeArchiveInfoDTO = mapper.readValue(body, PModeArchiveInfoDTO.class);
            pmodeId = pModeArchiveInfoDTO.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (pmodeId > id) {
            ResponseEntity<String> forEntity = restTemplate
                    .getForEntity("/rest/pmode/" + pmodeId , String.class);

            String xmlString = forEntity.getBody();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Downloaded pmode:" + lineSeperator + xmlString + lineSeperator);
            }

            JAXBContext jaxbContext;
            try
            {
                jaxbContext = JAXBContext.newInstance(Configuration.class);

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                Configuration configuration = (Configuration) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
                this.wrappedConfiguration.config = configuration;
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Downloaded pmode: " + lineSeperator + this.wrappedConfiguration + lineSeperator);
                }

            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }
        return this.wrappedConfiguration;
    }

    public AccessPointsConfiguration updateAccessPointsConfig(AccessPointsConfiguration config) {
        ConfigurationWrapper wrappedConfiguration = this.downloadNewPModes(config.getId());
        Configuration conf = wrappedConfiguration.config;
        String selfParty = conf.getParty();

        Map<String, AccessPoint> aps =  conf
                .getBusinessProcesses()
                .getParties().getParty().stream()
                .map(this::mapMpc)
                .collect(Collectors.toMap(AccessPoint::getName, Function.identity()));

        AccessPoint self = aps.remove(selfParty);
        config.setSelf(self);
        config.setRemoteAccessPoints(aps.values());
        config.setId(wrappedConfiguration.id);
        return config;
    }

    private AccessPoint mapMpc(Configuration.BusinessProcesses.Parties.Party party) {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setName(party.getName());
        accessPoint.setEndpoint(party.getEndpoint());
        return accessPoint;
    }

    private static class ConfigurationWrapper {
        Configuration config;
        int id = -1;
    }

}



