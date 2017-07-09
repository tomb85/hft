package hft;

import com.neovisionaries.ws.client.WebSocketException;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws WebSocketException, IOException {
        OrderBookManager orderBookManager = new OrderBookManager(System.out::println);
        WebsocketMarketDataReceiver marketDataReceiver = new WebsocketMarketDataReceiver(orderBookManager);
        marketDataReceiver.start();
    }
}