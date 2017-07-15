package hft;

import com.neovisionaries.ws.client.WebSocketException;
import hft.gdax.ArbCalculator;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws WebSocketException, IOException {
        OrderBookManager orderBookManager = new OrderBookManager(new ArbCalculator());
        WebsocketMarketDataReceiver marketDataReceiver = new WebsocketMarketDataReceiver(orderBookManager);
        marketDataReceiver.start();
    }
}