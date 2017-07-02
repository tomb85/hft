package hft.data;

public class OrderReceived {

    private String type;
    private String time;
    private String product_id;
    private long sequence;
    private String order_id;
    private double size;
    private double price;
    private String side;
    private String orderType;

    public String getSymbol() {
        return product_id;
    }
}

//{
//        "type": "received",
//        "time": "2014-11-07T08:19:27.028459Z",
//        "product_id": "BTC-USD",
//        "sequence": 10,
//        "order_id": "d50ec984-77a8-460a-b958-66f114b0de9b",
//        "size": "1.34",
//        "price": "502.1",
//        "side": "buy",
//        "order_type": "limit"
//        }