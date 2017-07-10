package hft.gdax.websocket.message;

public class Open {

    private String sessionId;
    private String type;
    private String time;
    private String product_id;
    private long sequence;
    private String order_id;
    private String price;
    private double remaining_size;
    private String side;

    public String getSymbol() {
        return product_id;
    }

    public long getSequence() {
        return sequence;
    }

    public String getPrice() {
        return price;
    }

    public String getSide() {
        return side;
    }

    public double getRemainingSize() {
        return remaining_size;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getOrderId() {
        return order_id;
    }
}

/*

{
    "type": "open",
    "time": "2014-11-07T08:19:27.028459Z",
    "product_id": "BTC-USD",
    "sequence": 10,
    "order_id": "d50ec984-77a8-460a-b958-66f114b0de9b",
    "price": "200.2",
    "remaining_size": "1.00",
    "side": "sell"
}

 */