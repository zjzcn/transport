package transport.protocol;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import transport.protocol.support.ServerBootstrap;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2014/6/19.
 */
public class Main{
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/*.xml");

        Bootstrap server = new ServerBootstrap("linkage", SpringContext.getApplicationContext());
        server.setLog(true);
        server.startup(new InetSocketAddress(9090));

        Bootstrap server1 = new ServerBootstrap("linkage", context);
        server1.setTransport("netty");
//        server1.setTcpOrUdp("udp");
        server1.setLog(true);
        server1.startup(new InetSocketAddress(9099));

        System.gc();

        System.in.read();
    }
}
