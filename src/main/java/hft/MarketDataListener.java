package hft;

import hft.data.OrderReceived;

public interface MarketDataListener {
    void onMessage(OrderReceived orderReceived);
}
