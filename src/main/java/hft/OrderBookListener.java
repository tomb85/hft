package hft;

@FunctionalInterface
public interface OrderBookListener {
    void onTick(Tick top);
}
