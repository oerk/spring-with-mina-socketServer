
package co.tutorial.minasocket.impl;

import org.apache.mina.core.buffer.BufferDataException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class FixedStringDecoder extends CumulativeProtocolDecoder {

	private static final Logger logger = LoggerFactory.getLogger(FixedStringDecoder.class);

	/** The default length for the prefix */
	public static final int DEFAULT_PREFIX_LENGTH = 4;

	/** The default maximum data length */
	public static final int DEFAULT_MAX_DATA_LENGTH = 2048;

	private final Charset charset;

	private int prefixLength = DEFAULT_PREFIX_LENGTH;

	private int maxDataLength = DEFAULT_MAX_DATA_LENGTH;

	/**
	 * Creates a new PrefixedStringDecoder instance
	 * 
	 * @param charset       the {@link Charset} to use for decoding
	 * @param prefixLength  the length of the prefix
	 * @param maxDataLength maximum number of bytes allowed for a single String
	 */
	public FixedStringDecoder(Charset charset, int prefixLength, int maxDataLength) {
		this.charset = charset;
		this.prefixLength = prefixLength;
		this.maxDataLength = (int) (Math.pow(10, prefixLength) - 1);
	}

	/**
	 * Creates a new PrefixedStringDecoder instance
	 * 
	 * @param charset      the {@link Charset} to use for decoding
	 * @param prefixLength the length of the prefix
	 */
	public FixedStringDecoder(Charset charset, int prefixLength) {
		this(charset, prefixLength, DEFAULT_MAX_DATA_LENGTH);
	}

	/**
	 * Creates a new PrefixedStringDecoder instance
	 * 
	 * @param charset the {@link Charset} to use for decoding
	 */
	public FixedStringDecoder(Charset charset) {
		this(charset, DEFAULT_PREFIX_LENGTH);
	}

	/**
	 * Sets the number of bytes used by the length prefix
	 *
	 * @param prefixLength the length of the length prefix (1, 2, or 4)
	 */
	public void setPrefixLength(int prefixLength) {
		this.prefixLength = prefixLength;
	}

	/**
	 * Gets the length of the length prefix (1, 2, or 4)
	 *
	 * @return length of the length prefix
	 */
	public int getPrefixLength() {
		return prefixLength;
	}

	/**
	 * Sets the maximum allowed value specified as data length in the incoming data
	 * <p>
	 * Useful for preventing an OutOfMemory attack by the peer. The decoder will
	 * throw a {@link BufferDataException} when data length specified in the
	 * incoming data is greater than maxDataLength The default value is
	 * {@link FixedStringDecoder#DEFAULT_MAX_DATA_LENGTH}.
	 * </p>
	 *
	 * @param maxDataLength maximum allowed value specified as data length in the
	 *                      incoming data
	 */
	public void setMaxDataLength(int maxDataLength) {
		this.maxDataLength = maxDataLength;
	}

	/**
	 * Gets the maximum number of bytes allowed for a single String
	 *
	 * @return maximum number of bytes allowed for a single String
	 */
	public int getMaxDataLength() {
		return maxDataLength;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		logger.debug("decode buff总长 len = {}",in.remaining());

		in.mark();
		if (in.remaining() < this.getPrefixLength()) {
			logger.debug("消息长度总长 小于消息头长报文不完整重新读取");
			// 代表报文不完整，需要再次读取缓冲区的数据
			in.reset();
			return false;
		}

		String headerLen = in.getString(this.getPrefixLength(), this.charset.newDecoder());
		int bodyLen = Integer.valueOf(headerLen);

		if (in.remaining() < bodyLen) {
			logger.debug("消息体度总不完整重新读取");
			// 代表报文不完整，需要再次读取缓冲区的数据
			in.reset();
			return false;
		}

		logger.debug("解析的请求报文长度为:{}", bodyLen);

		// 按编码转换为字符串
		String msg = in.getString(in.remaining(), this.charset.newDecoder());

		if (bodyLen > msg.length()) {
			in.reset();
			logger.debug("当前字符串长度:{}小于内容长度:{},等待读取更多内容", msg.length(), bodyLen);
			return false;
		}

		// 如果内容长度超过报文头的长度丢弃
		if (in.remaining() > 0) {
			String dropString = in.getString(in.remaining(), this.charset.newDecoder());
			logger.debug("丢弃剩余内容:{}", dropString);
		}
		out.write(msg);
		return true;
	}

}
