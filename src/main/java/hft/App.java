package hft;

import com.neovisionaries.ws.client.WebSocketException;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws WebSocketException, IOException {

        System.out.println("Application name: " + System.getProperty("app.name", "UNKNOWN"));
        System.out.println("Process id: " + System.getProperty("app.pid", "UNKNOWN"));
        System.out.println("Base dir " + System.getProperty("app.home", "UNKNOWN"));

        OrderBookManager orderBookManager = new OrderBookManager(new ArbCalculator());
        WebsocketMarketDataReceiver marketDataReceiver = new WebsocketMarketDataReceiver(orderBookManager);
        marketDataReceiver.start();
    }
}