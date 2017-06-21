package xiaopeng666.top.mq;


import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import xiaopeng666.top.config.ConfigProperties;
import xiaopeng666.top.entity.Registry;

import xiaopeng666.top.websockte.MyWebSocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * rabbit.接收任务处理
 */

@Configuration
public class ApplicationStartup {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);
    public static final String EXCHANGE = "hello1";
    public static final String ROUTINGKEY = "xpo";
    private static final SimpleDateFormat ddf = new SimpleDateFormat("yyyy年MM月dd日,HH时mm分ss秒");
    @Autowired
    ConfigProperties Config;
    @Autowired
    CachingConnectionFactory cachingConnectionFactory;


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
        return template;
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * <p>
     * <p>
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */

    @Bean
    public TopicExchange defaultExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(EXCHANGE, true, false, true, null); //队列持久
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(defaultExchange()).with(ApplicationStartup.ROUTINGKEY);
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(cachingConnectionFactory);
        container.setQueues(queue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认
        container.setMessageListener(new ChannelAwareMessageListener() {

            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                byte[] body = message.getBody();
                List<MyWebSocket> a = (List<MyWebSocket>) Registry.INSTANCE.getValue().get("webSocketSet");
                // 群发消息
                try {
                    // MsgInfo msgInfo = JsonUtils.Mapper.reader(new String(body), MsgInfo.class);
                    if (a.size() > 0) {
                        for (MyWebSocket item : a) {
                            item.sendMessage(ddf.format(new Date()) + "</br>" + new String(body));
                        }
                    }
                } catch (Exception e) {
                    logger.error("未启动客户端,跳过.");
                }
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费
            }
        });
        return container;
    }


}