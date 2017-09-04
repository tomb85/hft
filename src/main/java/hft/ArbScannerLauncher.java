package hft;

import org.apache.log4j.Logger;

public class ArbScannerLauncher {

    private static final Logger LOG = Logger.getLogger(ArbScannerLauncher.class);

    public static void main(String[] args) {
        try {
            OrderBookManager orderBookManager = new OrderBookManager(new ArbCalculator());
            new WebsocketMarketDataReceiver(orderBookManager);
        } catch (Exception e) {
            LOG.error("Unhandled exception", e);
        }
    }
}