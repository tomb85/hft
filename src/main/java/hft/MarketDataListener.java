package hft;

import hft.gdax.websocket.message.Done;
import hft.gdax.websocket.message.Match;
import hft.gdax.websocket.message.Open;
import hft.gdax.websocket.message.Received;

public interface MarketDataListener {

    void onMessage(Received received);

    void onMessage(Open open);

    void onMessage(Done done);

    void onMessage(Match match);
}
