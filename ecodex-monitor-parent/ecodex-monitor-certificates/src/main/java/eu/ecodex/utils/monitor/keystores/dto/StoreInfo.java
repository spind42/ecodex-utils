package eu.ecodex.utils.monitor.keystores.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
public class StoreInfo {

    String name;

    @Nullable
    String location;

    @Nullable
    String configuredLocation;

    @Nullable
    String type;

    @Nullable
    Boolean readable;

    @Nullable
    Boolean writeable;

    @Nullable
    String message;

    List<StoreEntryInfo> storeEntries = new ArrayList<>();

}
