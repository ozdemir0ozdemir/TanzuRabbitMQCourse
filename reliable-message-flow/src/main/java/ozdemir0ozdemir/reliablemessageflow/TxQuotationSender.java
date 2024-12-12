package ozdemir0ozdemir.reliablemessageflow;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ozdemir0ozdemir.reliablemessageflow.service.QuotationService;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class TxQuotationSender {

    private static final String QUEUE = "tx.quotations";
    private static final QuotationService service = new QuotationService();
    private static final Logger log = LoggerFactory.getLogger(TxQuotationSender.class);

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("localhost");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE, true, false, false, null);
        channel.queuePurge(QUEUE);

        int messagesCount = 5;
        int messagesSend = 0;
        int poisonPill = (int) (Math.random() * 5.0 * 2.0);

        channel.txSelect();
        for(int i = 0; i < messagesCount; i++){
            letsWait(1);
            log.info("Sending {}. message to the queue: {}", i, QUEUE);
            if(i == poisonPill) {
                log.error("Not sent {}", i);
            }
            else {
                channel.basicPublish("", QUEUE, null, service.nextDetailed().toString().getBytes());
                messagesSend++;
            }
        }

        if(messagesCount == messagesSend){
            log.info("All messages sent successfully");
            channel.txCommit();
        }
        else {
            log.error("{} message(s) cannot be sent. All of messages rolled back", messagesCount - messagesSend);
            channel.txRollback();
        }

        channel.close();
        connection.close();
        System.exit(0);

    }

    private static void letsWait(int duration) throws Exception {
        Thread.sleep(duration);
    }
}