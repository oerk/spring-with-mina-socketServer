package co.tutorial.minasocket.impl;

import org.apache.mina.core.buffer.BufferDataException;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;


public class FixedStringCodecFactory implements ProtocolCodecFactory {
	
    /** The default length for the prefix */
    public static final int DEFAULT_PREFIX_LENGTH = 4;


    private final Charset charset;

    private int prefixLength = DEFAULT_PREFIX_LENGTH;
	
    private final FixedStringEncoder encoder;

    private final FixedStringDecoder decoder;

    
    public FixedStringCodecFactory(Charset charset, int prefixLength) {
        this.charset = charset;
    	this.prefixLength = prefixLength;
        encoder = new FixedStringEncoder(charset,prefixLength);
        decoder = new FixedStringDecoder(charset,prefixLength);
    }
    
    /**
     * Creates a new PrefixedStringCodecFactory instance
     * 
     * @param charset The {@link Charset} to use for encoding or decoding
     */
    public FixedStringCodecFactory(Charset charset) {
    	  this(charset, DEFAULT_PREFIX_LENGTH);
    }

    /**
     * Creates a new PrefixedStringCodecFactory instance
     */
    public FixedStringCodecFactory() {
        this(Charset.defaultCharset());
    }

    
    
    public int getPrefixLength() {
		return prefixLength;
	}

	public void setPrefixLength(int prefixLength) {
		this.prefixLength = prefixLength;
	}

	public Charset getCharset() {
		return charset;
	}

	public FixedStringEncoder getEncoder() {
		return encoder;
	}

	public FixedStringDecoder getDecoder() {
		return decoder;
	}

	/**
     * Returns the allowed maximum size of an encoded string.
     * If the size of the encoded String exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.
     * The default value is {@link PrefixedStringEncoder#DEFAULT_MAX_DATA_LENGTH}.
     * <p>
     * This method does the same job as {@link PrefixedStringEncoder#setMaxDataLength(int)}.
     *
     * @return the allowed maximum size of an encoded string.
     */
    public int getEncoderMaxDataLength() {
        return encoder.getMaxDataLength();
    }

    /**
     * Sets the allowed maximum size of an encoded String.
     * If the size of the encoded String exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.
     * The default value is {@link PrefixedStringEncoder#DEFAULT_MAX_DATA_LENGTH}.
     * <p>
     * This method does the same job as {@link PrefixedStringEncoder#getMaxDataLength()}.
     *
     * @param maxDataLength allowed maximum size of an encoded String.
     */
    public void setEncoderMaxDataLength(int maxDataLength) {
        encoder.setMaxDataLength(maxDataLength);
    }

    /**
     * @return the allowed maximum size of a decoded string.
     * <p>
     * This method does the same job as {@link PrefixedStringEncoder#setMaxDataLength(int)}.
     * @see #setDecoderMaxDataLength(int)
     */
    public int getDecoderMaxDataLength() {
        return decoder.getMaxDataLength();
    }

    /**
     * Sets the maximum allowed value specified as data length in the decoded data
     * <p>
     * Useful for preventing an OutOfMemory attack by the peer.
     * The decoder will throw a {@link BufferDataException} when data length
     * specified in the incoming data is greater than maxDataLength
     * The default value is {@link PrefixedStringDecoder#DEFAULT_MAX_DATA_LENGTH}.
     * <p>
     * This method does the same job as {@link PrefixedStringDecoder#setMaxDataLength(int)}.
     *
     * @param maxDataLength maximum allowed value specified as data length in the incoming data
     */
    public void setDecoderMaxDataLength(int maxDataLength) {
        decoder.setMaxDataLength(maxDataLength);
    }

    /**
     * Sets the length of the prefix used by the decoder
     *
     * @param prefixLength the length of the length prefix (1, 2, or 4)
     */
    public void setDecoderPrefixLength(int prefixLength) {
        decoder.setPrefixLength(prefixLength);
    }

    /**
     * Gets the length of the length prefix (1, 2, or 4) used by the decoder
     *
     * @return length of the length prefix
     */
    public int getDecoderPrefixLength() {
        return decoder.getPrefixLength();
    }

    /**
     * Sets the length of the prefix used by the encoder
     *
     * @param prefixLength the length of the length prefix (1, 2, or 4)
     */
    public void setEncoderPrefixLength(int prefixLength) {
        encoder.setPrefixLength(prefixLength);
    }

    /**
     * Gets the length of the length prefix (1, 2, or 4) used by the encoder
     *
     * @return length of the length prefix
     */
    public int getEncoderPrefixLength() {
        return encoder.getPrefixLength();
    }

    /**
     * {@inheritDoc}
     */
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    /**
     * {@inheritDoc}
     */
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }
}
