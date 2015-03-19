/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package transport.channel.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.util.Version;
import transport.channel.Channel;
import transport.channel.ChannelException;
import transport.channel.ChannelHandler;
import transport.channel.Client;
import transport.util.ExecutorUtil;
import transport.util.NamedThreadFactory;
import transport.util.NetUtils;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractClient extends AbstractEndpoint implements Client {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);
    
    protected static final String CLIENT_THREAD_POOL_NAME  ="DubboClientHandler";
    
    private static final AtomicInteger CLIENT_THREAD_POOL_ID = new AtomicInteger();

    private final Lock            connectLock = new ReentrantLock();
    
    private static final ScheduledThreadPoolExecutor reconnectExecutorService = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("DubboClientReconnectTimer", true));
    
    private volatile  ScheduledFuture<?> reconnectExecutorFuture = null;
    
    protected volatile ExecutorService executor;
    
    private final boolean send_reconnect = false;
    
    private final AtomicInteger reconnect_count = new AtomicInteger(0);
    
    //重连的error日志是否已经被调用过.
    private final AtomicBoolean reconnect_error_log_flag = new AtomicBoolean(false) ;
    
    //重连warning的间隔.(waring多少次之后，warning一次) //for test
//    private final int reconnect_warning_period ;
    
    //the last successed connected time
    private long lastConnectedTime = System.currentTimeMillis();
    
