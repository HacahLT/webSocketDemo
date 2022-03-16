package cn.hacah.websocketdemo.handle;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpringWebSocketHandler extends TextWebSocketHandler {
    /**
     * 存储用户id和其对应的session
     */
    private static final Map<String, WebSocketSession> users = new HashMap<>();

    /**
     * 用户名key值
     */
    private static final String USER_ID = "WEBSOCKET_USERID";

    /**
     * 连接建立后触发
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("成功建立websocket连接!");
        // 取出在拦截器中存储的username
        String userId = (String) session.getAttributes().get(USER_ID);
        users.put(userId, session);
        System.out.println("当前线上用户数量:" + users.size());
    }

    /**
     * 关闭连接时触发
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        String userId = (String) session.getAttributes().get(USER_ID);
        System.out.println("用户" + userId + "已退出！");
        users.remove(userId);
        System.out.println("剩余在线用户" + users.size());
    }

    /**
     * 接收消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        System.out.println("收到消息：" + message);
        if (message.getPayload().contains("在吗")) {
            session.sendMessage(new TextMessage("对方不在线！"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("传输出现异常，关闭websocket连接... ");
        String userId = (String) session.getAttributes().get(USER_ID);
        users.remove(userId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    /**
     * 给某个用户发送消息
     */
    public void sendMessageToUser(String userId, TextMessage message) {
        for (String id : users.keySet()) {
            if (id.equals(userId)) {
                try {
                    if (users.get(id).isOpen()) {
                        users.get(id).sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 给所有在线用户发送消息
     */
    public void sendMessageToUsers(TextMessage message) {
        for (String userId : users.keySet()) {
            try {
                if (users.get(userId).isOpen()) {
                    users.get(userId).sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}