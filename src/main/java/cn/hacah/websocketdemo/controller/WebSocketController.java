package cn.hacah.websocketdemo.controller;

import cn.hacah.websocketdemo.handle.SpringWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*GET请求开放用于测试，最好只允许POST请求*/
@Controller
@RequestMapping(value = "/websocket", method = {RequestMethod.POST, RequestMethod.GET})
public class WebSocketController {

    @Autowired
    SpringWebSocketHandler springWebSocketHandler;

    /**
     * 登录将username放入session中，然后在拦截器HandshakeInterceptor中取出
     */
    @ResponseBody
    @RequestMapping("/login")
    public String login(HttpServletRequest request, @RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        System.out.println("登录：" + username + "：" + password);
        HttpSession session = request.getSession();
        if (null != session) {
            session.setAttribute("SESSION_USERNAME", username);
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     * 指定发送
     */
    @ResponseBody
    @RequestMapping("/sendToUser")
    public String send(@RequestParam(value = "username") String username, @RequestParam(value = "info") String info) {
        springWebSocketHandler.sendMessageToUser(username, new TextMessage(info));
        System.out.println("发送至：" + username);
        return "success";
    }

    /**
     * 广播
     */
    @ResponseBody
    @RequestMapping("/broadcast")
    public String broadcast(@RequestParam(value = "info") String info) {
        springWebSocketHandler.sendMessageToUsers(new TextMessage("广播消息：" + info));
        System.out.println("广播成功");
        return "success";
    }
}