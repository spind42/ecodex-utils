package eu.ecodex.utils.monitor.gw;

import eu.ecodex.utils.monitor.app.MonitorAppConfiguration;

public class GatewayMonitorAppStarter {

    public static void main(String[] args) {
        MonitorAppConfiguration app = new MonitorAppConfiguration();
        app.run(args);
    }
}
