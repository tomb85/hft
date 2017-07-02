package hft;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.*;
import hft.data.OrderReceived;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WebsocketMarketDataReceiver extends WebSocketAdapter {

    private static final Gson GSON = new Gson();
    private static final String SUBSCRIPTION_KEY = "{\"type\":\"subscribe\",\"product_ids\":[\"BTC-EUR\",\"ETH-EUR\",\"ETH-BTC\"]}";

    private final MarketDataListener listener;

    private WebSocket socket;

    public WebsocketMarketDataReceiver(MarketDataListener listener) {
        this.listener = listener;
    }

    public synchronized void start() throws IOException {
        if (socket == null) {
            socket = new WebSocketFactory().createSocket("wss://ws-feed.gdax.com").addExtension(WebSocketExtension.PERMESSAGE_DEFLATE).addListener(this);
        }
        socket.connectAsynchronously();
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        subscribe(websocket);
    }

    private void subscribe(WebSocket websocket) {
        websocket.sendText(SUBSCRIPTION_KEY);
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws IOException {
        System.out.println("Disconnected, closedByServer: " + closedByServer);
        System.out.println(serverCloseFrame);
        System.out.println(clientCloseFrame);
        socket = websocket.recreate();
        socket.connectAsynchronously();
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) {
        char code = text.charAt(9);
        switch (code) {
            case 'r':
                listener.onMessage(GSON.fromJson(text, OrderReceived.class));
        }
    }
}
