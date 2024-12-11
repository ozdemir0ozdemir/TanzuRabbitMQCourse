package ozdemir0ozdemir.sender.model;

import java.util.Date;

public final class Quotation {

    private String symbol;
    private double current;
    private Date date;

    public Quotation(String symbol, double current, Date date) {
        this.symbol = symbol;
        this.current = current;
        this.date = date;
    }


    public String toSimpleString() {
        return toString();
    }

    @Override
    public String toString() {
        return "Quotation{" +
                "current=" + current +
                ", symbol='" + symbol + '\'' +
                ", date=" + date +
                '}';
    }
}
