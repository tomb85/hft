package hft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import hft.gdax.rest.message.BookLvl3;
import hft.gdax.websocket.message.Done;
import hft.gdax.websocket.message.Match;
import hft.gdax.websocket.message.Open;
import hft.gdax.websocket.message.Received;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook implements MarketDataListener {

    private static final Gson GSON = new Gson();

    private final String symbol;
    private final OkHttpClient httpClient;
    private final OrderBookListener listener;
    private final String request;

    private long sequence;
    private Map<String, Double> bids = Maps.newHashMap();
    private Map<String, Double> asks = Maps.newHashMap();
    private double bestBid;
    private double bestAsk;
    private Tick top;

    public OrderBook(String symbol, OkHttpClient httpClient, OrderBookListener listener) {
        this.symbol = symbol;
        this.httpClient = httpClient;
        this.listener = listener;
        request = String.format("https://api.gdax.com/products/%s/book?level=3", symbol);
    }

    @Override
    public void onMessage(Received received) {
        long incomingSequence = received.getSequence();
        if (check(incomingSequence)) {
            if (incomingSequence > sequence) {
                sequence = incomingSequence;
            }
        }
    }

    @Override
    public void onMessage(Open open) {
        long incomingSequence = open.getSequence();
        if (check(incomingSequence)) {
            if (incomingSequence > sequence) {
                sequence = incomingSequence;
                double price = open.getPrice();
                double size = open.getSize();
                if ("sell".equals(open.getSide())) {
                    if (!asks.containsKey(String.valueOf(price))) {
                        asks.put(String.valueOf(price), 0.0);
                    }
                    asks.put(String.valueOf(price), asks.get(String.valueOf(price)) + size);
                } else {
                    if (!bids.containsKey(String.valueOf(price))) {
                        bids.put(String.valueOf(price), 0.0);
                    }
                    bids.put(String.valueOf(price), bids.get(String.valueOf(price)) + size);
                }
            }
        }
    }

    @Override
    public void onMessage(Done done) {
        long incomingSequence = done.getSequence();
        if (check(incomingSequence)) {
            if (incomingSequence > sequence) {
                sequence = incomingSequence;
                if ("canceled".equals(done.getReason())) {
                    sequence = incomingSequence;
                    double price = done.getPrice();
                    double size = done.getRemainingSize();
                    if ("sell".equals(done.getSide())) {
                        asks.put(String.valueOf(price), asks.get(String.valueOf(price)) - size);
                        if (asks.get(String.valueOf(price)) <= 0.0) {
                            asks.remove(String.valueOf(price));
                        }
                    } else {
                        bids.put(String.valueOf(price), bids.get(String.valueOf(price)) - size);
                        if (bids.get(String.valueOf(price)) <= 0.0) {
                            bids.remove(String.valueOf(price));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMessage(Match match) {
        long incomingSequence = match.getSequence();
        if (check(incomingSequence)) {
            if (incomingSequence > sequence) {
                sequence = incomingSequence;
            }
        }
    }

    private boolean check(long incomingSequence) {
        try {
            if (sequence == 0 || incomingSequence - sequence > 1) {
                init();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void init() throws IOException {
        Response response = httpClient.newCall(new Request.Builder().get().url(request).build()).execute();
        if (response.isSuccessful()) {
            String json = response.body().string();
            BookLvl3 lvl3 = GSON.fromJson(json, BookLvl3.class);
            sequence = lvl3.getSequence();
            populate(bids, lvl3.getBids());
            populate(asks, lvl3.getAsks());
            if (topChanged()) {
                System.out.println(top);
            }
        }
    }

    private boolean topChanged() {
        findBestBid();
        findBestAsk();
        Tick tick = Tick.forSymbol(symbol).withSequence(sequence).withBidPrice(bestBid).withAskPrice(bestAsk).build();
        if (!top.equals(tick)) {
            top = tick;
            return true;
        } else {
            return false;
        }
    }

    private void findBestBid() {
        double bestPrice = 0.0;
        for (String key : bids.keySet()) {
            double price = Double.parseDouble(key);
            if (price > bestPrice) {
                bestPrice = price;
            }
        }
        bestBid = bestPrice;
    }

    private void findBestAsk() {
        double bestPrice = Double.MAX_VALUE;
        for (String key : asks.keySet()) {
            double price = Double.parseDouble(key);
            if (price < bestPrice) {
                bestPrice = price;
            }
        }
        bestAsk = bestPrice;
    }

    private void populate(Map<String, Double> target, String[][] data) {
        for (String[] entry : data) {
            String price = entry[0];
            Double size = Double.parseDouble(entry[1]);
            if (!target.containsKey(price)) {
                target.put(price, 0.0);
            }
            target.put(price, target.get(price) + size);
        }
    }

    private static int bids(String lhs, String rhs) {
        long first = Math.round(Double.parseDouble(lhs) * 100000000);
        long second = Math.round(Double.parseDouble(rhs) * 100000000);
        if (first > second) {
            return 1;
        } else if (first < second) {
            return -1;
        } else {
            return 0;
        }
    }


    private static int asks(String lhs, String rhs) {
        long first = Math.round(Double.parseDouble(lhs) * 100000000);
        long second = Math.round(Double.parseDouble(rhs) * 100000000);
        return (int) (second - first);
    }

    public static void main(String[] args) {

        TreeMap<String, Double> bids = Maps.newTreeMap(OrderBook::bids);
        bids.put("223.3", 23.9);
        bids.put("123.3", 23.9);

        System.out.println(bids.firstKey());


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
//        use websocket session id?






