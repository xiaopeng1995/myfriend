package xiaopeng666.top.websockte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import xiaopeng666.top.entity.MsgInfo;
import xiaopeng666.top.entity.MsgType;
import xiaopeng666.top.entity.Registry;
import xiaopeng666.top.redis.RedisUtil;
import xiaopeng666.top.utils.JsonUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * websocket实例
 */
@ServerEndpoint("/websocket")
@Component
public class MyWebSocket {

    // logger
    private static final Logger logger = LoggerFactory.getLogger(MyWebSocket.class);
    private static int onlineCount = 0;

    private static List<MyWebSocket> webSocketSet = new ArrayList<>();
    private static final SimpleDateFormat ddf = new SimpleDateFormat("yyyy年MM月dd日,HH时mm分ss秒");
    private Session session;
    private String token;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        logger.info("增加链接更新到内存");
        Registry.INSTANCE.saveKey("webSocketSet", webSocketSet);
        addOnlineCount();
        logger.info("有新链接加入!当前在线人数为" + getOnlineCount());


        sendCount(getOnlineCount());
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一链接关闭!当前在线人数为" + getOnlineCount());
        System.out.println("减少链接更新到内存");
        Registry.INSTANCE.saveKey("webSocketSet", webSocketSet);
        sendCount(getOnlineCount());
    }

    private void sendCount(int count) {
        // 群发消息
        MsgInfo msgInfo = new MsgInfo();
        msgInfo.setInfo(count + "");
        msgInfo.setType(MsgType.MSMCOUNT.value());
        msgInfo.setTime(new Date());
        try {
            String jsonm = JsonUtils.Mapper.writeValueAsString(msgInfo);
            for (MyWebSocket item : webSocketSet) {
                item.sendMessage(jsonm);
            }
        } catch (IOException io) {
            logger.error("统计人数发生异常！");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.debug("来自的客户端的消息:" + message);
        token = message;
        // 群发消息
//        for ( MyWebSocket item : webSocketSet ){
//            item.sendMessage(ddf.format(new Date())+"</br>"+message);
//        }
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return MyWebSocket.onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }

}
