package co.tutorial.minasocket.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

import co.tutorial.minasocket.impl.CustPrefixedStringCodecFactory;
import co.tutorial.minasocket.impl.FixedStringCodecFactory;

public class MinaPreFiexClient {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 10200;
	private static final Logger logger = LoggerFactory.getLogger(MinaPreFiexClient.class);

	private static final int DEFAULT_BOTHIDLE_TIMEOUT = 90; // 设置默认发呆时间为90s
	private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 2; // 设置默认连接超时为2s
	private static final int DEFAULT_SO_TIMEOUT = 1000 * 60; // 设置默认读取超时为60s

	public static void main(String[] args) {
		IoConnector connector = new NioSocketConnector();
		connector.setHandler(new ClientHandler());
		// 设置连接超时时间 单位毫秒
		connector.setConnectTimeoutMillis(30000);

		IoFilter filter = new ProtocolCodecFilter(new CustPrefixedStringCodecFactory(Charset.forName("UTF-8"),8));

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
			session.write("123456789啊");

		} catch (Exception e) {
			logger.error("客户端连接异常", e);
		} finally {
			if (session != null) {
				session.getCloseFuture().awaitUninterruptibly();
				session.closeNow();
				session.getService().dispose();
				connector.dispose();
			}

		}
	}

}
