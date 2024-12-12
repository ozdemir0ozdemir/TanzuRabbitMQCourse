package ozdemir0ozdemir.reliablemessageflow;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ozdemir0ozdemir.reliablemessageflow.service.QuotationService;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class AckQuotationSender {

    private static final String QUEUE = "ack.quotations";
    private static final QuotationService service = new QuotationService();
    private static final Logger log = LoggerFactory.getLogger(AckQuotationSender.class);

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("localhost");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE, true, false, false, null);
        channel.queuePurge(QUEUE);


        AtomicBoolean sendMessages = new AtomicBoolean(true);
        gui(e -> sendMessages.set(false));

        while (sendMessages.get()) {
            letsWait(500);
            channel.basicPublish("", QUEUE, null, service.nextDetailed().toString().getBytes());
        }

        channel.close();
        connection.close();
        System.exit(0);
    }

    private static void letsWait(int duration) throws Exception {
        Thread.sleep(duration);
    }

    private static void gui(ActionListener actionListener) {
        JFrame f = new JFrame("Ack Sender");
        JButton b = new JButton("Click to stop");
        b.setBounds(15, 15, 200, 50);
        f.add(b);
        f.setSize(260, 120);
        f.setLayout(null);
        f.setVisible(true);
        b.addActionListener(actionListener);

    }
}