//    private final long shutdown_timeout ;
    
    
    public AbstractClient(URL url, ChannelHandler handler) throws ChannelException {
        
        try {
            doOpen();
        } catch (Throwable t) {
            close();
        }
        try {
            // connect.
            connect();
            if (logger.isInfoEnabled()) {
                logger.info("Start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress() + " connect to the server " + getRemoteAddress());
            }
        } catch (ChannelException t) {
        } catch (Throwable t){
            close();
        }
//        
//        executor = (ExecutorService) ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension().get(Constants.CONSUMER_SIDE, Integer.toString(url.getPort()));
//        ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension().remove(Constants.CONSUMER_SIDE, Integer.toString(url.getPort()));
    }
    
    protected static ChannelHandler wrapChannelHandler(URL url, ChannelHandler handler){
//        url = ExecutorUtil.setThreadName(url, CLIENT_THREAD_POOL_NAME);
//        url = url.addParameterIfAbsent(Constants.THREADPOOL_KEY, Constants.DEFAULT_CLIENT_THREADPOOL);
//        return ChannelHandlers.wrap(handler, url);
    	return null;
    }
    
    /**
     * init reconnect thread
     */
    private synchronized void initConnectStatusCheckCommand(){
        //reconnect=false to close reconnect 
        int reconnect = 0;
        if(reconnect > 0 && (reconnectExecutorFuture == null || reconnectExecutorFuture.isCancelled())){
            Runnable connectStatusCheckCommand =  new Runnable() {
                public void run() {
                    try {
                        if (! isConnected()) {
                            connect();
                        } else {
                            lastConnectedTime = System.currentTimeMillis();
                        }
                    } catch (Throwable t) { 
//                        String errorMsg = "client reconnect to "+getUrl().getAddress()+" find error . url: "+ getUrl();
//                        // wait registry sync provider list
//                        if (System.currentTimeMillis() - lastConnectedTime > shutdown_timeout){
//                            if (!reconnect_error_log_flag.get()){
//                                reconnect_error_log_flag.set(true);
//                                logger.error(errorMsg, t);
//                                return ;
//                            }
//                        }
//                        if ( reconnect_count.getAndIncrement() % reconnect_warning_period == 0){
//                            logger.warn(errorMsg, t);
//                        }
                    }
                }
            };
            reconnectExecutorFuture = reconnectExecutorService.scheduleWithFixedDelay(connectStatusCheckCommand, reconnect, reconnect, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * @param url
     * @return 0-false
     */
    private static int getReconnectParam(URL url){
        int reconnect = 0;
//        String param = url.getParameter(Constants.RECONNECT_KEY);
//        if (param == null || param.length()==0 || "true".equalsIgnoreCase(param)){
//            reconnect = Constants.DEFAULT_RECONNECT_PERIOD;
//        }else if ("false".equalsIgnoreCase(param)){
//            reconnect = 0;
//        } else {
//            try{
//                reconnect = Integer.parseInt(param);
//            }catch (Exception e) {
//                throw new IllegalArgumentException("reconnect param must be nonnegative integer or false/true. input is:"+param);
//            }
//            if(reconnect < 0){
//                throw new IllegalArgumentException("reconnect param must be nonnegative integer or false/true. input is:"+param);
//            }
//        }
        return reconnect;
    }
    
    private synchronized void destroyConnectStatusCheckCommand(){
        try {
            if (reconnectExecutorFuture != null && ! reconnectExecutorFuture.isDone()){
                reconnectExecutorFuture.cancel(true);
                reconnectExecutorService.purge();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }
    
    protected ExecutorService createExecutor() {
//        return Executors.newCachedThreadPool(new NamedThreadFactory(CLIENT_THREAD_POOL_NAME + CLIENT_THREAD_POOL_ID.incrementAndGet() + "-" + getUrl().getAddress(), true));
    	return null;
    }
    
    public InetSocketAddress getConnectAddress() {
//        return new InetSocketAddress(NetUtils.filterLocalHost(getUrl().getHost()), getUrl().getPort());
    	return null;
    }

    public InetSocketAddress getRemoteAddress() {
        Channel channel = getChannel();
        return channel.getRemoteAddress();
    }

    public InetSocketAddress getLocalAddress() {
        Channel channel = getChannel();
        if (channel == null)
            return InetSocketAddress.createUnresolved(NetUtils.getLocalHost(), 0);
        return channel.getLocalAddress();
    }

    public boolean isConnected() {
        Channel channel = getChannel();
        if (channel == null)
            return false;
        return channel.isConnected();
    }

    public Object getAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null)
            return null;
        return channel.getAttribute(key);
    }

    public void setAttribute(String key, Object value) {
        Channel channel = getChannel();
        if (channel == null)
            return;
        channel.setAttribute(key, value);
    }

    public void removeAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null)
            return;
        channel.removeAttribute(key);
    }

    public boolean hasAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null)
            return false;
        return channel.containsAttribute(key);
    }
    
    public void send(Object message, boolean sent) throws ChannelException {
        if (send_reconnect && !isConnected()){
            connect();
        }
        Channel channel = getChannel();
        //TODO getChannel返回的状态是否包含null需要改进
        if (channel == null || ! channel.isConnected()) {
//          throw new ChannelException(this, "message can not send, because channel is closed .");
        }
        channel.send(message, sent);
    }
    
    public void connect() throws ChannelException {
        connectLock.lock();
        try {
            if (isConnected()) {
                return;
            }
            initConnectStatusCheckCommand();
            doConnect();
//            if (! isConnected()) {
//                throw new ChannelException(this, "Failed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + " "
//                                            + NetUtils.getLocalHost() + " using dubbo version " + Version.getVersion()
//                                            + ", cause: Connect wait timeout: " + getTimeout() + "ms.");
//            } else {
//            	if (logger.isInfoEnabled()){
//            		logger.info("Successed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + " "
//                                            + NetUtils.getLocalHost() + " using dubbo version " + Version.getVersion()
//                                            + ", channel is " + this.getChannel());
//            	}
//            }
            reconnect_count.set(0);
            reconnect_error_log_flag.set(false);
        } catch (ChannelException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("Failed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + " "
                                        + NetUtils.getLocalHost() + " using dubbo version " + Version.getVersion()
                                        + ", cause: " + e.getMessage(), e);
        } finally {
            connectLock.unlock();
        }
    }

    public void disconnect() {
        connectLock.lock();
        try {
            destroyConnectStatusCheckCommand();
            try {
                Channel channel = getChannel();
                if (channel != null) {
                    channel.close();
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
            try {
                doDisConnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        } finally {
            connectLock.unlock();
        }
    }
    
    public void reconnect() throws ChannelException {
        disconnect();
        connect();
    }

    public void close() {
    	try {
    		if (executor != null) {
    			ExecutorUtil.shutdownNow(executor, 100);
    		}
    	} catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
//            super.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
        	disconnect();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            doClose();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public void close(int timeout) {
        ExecutorUtil.gracefulShutdown(executor ,timeout);
        close();
    }
    
    @Override
    public String toString() {
        return getClass().getName() + " [" + getLocalAddress() + " -> " + getRemoteAddress() + "]";
    }

    /**
     * Open client.
     * 
     * @throws Throwable
     */
    protected abstract void doOpen() throws Throwable;

    /**
     * Close client.
     * 
     * @throws Throwable
     */
    protected abstract void doClose() throws Throwable;

    /**
     * Connect to server.
     * 
     * @throws Throwable
     */
    protected abstract void doConnect() throws Throwable;
    
    /**
     * disConnect to server.
     * 
     * @throws Throwable
     */
    protected abstract void doDisConnect() throws Throwable;

    /**
     * Get the connected channel.
     * 
     * @return channel
     */
    protected abstract Channel getChannel();

}