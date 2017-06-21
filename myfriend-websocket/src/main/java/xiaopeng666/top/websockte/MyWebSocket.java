package xiaopeng666.top.websockte;

import org.springframework.stereotype.Component;
import xiaopeng666.top.entity.Registry;

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

    private static int onlineCount = 0;

    private static List<MyWebSocket> webSocketSet = new ArrayList<>();
    private static final SimpleDateFormat ddf = new SimpleDateFormat("yyyy年MM月dd日,HH时mm分ss秒");
    private Session session;

    @OnOpen
    public void onOpen (Session session){
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        System.out.println("有新链接加入!当前在线人数为" + getOnlineCount());
        System.out.println("增加链接更新到内存");
        Registry.INSTANCE.saveKey("webSocketSet",webSocketSet);
    }

    @OnClose
    public void onClose (){
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一链接关闭!当前在线人数为" + getOnlineCount());
        System.out.println("减少链接更新到内存");
        Registry.INSTANCE.saveKey("webSocketSet",webSocketSet);
    }

    @OnMessage
    public void onMessage (String message, Session session) throws IOException {
        System.out.println("来自的客户端的消息:" + message);
        System.out.println(session.toString());
        // 群发消息
        for ( MyWebSocket item : webSocketSet ){
            item.sendMessage(ddf.format(new Date())+"</br>"+message);
        }
    }



    public void sendMessage (String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized  int getOnlineCount (){
        return MyWebSocket.onlineCount;
    }

    public static synchronized void addOnlineCount (){
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount (){
        MyWebSocket.onlineCount--;
    }

}
