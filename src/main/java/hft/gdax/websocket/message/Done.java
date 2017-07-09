package hft.gdax.websocket.message;

public class Done {

    private String type;
    private String time;
    private String product_id;
    private long sequence_id;
    private String price;
    private String order_id;
    private String reason;
    private String side;
    private double remaining_size;
    private long sequence;

    public String getSymbol() {
        return product_id;
    }

    public long getSequence() {
        return sequence;
    }

    public String getPrice() {
        return price;
    }

    public double getRemainingSize() {
        return remaining_size;
    }

    public String getSide() {
        return side;
    }

    public String getReason() {
        return reason;
    }
}

/*

{
    "type": "done",
    "time": "2014-11-07T08:19:27.028459Z",
    "product_id": "BTC-USD",
    "sequence": 10,
    "price": "200.2",
    "order_id": "d50ec984-77a8-460a-b958-66f114b0de9b",
    "reason": "filled", // canceled
    "side": "sell",
    "remaining_size": "0.2"
}

 */