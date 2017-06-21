package xiaopeng666.top.config;

/**
 * Created by xiaopeng on 2016/11/21.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import xiaopeng666.top.handler.CounterHandler;

@Configuration
@EnableWebSocket
@EnableScheduling
public class Web implements WebSocketConfigurer {

    @Autowired
    CounterHandler counterHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(counterHandler, "/counter");
    }

}
