package co.tutorial.minasocket.impl;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustPrefixedStringDecoder extends PrefixedStringDecoder {

	private static final Logger logger = LoggerFactory.getLogger(CustPrefixedStringDecoder.class);

	public static final int DEFAULT_PREFIX_WIDTH = 8;

	public static final int DEFAULT_PREFIX_WIDTH_MAX = 9;

	private int prefixWidth = DEFAULT_PREFIX_WIDTH;

	private Charset MyCharset;

	public CustPrefixedStringDecoder(Charset charset) {
		super(charset);
		this.MyCharset = charset;
	}

	public CustPrefixedStringDecoder(Charset charset, int prefixWidth) {
		super(charset, calculatprefixLength(prefixWidth));
		this.MyCharset = charset;
	}

	public CustPrefixedStringDecoder(Charset charset, int prefixWidth, int maxDataLength) {
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

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

		logger.debug("decode buff?????? len = {}", in.remaining());
		logger.debug("???????????????????????????:{}",this.getPrefixWidth());
		logger.debug("?????????50??????:[{}]",in.getHexDump(50));

		in.mark();
		if (in.remaining() < this.getPrefixWidth()) {
			logger.debug("?????????????????? ?????????????????????????????????????????????");
			// ????????????????????????????????????????????????????????????
			in.reset();
			return false;
		}

		String headerLen = in.getString(this.getPrefixWidth(), MyCharset.newDecoder());
		int bodyLen = Integer.valueOf(headerLen);
		logger.debug("??????????????????????????????????????????:{}",bodyLen);
		IoBuffer buffer = IoBuffer.allocate(this.getPrefixWidth()).setAutoExpand(true);
		
		buffer.putInt(in.remaining());
		byte[] data = new byte[in.remaining()];
		in.get(data);
		buffer.put(data);
		buffer.flip();

		int prefixLength = calculatprefixLength(this.getPrefixWidth());
		if (buffer.prefixedDataAvailable(prefixLength, this.getMaxDataLength())) {
			String text = buffer.getPrefixedString(prefixLength, MyCharset.newDecoder());
			int textLen = text.length();
			if(textLen < bodyLen) {
				in.reset();
				return false;
			}
			if(textLen > bodyLen) {
				logger.debug("??????????????????:{},??????????????????",textLen);
				text = text.substring(0, bodyLen);
			}
			
			logger.debug("??????????????????:[{}]",text);
			out.write(text);
			return true;
		}

		return false;

	}

}
