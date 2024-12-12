package ozdemir0ozdemir.reliablemessageflow;

import com.rabbitmq.client.*;
import ozdemir0ozdemir.reliablemessageflow.service.QuotationService;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuotationSender {

    private static final String EXCHANGE = "quotations";
    private static final String QUEUE = "quotations";
    private static final QuotationService service = new QuotationService();

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("localhost");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT, true);
        channel.queueDeclare(QUEUE, true, false, false, null);
        channel.queueBind(QUEUE, EXCHANGE, "");

        AtomicBoolean sendMessages = new AtomicBoolean(true);
        gui(e -> sendMessages.set(false));


        AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .build();

        while (sendMessages.get()) {
            letsWait(1000);
            String quotation = service.next();


            channel.basicPublish(EXCHANGE, "nasq", props, quotation.getBytes());
        }

        channel.close();
        connection.close();
        System.exit(0);

    }

    private static void letsWait(int duration) throws Exception {
        Thread.sleep(duration);
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