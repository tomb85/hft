package hft;

import com.neovisionaries.ws.client.WebSocketException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class App {

    private static OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws WebSocketException, IOException {
        OrderBookManager orderBookManager = new OrderBookManager(null);
        WebsocketMarketDataReceiver marketDataReceiver = new WebsocketMarketDataReceiver(orderBookManager);
        marketDataReceiver.start();
    }

    private static Tick getForSymbol(String symbol) throws IOException {
        Response response = client.newCall(new Request.Builder().get().url(String.format("https://api.gdax.com/products/%s/book", symbol)).build()).execute();
        if (response.isSuccessful()) {
            String json = response.body().string();
            return Tick.forSymbol(symbol).fromJson(json).build();
        } else {
            return null;
        }
    }
}
