package hft;

import com.google.common.collect.Maps;
import hft.gdax.Product;
import org.joda.time.LocalDate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import static hft.gdax.Product.*;

public class ArbCalculator implements OrderBookListener {

    private final Map<Product, Tick> ticks = Maps.newHashMapWithExpectedSize(3);
    private final EnumSet<Product> requiredSymbols = EnumSet.of(BTC_EUR, ETH_BTC, ETH_EUR);

    private String sessionId;
    private BufferedWriter writer;
    private double currentArb;

    private static final double THRESHOLD = 0.0000_0000_0000_0001;

    private BufferedWriter getWriter() {
        try {
            if (writer != null) {
                writer.close();
            }
            String outputFile = "output/" + LocalDate.now().toString() + "/eur_btc_eth_eur_" + sessionId + ".arb";
            Path path = Paths.get(outputFile);
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
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
            writer = getWriter();
        }
        Product product = Product.fromProductId(tick.getSymbol());
        ticks.put(product, tick);
        if (ticks.keySet().containsAll(requiredSymbols)) {

            double btcEurCost = 0.9975;
            double ethBtcCost = 0.997;
            double ethEurCost = 0.997;

            double leg1 = ticks.get(BTC_EUR).getAskSize() * ticks.get(BTC_EUR).getAskPrice();
            double leg2 = ticks.get(ETH_BTC).getAskSize() * ticks.get(ETH_EUR).getAskPrice();
            double leg3 = ticks.get(ETH_EUR).getBidSize() * ticks.get(ETH_EUR).getBidPrice();

            double eur = Collections.min(Arrays.asList(leg1, leg2, leg3));
            double start = eur;
            double btc = eur * btcEurCost / ticks.get(BTC_EUR).getAskPrice();
            double eth = btc * ethBtcCost / ticks.get(ETH_BTC).getAskPrice();
            eur = eth * ethEurCost * ticks.get(ETH_EUR).getBidPrice();

            double arb = (1.0 / ticks.get(BTC_EUR).getAskPrice()) * (1.0 / ticks.get(ETH_BTC).getAskPrice()) * ticks.get(ETH_EUR).getBidPrice();
            arb *= (btcEurCost * ethBtcCost * ethEurCost);
            double diff = currentArb - arb;
            currentArb = arb;
            boolean same = diff >= -THRESHOLD && diff <= THRESHOLD;
            if (!same && writer != null) {
                try {
                    writer.write(String.join(",",
                            String.valueOf(tick.getTime()),
                            String.valueOf(arb),
                            String.valueOf(eur - start),
                            String.format("%.2f", start)
                            ));
                    writer.write("\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}