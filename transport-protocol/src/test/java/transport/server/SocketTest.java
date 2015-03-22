package transport.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2014/7/21.
 */
public class SocketTest {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8088);
        while (true) {
            final Socket socket = server.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader in = null;
                    PrintWriter out = null;
                    try {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream());

                        while (true) {
                            String msg = in.readLine();
                            System.out.println(msg);
                            out.println("Serverreceived" + msg);
                            out.flush();
                            if (msg.equals("bye")) {
                                break;
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (Exception e) {

                        }
                        try {
                            out.close();
                        } catch (Exception e) {

                        }
                        try {
                            socket.close();
                        } catch (Exception e) {

                        }
                    }
                }
            }).start();
        }
    }
}
