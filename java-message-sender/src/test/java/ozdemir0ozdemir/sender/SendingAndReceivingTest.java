package ozdemir0ozdemir.sender;

import com.rabbitmq.client.*;
import org.junit.jupiter.api.Test;
import ozdemir0ozdemir.sender.service.QuotationService;

import static org.assertj.core.api.Assertions.*;

public class SendingAndReceivingTest {

    private static final String QUEUE = "que-quotations-test";
    private static final String EXCHANGE = "exc-quotations-test";
    private static final String ROUTE = "route-test";

    private final QuotationService quotationService =
            new QuotationService();


    @Test
    public void should_sendAndReceiveMessageSuccessfully() throws Exception {
        // Given
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT, true);
        channel.queueBind(QUEUE, EXCHANGE, "");

        // When
        String quotation = quotationService.next();
        channel.basicPublish(EXCHANGE, ROUTE, null, quotation.getBytes());

        // Then
        GetResponse response = null;
        int retries = 0;
        while(response == null && retries < 5) {
            response = channel.basicGet(QUEUE, true);
            retries++;
        }

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo(quotation.getBytes());

        // Cleaning up
        channel.queueUnbind(QUEUE, EXCHANGE, "");
        channel.queueDelete(QUEUE);
        channel.exchangeDelete(EXCHANGE);

        channel.close();
        connection.close();
    }
}
