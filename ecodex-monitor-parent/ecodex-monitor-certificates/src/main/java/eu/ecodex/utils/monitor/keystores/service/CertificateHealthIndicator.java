package eu.ecodex.utils.monitor.keystores.service;

import eu.ecodex.utils.monitor.keystores.config.CertificateConfigurationProperties;
import eu.ecodex.utils.monitor.keystores.config.KeyCheck;

import eu.ecodex.utils.monitor.keystores.dto.StoreEntryInfo;
import eu.ecodex.utils.monitor.keystores.service.crtprocessor.X509CertificateToStoreEntryInfoProcessorImpl;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

public class CertificateHealthIndicator extends AbstractHealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateHealthIndicator.class);

    @Autowired
    CertificateConfigurationProperties crtCheckConfig;

    @Autowired
    KeyService keyService;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.unknown();
        if (crtCheckConfig.getKeyChecks().size() == 0) {
            builder.withDetail("message", "No key checks configured");
        }
        crtCheckConfig.getKeyChecks()
                .stream()
                .forEach(check -> this.keyCheck(check, builder));

    }

    private void keyCheck(KeyCheck check, Health.Builder builder) {
        String alias = check.getAliasName();
        String storeName = check.getStoreName();
        String checkName = String.format("Check_%s@%s_%s", storeName, alias, check.getCheckName());
        StoreEntryInfo storeEntryInfo = keyService.getStoreEntryInfo("*", storeName, alias);

        if (storeEntryInfo.getPresent() == false) {
            builder.unknown();
            builder.withDetail(checkName + "_message", String.format("No certificate found in store %s with alias %s", check.getStoreName(), check.getAliasName()));
            return;
        }
        builder.up();
        checkNotBefore(checkName, storeEntryInfo, builder);
        checkNotAfter(check, checkName, storeEntryInfo, builder);
        checkValidation(check, checkName, storeEntryInfo, builder);


    }

    private void checkValidation(KeyCheck check, String checkName, StoreEntryInfo storeEntryInfo, Health.Builder builder) {
        LOGGER.debug("#checkValidation: Checking certificate validation");

        if (X509CertificateToStoreEntryInfoProcessorImpl.X509CertName.equals(storeEntryInfo.getCertificateType()) &&
                storeEntryInfo.getCertificate() != null
        ) {
//            try {
//                X509CertificateHolder certificateHolder = new X509CertificateHolder(storeEntryInfo.getCertificate());

            //TODO: do validation check...

//            } catch (IOException e) {
//                LOGGER.error("IOException occured while reading X509 certificate", e);
//            }

        }

    }

    private void checkNotAfter(KeyCheck check, String checkName, StoreEntryInfo storeEntryInfo, Health.Builder builder) {
        checkName = checkName + "_not_after";
        builder.withDetail(checkName + "_date", storeEntryInfo.getNotAfter());
        builder.withDetail(checkName + "_error_threshold", check.getErrorThreshold());

        if (storeEntryInfo.getNotAfter() == null) {
            builder.withDetail(checkName + "_not_after_message", "Was not able to check not before, because there is no not before information available");
            return;
        }

        Instant notAfterWarn = storeEntryInfo.getNotAfter().toInstant();
        notAfterWarn.plus(check.getWarnThreshold());
        Instant notAfterError = storeEntryInfo.getNotAfter().toInstant();
        notAfterError.plus(check.getErrorThreshold());

        if (notAfterWarn.isBefore(Instant.now())) {
            LOGGER.warn("{}: Not after check is warn!", checkName);
            builder.withDetail(checkName + "_not_after_message",  "Has failed because warn threshold for cert expiration has been reached");
            builder.withDetail(checkName + "_not_after_state", "warn");
            builder.down();
        }  else {
            builder.withDetail(checkName + "_not_after_message", "Is ok");
            builder.withDetail(checkName + "_not_after_state", "ok");
        }

        if (notAfterError.isBefore(Instant.now())) {
            LOGGER.error("{}: Not after check has failed!", checkName);
            builder.withDetail(checkName + "_not_after_message", "Has failed because error threshold for cert expiration has been reached");
            builder.withDetail(checkName + "_not_after_state", "failed");
            builder.down();
        } else {
            LOGGER.info("{}: Not after check is valid or warn!", checkName);
        }
    }

    private void checkNotBefore(String checkName, StoreEntryInfo storeEntryInfo, Health.Builder builder) {
        builder.withDetail(checkName + "_not_before_date", storeEntryInfo.getNotBefore());
        if (storeEntryInfo.getNotBefore() == null) {
            builder.withDetail(checkName + "_not_before_message", "Was not able to check not before, because there is no not before information available");
        } else if (storeEntryInfo.getNotBefore().before(new Date())) {
            LOGGER.info("{}: Not before check is valid!", checkName);
            builder.withDetail(checkName + "_not_before_message", "Is ok");
        } else {
            LOGGER.info("{}: Not before check is failed!", checkName);
            builder.withDetail(checkName + "_not_before_message", "Is failed");
            builder.down();
        }
    }


}
