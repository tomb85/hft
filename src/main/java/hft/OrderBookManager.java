package hft;

import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import hft.data.OrderReceived;

import java.util.Map;

public class OrderBookManager implements MarketDataListener {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final OrderBookListener listener;

    private Map<String, OrderBook> books = Maps.newHashMap();

    public OrderBookManager(OrderBookListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMessage(OrderReceived orderReceived) {
        String symbol = orderReceived.getSymbol();
        getBook(symbol).onMessage(orderReceived);
    }

    private OrderBook getBook(String symbol) {
        if (!books.containsKey(symbol)) {
            books.put(symbol, createBook(symbol));
        }
        return books.get(symbol);
    }

    private OrderBook createBook(String symbol) {
        return new OrderBook(symbol, httpClient, listener);
    }
}
