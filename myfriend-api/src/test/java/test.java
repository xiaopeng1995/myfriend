import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import xiaopeng666.top.utils.TulingApiProcess;

/**
 * Created by xiaopeng on 2017/8/7.
 */
public class Test {
    //队列名称
    private final static String QUEUE_NAME = "hello1";
    public static final String ROUTINGKEY = "xpo";

    public static void main(String[] args) {
        /**
         * 创建连接连接到MabbitMQ
         */
        try {
            String msg="{\"name\":\"ded\",\"info\":\"eeee\",\"time\":1502847915997,\"type\":1}";
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("113.209.37.40");
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setPort(5672);
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
            //logger.error(e.getMessage());
        }

    }
}
