package hft;

import com.neovisionaries.ws.client.WebSocketException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ArbScannerLauncher {

    private static final Logger LOG = Logger.getLogger(ArbScannerLauncher.class);

    public static void main(String[] args) throws WebSocketException, IOException {

        LOG.info("Process id: " + System.getProperty("app.pid", "UNKNOWN"));

        OrderBookManager orderBookManager = new OrderBookManager(new ArbCalculator());
        WebsocketMarketDataReceiver marketDataReceiver = new WebsocketMarketDataReceiver(orderBookManager);
        marketDataReceiver.start();
    }
}