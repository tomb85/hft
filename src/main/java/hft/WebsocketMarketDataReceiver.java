package hft;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.*;
import hft.gdax.websocket.message.Done;
import hft.gdax.websocket.message.Match;
import hft.gdax.websocket.message.Open;
import hft.gdax.websocket.message.Received;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WebsocketMarketDataReceiver extends WebSocketAdapter {

    private static final Logger LOG = Logger.getLogger(WebsocketMarketDataReceiver.class);

    private static final Gson GSON = new Gson();
    private static final JsonParser PARSER = new JsonParser();
    private static final String SUBSCRIPTION_REQUEST = "{\"type\":\"subscribe\",\"product_ids\":[\"BTC-EUR\",\"ETH-EUR\",\"ETH-BTC\"]}";
//    private static final String SUBSCRIPTION_REQUEST = "{\"type\":\"subscribe\",\"product_ids\":[\"BTC-USD\"]}";

    private final MarketDataListener listener;

    private String sessionId;
    private WebSocket socket;

    public WebsocketMarketDataReceiver(MarketDataListener listener) {
        this.listener = listener;
    }

    public synchronized void start() throws IOException {
        LOG.info("Application starting");
        if (socket == null) {
            socket = new WebSocketFactory().createSocket("wss://ws-feed.gdax.com").addExtension(WebSocketExtension.PERMESSAGE_DEFLATE).addListener(this);
        }
        socket.connectAsynchronously();
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        sessionId = UUID.randomUUID().toString();
        LOG.info("Connected to market data websocket. Established new session " + sessionId);
        subscribe(websocket);
    }

    private void subscribe(WebSocket websocket) {
        LOG.info("Sending subscription request: " + SUBSCRIPTION_REQUEST);
        websocket.sendText(SUBSCRIPTION_REQUEST);
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws IOException {
        LOG.warn("Disconnected, closedByServer: " + closedByServer + ", serverCloseFrame: " + serverCloseFrame + ", clientCloseFrame: " + clientCloseFrame);
        socket = websocket.recreate();
        socket.connectAsynchronously();
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) {
        JsonObject obj = PARSER.parse(text).getAsJsonObject();
        obj.addProperty("sessionId", sessionId);
        char code = text.charAt(9);
        switch (code) {
            case 'r':
                listener.onMessage(GSON.fromJson(obj, Received.class));
                break;
            case 'o':
                listener.onMessage(GSON.fromJson(obj, Open.class));
                break;
            case 'd':
                listener.onMessage(GSON.fromJson(obj, Done.class));
                break;
            case 'm':
                listener.onMessage(GSON.fromJson(obj, Match.class));
                break;
        }
    }
}
