package xiaopeng666.top.utils.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by xiaopeng on 2017/5/22.
 */
public class RabittMQSend {
    private final static Logger logger = LoggerFactory.getLogger(RabittMQSend.class);
    //队列名称
    private final static String QUEUE_NAME = "hello1";
    public static final String ROUTINGKEY = "xpo";
    private static ConnectionFactory factory = null;


    public void init(AbstractConfiguration aconfig) {
        factory=new ConnectionFactory();
        factory.setHost(aconfig.getString("rabbitmq.virtualHost"));
        factory.setUsername(aconfig.getString("rabbitmq.userName"));
        factory.setPassword(aconfig.getString("rabbitmq.password"));
        factory.setPort(Integer.parseInt(aconfig.getString("rabbitmq.port")));
    }

    public static void sendRabbitMQ(String msg) {
        /**
         * 创建连接连接到MabbitMQ
         */
        try {
            //创建一个连接
            Connection connection = factory.newConnection();
            //创建一个频道
            Channel channel = connection.createChannel();
            //指定一个队列
            channel.exchangeDeclare(QUEUE_NAME, "topic", true);
            //往队列中发出一条消息
            channel.basicPublish(QUEUE_NAME, ROUTINGKEY, MessageProperties.BASIC, msg.getBytes());
            //关闭频道和连接
            channel.close();
            connection.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
