package co.tutorial.minasocket.client;


import java.util.Date;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends IoHandlerAdapter{
	
	private static final  Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    /**
     * 在会话打开时向服务端发送当前日期
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.write("客户端会话打开时间:"+new Date());
    }
    
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        logger.info("我是客户端,收到响应:{}",message.toString());
    }
    
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.info("我是客户端,我发送的消息:{}",message);
    }
    
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("客户端会话关闭时间:{}",new Date());
    }
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        logger.info("我是客户端,系统出现异常:{}",cause.getMessage());
        session.closeNow();
    }    
	
}
