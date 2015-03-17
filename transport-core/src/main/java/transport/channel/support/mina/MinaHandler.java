package transport.channel.support.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import transport.channel.ChannelHandler;
import transport.util.Assert;

final class MinaHandler extends IoHandlerAdapter {

    private final ChannelHandler handler;
    
    public MinaHandler(ChannelHandler handler) {
    	Assert.notNull(handler, "handler == null");
        this.handler = handler;
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        MinaChannel channel = MinaChannel.getChannel(session);
        try {
            handler.channelConnected(channel);
        } finally {
            MinaChannel.removeChannelIfDisconnected(session);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        MinaChannel channel = MinaChannel.getChannel(session);
        try {
            handler.channelDisconnected(channel);
        } finally {
            MinaChannel.removeChannelIfDisconnected(session);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        MinaChannel channel = MinaChannel.getChannel(session);
        try {
            handler.messageReceived(channel, message);
        } finally {
            MinaChannel.removeChannelIfDisconnected(session);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        MinaChannel channel = MinaChannel.getChannel(session);
        try {
            handler.messageSent(channel, message);
        } finally {
            MinaChannel.removeChannelIfDisconnected(session);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        MinaChannel channel = MinaChannel.getChannel(session);
        try {
            handler.exceptionCaught(channel, cause);
        } finally {
            MinaChannel.removeChannelIfDisconnected(session);
        }
    }
}
