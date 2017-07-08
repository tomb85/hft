package hft.gdax.rest.message;

public class BookLvl3 {

    private long sequence;
    private String[][] bids;
    private String[][] asks;

    public long getSequence() {
        return sequence;
    }

    public String[][] getBids() {
        return bids;
    }

    public String[][] getAsks() {
        return asks;
    }
}
