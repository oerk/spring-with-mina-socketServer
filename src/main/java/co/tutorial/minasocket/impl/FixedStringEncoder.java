package co.tutorial.minasocket.impl;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class FixedStringEncoder extends ProtocolEncoderAdapter {
	
	private static final  Logger logger = LoggerFactory.getLogger(FixedStringEncoder.class);
	
	/** The default length for the prefix */
	public static final int DEFAULT_PREFIX_LENGTH = 4;

	/** The default maximum data length */
	public static final int DEFAULT_MAX_DATA_LENGTH = 2048;

	private final Charset charset;

	private int prefixLength = DEFAULT_PREFIX_LENGTH;

	private int maxDataLength = DEFAULT_MAX_DATA_LENGTH;

	/**
	 * Creates a new PrefixedStringEncoder instance
	 * 
	 * @param charset       the {@link Charset} to use for encoding
	 * @param prefixLength  the length of the prefix
	 * @param maxDataLength maximum number of bytes allowed for a single String
	 */
	public FixedStringEncoder(Charset charset, int prefixLength, int maxDataLength) {
		this.charset = charset;
		this.prefixLength = prefixLength;
		this.maxDataLength = (int) (Math.pow(10, prefixLength) - 1);

	}

	/**
	 * Creates a new PrefixedStringEncoder instance
	 * 
	 * @param charset      the {@link Charset} to use for encoding
	 * @param prefixLength the length of the prefix
	 */
	public FixedStringEncoder(Charset charset, int prefixLength) {
		this(charset, prefixLength, DEFAULT_MAX_DATA_LENGTH);
	}

	/**
	 * Creates a new PrefixedStringEncoder instance
	 * 
	 * @param charset the {@link Charset} to use for encoding
	 */
	public FixedStringEncoder(Charset charset) {
		this(charset, DEFAULT_PREFIX_LENGTH);
	}

	/**
	 * Creates a new PrefixedStringEncoder instance
	 */
	public FixedStringEncoder() {
		this(Charset.defaultCharset());
	}

	/**
	 * Sets the number of bytes used by the length prefix
	 *
	 * @param prefixLength the length of the length prefix (1, 2, or 4)
	 */
	public void setPrefixLength(int prefixLength) {
		if (prefixLength != 1 && prefixLength != 2 && prefixLength != 4) {
			throw new IllegalArgumentException("prefixLength: " + prefixLength);
		}
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
	 * Sets the maximum number of bytes allowed for encoding a single String
	 * (including the prefix)
	 * <p>
	 * The encoder will throw a {@link IllegalArgumentException} when more bytes are
	 * needed to encode a String value. The default value is
	 * {@link FixedStringEncoder#DEFAULT_MAX_DATA_LENGTH}.
	 * </p>
	 *
	 * @param maxDataLength maximum number of bytes allowed for encoding a single
	 *                      String
	 */
	public void setMaxDataLength(int maxDataLength) {
		this.maxDataLength = maxDataLength;
	}

	/**
	 * Gets the maximum number of bytes allowed for encoding a single String *
	 *
	 * @return maximum number of bytes allowed for encoding a single String (prefix
	 *         included)
	 */
	public int getMaxDataLength() {
		return maxDataLength;
	}

	/**
	 * {@inheritDoc}
	 */
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		String value = (String) message;
		Integer bodyLen = value.length();
		String format = "%0" + this.getPrefixLength() + "d";
		String headContent = String.format(format, value.length());
		int allLen = bodyLen + this.getPrefixLength();

		String SendMsg = headContent + value;
		logger.debug("要发送的最终内容:" + SendMsg);
		logger.debug("要发送的最终内容长度:" + SendMsg.length());

		IoBuffer buffer = IoBuffer.allocate(allLen).setAutoExpand(true);
		buffer.putString(SendMsg, charset.newEncoder());

		logger.debug("要发送的buff长度:" + buffer.position());

		if (buffer.position() > maxDataLength) {
			throw new IllegalArgumentException("Data length: " + buffer.position());
		}
		buffer.flip();
		out.write(buffer);

	}
}
