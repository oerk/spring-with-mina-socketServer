package co.tutorial.minasocket.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.firewall.BlacklistFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.tutorial.minasocket.impl.ServiceMainHandler;


public class MinaPreFiexServer {
	private static final Logger logger = LoggerFactory.getLogger(MinaClient.class);

	 private static final int PORT = 10200;
	    
	    public static void main(String[] args) throws IOException {
	        // 构造接收器
	        IoAcceptor acceptor = new NioSocketAcceptor();
	        ServiceMainHandler handler = new ServiceMainHandler();
	        acceptor.setHandler(handler);
	        // 读写通道10秒内无操作进入空闲状态   
	        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
	        acceptor.getSessionConfig().setReadBufferSize( 2048 );
	        acceptor.getSessionConfig().setWriteTimeout(20);

	        //添加过滤器和日志组件
	        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new PrefixedStringCodecFactory(Charset.forName( "UTF-8" ))));

	        acceptor.getFilterChain().addLast("logging", new LoggingFilter(logger.getClass()));

	        acceptor.bind(new InetSocketAddress(PORT));
	        logger.info("MinaServer started on port " + PORT);
	    }
	
}
