package hft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import hft.gdax.Product;
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
    private static final double THRESHOLD = 0.000000001;

    private final Product product;
    private final OkHttpClient httpClient;
    private final OrderBookListener listener;
    private final String request;

    private long sequence;
    private String sessionId;
    private TreeMap<String, Double> bids = Maps.newTreeMap(this::bids);
    private TreeMap<String, Double> asks = Maps.newTreeMap(this::asks);
    private Tick top;

    public OrderBook(Product product, OkHttpClient httpClient, OrderBookListener listener) {
        this.product = product;
        this.httpClient = httpClient;
        this.listener = listener;
        request = String.format("https://api.gdax.com/products/%s/book?level=3", product);
    }

    @Override
    public void onMessage(Received received) {
        long incomingSequence = received.getSequence();
        if (check(incomingSequence)) {
            if (incomingSequence > sequence) {
                sequence = incomingSequence;
                sessionId = received.getSessionId();
            }
        }
    }

    @Override
    public void onMessage(Open open) {
        long incomingSequence = open.getSequence();
        if (check(incomingSequence)) {
            if (incomingSequence > sequence) {
                sequence = incomingSequence;
                sessionId = open.getSessionId();
                String price = open.getPrice();
                double size = open.getRemainingSize();
                if ("sell".equals(open.getSide())) {
                    if (!asks.containsKey(price)) {
                        asks.put(price, 0.0);
                    }
                    asks.put(price, asks.get(price) + size);
                } else {
                    if (!bids.containsKey(price)) {
                        bids.put(price, 0.0);
                    }
                    bids.put(price, bids.get(price) + size);
                }
                fireUpdate();
            }
        }
    }

    @Override
    public void onMessage(Done done) {
        long incomingSequence = done.getSequence();
        if (check(incomingSequence)) {
            if (incomingSequence > sequence) {
                sequence = incomingSequence;
                sessionId = done.getSessionId();
                if ("canceled".equals(done.getReason())) {
                    sequence = incomingSequence;
                    String price = done.getPrice();
                    double size = done.getRemainingSize();
                    if ("sell".equals(done.getSide())) {
                        double askSize = asks.get(price);
                        asks.put(price, askSize - size);
                        askSize = asks.get(price);
                        if (askSize >= -THRESHOLD && askSize <= THRESHOLD) {
                            asks.remove(price);
                        }
                    } else {
                        double bidSize = bids.get(price);
                        bids.put(price, bidSize - size);
                        bidSize = bids.get(price);
                        if (bidSize >= -THRESHOLD && bidSize <= THRESHOLD) {
                            bids.remove(price);
                        }
                    }
                    fireUpdate();
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
                sessionId = match.getSessionId();
                String price = match.getPrice();
                double size = match.getSize();
                if ("sell".equals(match.getSide())) {
                    double askSize = asks.get(price);
                    asks.put(price, askSize - size);
                    askSize = asks.get(price);
                    if (askSize >= -THRESHOLD && askSize <= THRESHOLD) {
                        asks.remove(price);
                    }
                } else {
                    double bidSize = bids.get(price);
                    bids.put(price, bidSize - size);
                    bidSize = bids.get(price);
                    if (bidSize >= -THRESHOLD && bidSize <= THRESHOLD) {
                        bids.remove(price);
                    }
                }
                fireUpdate();
            }
        }
    }

    private void fireUpdate() {
        if (topChanged()) {
            listener.onTick(top);
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
        bids.clear();
        asks.clear();
        Response response = httpClient.newCall(new Request.Builder().get().url(request).build()).execute();
        if (response.isSuccessful()) {
            String json = response.body().string();
            BookLvl3 lvl3 = GSON.fromJson(json, BookLvl3.class);
            sequence = lvl3.getSequence();
            populate(bids, lvl3.getBids());
            populate(asks, lvl3.getAsks());
        }
    }

    private boolean topChanged() {
        double bidPrice = Double.parseDouble(bids.firstKey());
        double bidSize = bids.firstEntry().getValue();
        double askPrice = Double.parseDouble(asks.firstKey());
        double askSize = asks.firstEntry().getValue();
        Tick tick = Tick.forSymbol(product.getId()).withSessionId(sessionId).withBidPrice(bidPrice).withAskPrice(askPrice).withBidSize(bidSize).withAskSize(askSize).build();
        if (!tick.equals(top)) {
            top = tick;
            return true;
        } else {
            return false;
        }
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

    private int bids(String lhs, String rhs) {
        long first = convertPrice(lhs);
        long second = convertPrice(rhs);
        if (first > second) {
            return -1;
        } else if (first < second) {
            return 1;
        } else {
            return 0;
        }
    }


    private int asks(String lhs, String rhs) {
        long first = convertPrice(lhs);
        long second = convertPrice(rhs);
        if (first > second) {
            return 1;
        } else if (first < second) {
            return -1;
        } else {
            return 0;
        }
    }

    private long convertPrice(String price) {
        return Math.round(Double.parseDouble(price) * product.getMultiplier());
    }
}