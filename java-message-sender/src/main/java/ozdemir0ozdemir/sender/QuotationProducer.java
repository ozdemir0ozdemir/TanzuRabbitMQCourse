package ozdemir0ozdemir.sender;

import ozdemir0ozdemir.sender.service.QuotationService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import sun.misc.Signal;

import java.util.concurrent.atomic.AtomicBoolean;

public class QuotationProducer {
    private static QuotationService quotationService =
            new QuotationService();

    public static void main(String[] args) throws Exception{

        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        while(true) {
            letsWait(1000);
            String quotation = quotationService.next();

            channel.basicPublish(
                    "quotations",
                    "nasq",
                    null,
                    quotation.getBytes());
        }

//        channel.close();
//        connection.close();
    }

    private static void letsWait(int duration) throws Exception {
        Thread.sleep(duration);
    }
}