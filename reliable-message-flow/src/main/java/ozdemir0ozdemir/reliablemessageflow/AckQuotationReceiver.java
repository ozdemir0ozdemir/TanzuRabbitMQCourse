package ozdemir0ozdemir.reliablemessageflow;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class AckQuotationReceiver {

    private static final String QUEUE = "ack.quotations";
    private static final Logger log = LoggerFactory.getLogger(AckQuotationReceiver.class);

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("localhost");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE, true, false, false, null);
        channel.queuePurge(QUEUE);

        // only 1 unacked message in the queue
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("Received Message: {}", envelope.getDeliveryTag());
                process(2000); // simulates processing
                log.info("Processed Message: {}", envelope.getDeliveryTag());

                // Acknowledge the message has been processed
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(QUEUE, false, consumer);
        log.info("Fair consumer started");

//        letsWait(5000);
//        channel.close();
//        connection.close();

    }


    private static void process(int duration)  {
        try {
            Thread.sleep(duration);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void gui(ActionListener actionListener) {
        JFrame f = new JFrame("Quotation Sender");
        JButton b = new JButton("Click to stop");
        b.setBounds(15, 15, 200, 50);
        f.add(b);
        f.setSize(260, 120);
        f.setLayout(null);
        f.setVisible(true);
        b.addActionListener(actionListener);

    }
}