package transport.channel.support.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.codec.ProtocolEncoderException;

import transport.buffer.ChannelBuffer;
import transport.buffer.ChannelBuffers;
import transport.buffer.DynamicChannelBuffer;
import transport.channel.Channel;
import transport.channel.ChannelFilter;
import transport.util.Assert;
import transport.util.HexUtils;

final class MinaFilter extends IoFilterAdapter {
	
    private final ChannelFilter filter;
    
    private int bufferSize = 1024;
    private ChannelBuffer buffer = ChannelBuffers.EMPTY_BUFFER;
    
    public MinaFilter(ChannelFilter filter) {
        Assert.notNull(filter, "filter == null");
        this.filter = filter;
    }
    
    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object in) throws Exception {
        MinaChannel channel = MinaChannel.getChannel(session);
        Object msg;
        if (!(in instanceof IoBuffer)) {
        	msg = filter.messageReceived(channel, in);
        	nextFilter.messageReceived(session, msg);
            return;
        }

        IoBuffer inBuf = (IoBuffer) in;
        if (!inBuf.hasRemaining()) {
            return;
        }
        
        ChannelBuffer frame;
        if (buffer.readable()) {
            if (buffer instanceof DynamicChannelBuffer) {
                buffer.writeBytes(inBuf.buf());
                frame = buffer;
            } else {
                int size = buffer.readableBytes() + inBuf.remaining();
                frame = ChannelBuffers.dynamicBuffer(size > bufferSize ? size : bufferSize);
                frame.writeBytes(buffer, buffer.readableBytes());
                frame.writeBytes(inBuf.buf());
            }
        } else {
            frame = ChannelBuffers.wrappedBuffer(inBuf.buf());
        }

        try {
        	while (frame.readable()){
        		 //TODO:debug
        		byte[] b = new byte[frame.readableBytes()];
        		frame.getBytes(0,b);
        		System.out.println(HexUtils.byte2hex(b));
        		
        		frame.markReaderIndex();
        		msg = filter.messageReceived(channel, frame);
        		if (msg == ChannelFilter.FilterResult.REPLAY_INPUT) {
        			frame.resetReaderIndex();
        			break;
        		} else {
        			if (msg != null) {
        				nextFilter.messageReceived(session, msg);
        			}
        		}
        	} 
        } finally {
        	if (frame.readable()) {
        		frame.discardReadBytes();
        		buffer = frame;
        	} else {
        		buffer = ChannelBuffers.EMPTY_BUFFER;
        	}
        	MinaChannel.removeChannelIfDisconnected(session);
        }
    	
//    	Object out = null;
//    	try {
//    		Channel channel = MinaChannel.getChannel(session);
//    		if (message instanceof IoBuffer) {
//    			IoBuffer in = (IoBuffer) message;
//    			while (in.hasRemaining()) {
//    				ChannelBuffer frame = ChannelBuffers.directBuffer(in.remaining());
//    				frame.writeBytes(in.buf());
//    				try {
//    					out = filter.messageReceived(channel, frame);
//    				} catch (Throwable t) {
//    					nextFilter.exceptionCaught(session, t);
//    				}
//    			}
//    		} else {
//    			out = filter.messageReceived(channel, message);
//    		}
//    	} finally {
//    		MinaChannel.removeChannelIfDisconnected(session);
//    	}
//    	nextFilter.messageReceived(session, out);
    }

    @Override
    public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {

    	Object message = writeRequest.getMessage();
    	Object out = null;
    	// Bypass the encoding if the message is contained in a IoBuffer,
    	// as it has already been encoded before
    	if ((message instanceof IoBuffer) || (message instanceof FileRegion)) {
    		nextFilter.filterWrite(session, writeRequest);
    		return;
    	}

    	try {
    		Channel channel = MinaChannel.getChannel(session);
    		out = filter.filterWrite(channel, message);
    		if(out instanceof ChannelBuffer){
    			IoBuffer buffer = IoBuffer.wrap(((ChannelBuffer)out).toByteBuffer());
    			nextFilter.filterWrite(session, new DefaultWriteRequest(buffer));
    			return;
    		}
    		
    		nextFilter.filterWrite(session, new DefaultWriteRequest(out));
    	} catch (Throwable t) {
    		ProtocolEncoderException pee;

    		// Generate the correct exception
    		if (t instanceof ProtocolEncoderException) {
    			pee = (ProtocolEncoderException) t;
    		} else {
    			pee = new ProtocolEncoderException(t);
    		}

    		throw pee;
    	}
    }
}
