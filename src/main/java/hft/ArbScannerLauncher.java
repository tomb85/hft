package hft;

import com.neovisionaries.ws.client.WebSocketException;

import java.io.IOException;

public class ArbScannerLauncher {

    public static void main(String[] args) throws WebSocketException, IOException {
        OrderBookManager orderBookManager = new OrderBookManager(new ArbCalculator());
        WebsocketMarketDataReceiver marketDataReceiver = new WebsocketMarketDataReceiver(orderBookManager);
        marketDataReceiver.start();
    }
}