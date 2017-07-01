package hft;

import com.google.gson.Gson;

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

        private static final Gson GSON = new Gson();

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

        public TickBuilder fromJson(String json) {
            TickRaw raw = GSON.fromJson(json, TickRaw.class);
            sequence = Long.parseLong(raw.sequence);
            bidPrice = Double.parseDouble(raw.bids[0][0]);
            bidSize = Double.parseDouble(raw.bids[0][1]);
            askPrice = Double.parseDouble(raw.asks[0][0]);
            askSize = Double.parseDouble(raw.asks[0][1]);
            return this;
        }

        public Tick build() {
            Tick tick = new Tick(symbol, sequence, bidPrice, bidSize, askPrice, askSize);
            return tick;
        }
    }

    private class TickRaw {
        private String sequence;
        private String[][] bids;
        private String[][] asks;
    }
}
