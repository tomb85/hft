package hft.gdax;

import com.google.common.collect.Maps;
import com.google.gson.internal.$Gson$Preconditions;
import hft.OrderBookListener;
import hft.Tick;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static hft.gdax.Product.BTC_EUR;
import static hft.gdax.Product.ETH_BTC;
import static hft.gdax.Product.ETH_EUR;

public class ArbCalculator implements OrderBookListener {

    private final Map<Product, Tick> ticks = Maps.newHashMapWithExpectedSize(3);
    private final EnumSet<Product> requiredSymbols = EnumSet.of(BTC_EUR, ETH_BTC, ETH_EUR);
    private final BufferedWriter writer;

    private String sessionId;
    private double currentArb;

    private static final double THRESHOLD = 0.0000_0000_0000_0001;

    public ArbCalculator() {
        writer = checkNotNull(getWriter());
    }

    private BufferedWriter getWriter() {
        try {
            String outputFile = "eur_btc_eth_eur.arb";
            Files.deleteIfExists(Paths.get(outputFile));
            Files.createFile(Paths.get(outputFile));
            return new BufferedWriter(new FileWriter(outputFile, true));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onTick(Tick tick) {
        if (!tick.getSessionId().equals(sessionId)) {
            ticks.clear();
            sessionId = tick.getSessionId();
            currentArb = 0;
        }
        Product product = Product.fromProductId(tick.getSymbol());
        ticks.put(product, tick);
        if (ticks.keySet().containsAll(requiredSymbols)) {
            double arb = (1.0 / ticks.get(BTC_EUR).getAskPrice()) * (1.0 / ticks.get(ETH_BTC).getAskPrice()) * ticks.get(ETH_EUR).getBidPrice();
            double diff = currentArb - arb;
            currentArb = arb;
            boolean same = diff >= -THRESHOLD && diff <= THRESHOLD;
            if (!same) {
                try {
                    writer.write(String.join(",", tick.getSessionId(), String.valueOf(tick.getTime()), String.valueOf(arb)));
                    writer.write("\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}