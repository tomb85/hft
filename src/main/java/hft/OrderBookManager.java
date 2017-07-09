package hft;

import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import hft.gdax.Product;
import hft.gdax.websocket.message.Done;
import hft.gdax.websocket.message.Match;
import hft.gdax.websocket.message.Open;
import hft.gdax.websocket.message.Received;

import java.util.Map;

public class OrderBookManager implements MarketDataListener {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final OrderBookListener listener;

    private Map<String, OrderBook> books = Maps.newHashMap();

    public OrderBookManager(OrderBookListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMessage(Received received) {
        String symbol = received.getSymbol();
        getBook(symbol).onMessage(received);
    }

    @Override
    public void onMessage(Open open) {
        String symbol = open.getSymbol();
        getBook(symbol).onMessage(open);
    }

    @Override
    public void onMessage(Done done) {
        String symbol = done.getSymbol();
        getBook(symbol).onMessage(done);
    }

    @Override
    public void onMessage(Match match) {
        String symbol = match.getSymbol();
        getBook(symbol).onMessage(match);
    }

    private OrderBook getBook(String symbol) {
        if (!books.containsKey(symbol)) {
            books.put(symbol, createBook(symbol));
        }
        return books.get(symbol);
    }

    private OrderBook createBook(String symbol) {
        Product product = Product.fromProductId(symbol);
        return new OrderBook(product, httpClient, listener);
    }
}