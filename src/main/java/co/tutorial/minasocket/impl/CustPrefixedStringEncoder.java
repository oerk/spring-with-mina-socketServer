package co.tutorial.minasocket.impl;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustPrefixedStringEncoder extends PrefixedStringEncoder {

	private static final Logger logger = LoggerFactory.getLogger(CustPrefixedStringEncoder.class);

	public static final int DEFAULT_PREFIX_WIDTH = 8;

	public static final int DEFAULT_PREFIX_WIDTH_MAX = 9;

	private int prefixWidth = DEFAULT_PREFIX_WIDTH;

	private Charset MyCharset;

	public CustPrefixedStringEncoder(Charset charset) {
		super(charset);
		this.MyCharset = charset;
	}

	public CustPrefixedStringEncoder(Charset charset, int prefixWidth) {
		super(charset, calculatprefixLength(prefixWidth));
		this.MyCharset = charset;
	}

	public CustPrefixedStringEncoder(Charset charset, int prefixWidth, int maxDataLength) {
		super(charset, calculatprefixLength(prefixWidth), maxDataLength);
		this.MyCharset = charset;
	}

	public static int calculatMaxDataLength(int prefixWidth) {
		return (int) (Math.pow(10, prefixWidth) - 1);
	}

	public static int calculatprefixLength(int prefixWidth) {

		if (prefixWidth > DEFAULT_PREFIX_WIDTH_MAX)
			throw new IllegalArgumentException("prefixWidth: " + prefixWidth);

		if (prefixWidth < 1)
			throw new IllegalArgumentException("prefixWidth: " + prefixWidth);

		int prefixval = 4;

		if (calculatMaxDataLength(prefixWidth) < 256) {
			prefixval = 1;
		}

		if (calculatMaxDataLength(prefixWidth) < 65536) {
			prefixval = 2;
		}

		if (calculatMaxDataLength(prefixWidth) < Integer.MAX_VALUE + 1) {
			prefixval = 4;
		}
		return prefixval;

	}

	public int getPrefixWidth() {
		return prefixWidth;
	}

	public void setPrefixWidth(int prefixWidth) {
		this.prefixWidth = prefixWidth;
	}

	
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		String value = (String) message;
		Integer bodyLen = value.length();
		logger.debug("编码的消息头宽度:{}",this.getPrefixWidth());
		String format = "%0" + this.getPrefixWidth() + "d";
		String headContent = String.format(format, value.length());
		int allLen = bodyLen + this.getPrefixWidth();

		String SendMsg = headContent + value;
		logger.debug("要发送的最终内容:[{}]",SendMsg);
		logger.debug("要发送的最终内容长度:{}",SendMsg.length());

		IoBuffer buffer = IoBuffer.allocate(allLen).setAutoExpand(true);
		buffer.putString(SendMsg, MyCharset.newEncoder());

		logger.debug("要发送的buff长度:{}",buffer.position());

		if (buffer.position() > this.getMaxDataLength()) {
			throw new IllegalArgumentException("Data length: " + buffer.position());
		}
		buffer.flip();
		out.write(buffer);

	}
	
}
