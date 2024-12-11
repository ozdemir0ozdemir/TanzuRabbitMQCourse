package ozdemir0ozdemir.sender;

import java.io.IOException;

import com.rabbitmq.client.*;

public class QuotationConsumer {

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicConsume(
                "quotations",
                true,
                consumer(channel));



//        channel.close();
//        connection.close();


    }

    private static DefaultConsumer consumer(Channel channel) {
        return new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Message: " + new String(body));
            }
        };
    }
}
