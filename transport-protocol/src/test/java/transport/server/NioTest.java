package transport.server;

/**
 * Created by Administrator on 2014/7/21.
 */
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Properties;

public class NioTest {

    public static final int PORT = 8088;

    protected Selector selector;
    protected Charset charset = Charset.forName("UTF-8");
    protected CharsetEncoder charsetEncoder = charset.newEncoder();
    protected CharsetDecoder charsetDecoder = charset.newDecoder();

    protected Properties talks = new Properties();

    int clientCount;

    public NioTest() throws Exception {

        selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT)); // port
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);// register

        System.out.println("Server localhost:" + PORT + " started. waiting for clients. ");

        while (true) {
            // selector 线程。select() 会阻塞，直到有客户端连接，或者有消息读入
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {

                SelectionKey selectionKey = iterator.next();
                iterator.remove(); // 删除此消息

                // 并在当前线程内处理。（为了高效，一般会在另一个线程中处理此消息，例如使用线程池等）
                handleSelectionKey(selectionKey);
            }
        }

    }

    public void handleSelectionKey(SelectionKey selectionKey) throws Exception {

        if (selectionKey.isAcceptable()) {

            // 有客户端进来
            clientCount++;

            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            Socket socket = socketChannel.socket();

            // 立即注册一个 OP_READ 的SelectionKey, 接收客户端的消息
            SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
            key.attach("第 " + clientCount + " 个客户端 [" + socket.getRemoteSocketAddress() + "]: ");

            System.out.println(key.attachment() + "\t[connected] =========================================");

        } else if (selectionKey.isReadable()) {

            // 有消息进来

            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

            try {
                int len = socketChannel.read(byteBuffer);

                // 如果len>0，表示有输入。如果len==0, 表示输入结束。需要关闭 socketChannel
                if (len > 0) {

                    byteBuffer.flip();
                    String msg = charsetDecoder.decode(byteBuffer).toString();
                    System.out.println(msg);
                } else {
                    // 输入结束，关闭 socketChannel
                    System.out.println(selectionKey.attachment() + "read finished. close socketChannel. ");
                    socketChannel.close();
                }

            } catch (Exception e) {

                // 如果read抛出异常，表示连接异常中断，需要关闭 socketChannel
                e.printStackTrace();

                System.out.println(selectionKey.attachment() + "socket closed? ");
                socketChannel.close();
            }

        } else if (selectionKey.isWritable()) {
            System.out.println(selectionKey.attachment() + "TODO: isWritable() ???????????????????????????? ");
        } else if (selectionKey.isConnectable()) {
            System.out.println(selectionKey.attachment() + "TODO: isConnectable() ????????????????????????? ");
        } else {
            System.out.println(selectionKey.attachment() + "TODO: else. ");
        }

    }

    public static void main(String[] args) throws Exception {
        new NioTest();
    }

}