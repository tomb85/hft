package hft;

import static com.google.common.base.Preconditions.checkNotNull;

public class Tick {

    private final String symbol;
    private final long sequence;
    private final double bidPrice;
    private final double bidSize;
    private final double askPrice;
    private final double askSize;

    public Tick(String symbol, long sequence, double bidPrice, double bidSize, double askPrice, double askSize) {
        this.symbol = checkNotNull(symbol);
        this.sequence = sequence;
        this.bidPrice = bidPrice;
        this.bidSize = bidSize;
        this.askPrice = askPrice;
        this.askSize = askSize;
    }

    public static TickBuilder forSymbol(String symbol) {
        return new TickBuilder(symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tick tick = (Tick) o;

        if (Double.compare(tick.bidPrice, bidPrice) != 0) return false;
        if (Double.compare(tick.bidSize, bidSize) != 0) return false;
        if (Double.compare(tick.askPrice, askPrice) != 0) return false;
        if (Double.compare(tick.askSize, askSize) != 0) return false;
        return symbol != null ? symbol.equals(tick.symbol) : tick.symbol == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = symbol != null ? symbol.hashCode() : 0;
        temp = Double.doubleToLongBits(bidPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(bidSize);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(askPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(askSize);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Tick{" +
                "symbol='" + symbol + '\'' +
                ", sequence=" + sequence +
                ", bidPrice=" + bidPrice +
                ", bidSize=" + bidSize +
                ", askPrice=" + askPrice +
                ", askSize=" + askSize +
                '}';
    }

    public static final class TickBuilder {

        private final String symbol;
        private long sequence;
        private double bidPrice;
        private double bidSize;
        private double askPrice;
        private double askSize;

        public TickBuilder(String symbol) {
            this.symbol = checkNotNull(symbol);
        }

        public TickBuilder withSequence(long sequence) {
            this.sequence = sequence;
            return this;
        }

        public TickBuilder withBidPrice(double bidPrice) {
            this.bidPrice = bidPrice;
            return this;
        }

        public TickBuilder withBidSize(double bidSize) {
            this.bidSize = bidSize;
            return this;
        }

        public TickBuilder withAskPrice(double askPrice) {
            this.askPrice = askPrice;
            return this;
        }

        public TickBuilder withAskSize(double askSize) {
            this.askSize = askSize;
            return this;
        }

        public Tick build() {
            Tick tick = new Tick(symbol, sequence, bidPrice, bidSize, askPrice, askSize);
            return tick;
        }
    }
}