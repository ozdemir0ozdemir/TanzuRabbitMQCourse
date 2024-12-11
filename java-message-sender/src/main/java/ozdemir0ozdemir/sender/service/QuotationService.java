package ozdemir0ozdemir.sender.service;

import ozdemir0ozdemir.sender.model.Quotation;

import java.util.Date;

public class QuotationService {

    private double current = 80.63;
    private String symbol = "VMW";

    public String next() {
        return nextDetailed().toSimpleString();
    }

    public Quotation nextDetailed() {
        current += Math.random() - .4;
        Quotation quotation = new Quotation(symbol, current, new Date());
        return quotation;
    }
}
