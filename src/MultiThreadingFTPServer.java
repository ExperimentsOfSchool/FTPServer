import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lawrence on 3/10/16.
 *
 */
public class MultiThreadingFTPServer{
    public static void main(String[] args) throws IOException {
        ServerSocket controller = null;
        try {
            controller = new ServerSocket(2333);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while(true) {
            try {
                Socket contSocket = controller.accept();
                new FTPServer(contSocket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
