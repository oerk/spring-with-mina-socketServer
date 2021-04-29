package co.tutorial.minasocket.impl;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceMainHandler extends IoHandlerAdapter {

	private static final  Logger logger = LoggerFactory.getLogger(FixedStringDecoder.class);

	/**
	 * 当一个新客户端连接后触发此方法 
	 */
	@Override
	public void sessionCreated(IoSession session)  throws Exception {
		logger.debug("新客户端连接, session open for " + session.getRemoteAddress());
	}
	


	/**
	 * 有新连接时触发
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.debug("服务端, session open for " + session.getRemoteAddress());
	}

	/**
	 * 连接关闭时触发
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.debug("服务端, session closed from " + session.getRemoteAddress());
	}

	/**
	 * 收到来自客户端的消息
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String clientIP = session.getRemoteAddress().toString();
		logger.debug("服务端接收到来自IP:" + clientIP + "的消息:" + message);
		// 发送完毕后关闭连接
		WriteFuture future=
				session.write("在这里实现业务逻辑\n");
		future.addListener(IoFutureListener.CLOSE);
	}

	
	/**
	 *  当信息已经传送给客户端后触发此方法.
	 */
	@Override
	public void messageSent(IoSession session, Object message)   throws Exception {
		String clientIP = session.getRemoteAddress().toString();
		logger.debug("消息已发送给IP:" + clientIP + "的消息:" + message);
		session.closeOnFlush();
		
	}

	
	/**
	 * 当有异常发生时触发
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.debug("服务端,发生异常" + cause.getMessage());
		session.closeNow();
	}

	 
	
	/**
	 * 当连接空闲时触发此方法
	 */
    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        logger.debug( "空闲状态 " + session.getIdleCount( status ));
    }
	
}
