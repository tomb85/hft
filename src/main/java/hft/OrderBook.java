package hft;

import com.squareup.okhttp.OkHttpClient;
import hft.data.OrderReceived;

public class OrderBook implements MarketDataListener {

    private final String symbol;
    private final OkHttpClient httpClient;
    private final OrderBookListener listener;

    public OrderBook(String symbol, OkHttpClient httpClient, OrderBookListener listener) {
        this.symbol = symbol;
        this.httpClient = httpClient;
        this.listener = listener;
    }

    @Override
    public void onMessage(OrderReceived orderReceived) {
        System.out.println("Received");
    }
}

//sequence;
//
//        if (seq == 0 => seq = init)
//
//        if (incomingSeq < seq => discard)
//
//        if (incomingSeq - seq > 1 => seq = init)
//
//        else => seq = incomingSeq, applyMsg
//
//        == check if top changed
//

