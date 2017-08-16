package xiaopeng666.top.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Map;

@Component
public class Sender {
    public static final String EXCHANGE = "hello1";
    public static final String ROUTINGKEY = "xpo";
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(Map msg) {
        logger.debug("send map:"+msg);
        this.rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY, msg);
    }

    public void send(String msg) {
        logger.debug("send String:"+msg);
        this.rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY, msg);
    }

}