package co.tutorial.minasocket.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.tutorial.minasocket.impl.FixedStringCodecFactory;
import co.tutorial.minasocket.impl.FixedStringEncoder;
//https://www.cnblogs.com/dennisit/archive/2013/02/21/2920693.html
public class MinaClient {

	  private static final String HOST = "127.0.0.1";
	    private static final int PORT = 10200;
		private static final  Logger logger = LoggerFactory.getLogger(MinaClient.class);

	    public static void main(String[] args) {
	        IoConnector connector = new NioSocketConnector();
	        connector.setHandler(new ClientHandler());
	        // 设置连接超时时间 单位毫秒   
	        connector.setConnectTimeoutMillis(30000);   
	        //添加过滤器和日志组件
	        //IoFilter filter = new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName( "UTF-8" )));

	       IoFilter filter = new ProtocolCodecFilter(new FixedStringCodecFactory(Charset.forName( "UTF-8" ),8));
	        connector.getFilterChain().addLast(" codec", filter);
	        connector.getFilterChain().addLast("logging", new LoggingFilter());
	        // 创建连接   
	        IoSession session = null;   
	        try {   
	            ConnectFuture connect = connector.connect(new InetSocketAddress(HOST, PORT));   
	            // 等待连接创建完成   
	            connect.awaitUninterruptibly();   
	            // 获取session   
	            session = connect.getSession();   
	            session.write("123456789");   
	        } catch (Exception e) {   
	        	logger.info("客户端连接异常");   
	        }   
	        session.getCloseFuture().awaitUninterruptibly(4000);   
	        connector.dispose();   

	    }
	
}
