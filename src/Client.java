import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Lawrence on 3/8/16.
 *
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket controller = new Socket("127.0.0.1", 2333);
//        Socket dataTransfer = new Socket("127.0.0.1", 20);
        BufferedReader instReader = new BufferedReader(new InputStreamReader(controller.getInputStream()));
        PrintWriter instWriter = new PrintWriter(new OutputStreamWriter(controller.getOutputStream()));
        String serverResponse = null;
        instWriter.println("USER root");
        instWriter.flush();
        serverResponse = instReader.readLine();

        System.out.println(serverResponse);
        instWriter.println("PASS root");
        instWriter.flush();
        serverResponse = instReader.readLine();
        System.out.println(serverResponse);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            System.err.println(e);
//        }
        instWriter.println("PASV");
        instWriter.flush();
        serverResponse = instReader.readLine();
        System.out.println(serverResponse);


        instReader.close();
        instWriter.close();
        controller.close();

    }
    private static boolean responseAnalyser(String response) {
        if(response.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }
}
