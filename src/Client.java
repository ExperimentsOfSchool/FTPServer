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
        Socket dataTransfer;
        int dataPort = 0;
        BufferedReader instReader = new BufferedReader(new InputStreamReader(controller.getInputStream()));
        PrintWriter instWriter = new PrintWriter(new OutputStreamWriter(controller.getOutputStream()));
        BufferedReader dataReader;
        PrintWriter dataWriter;
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
        try {
            dataPort = Integer.parseInt(serverResponse);
        } catch (NumberFormatException e) {
            System.out.println("Wrong Response!");
        }
        System.out.println(serverResponse);
        dataTransfer = new Socket("127.0.0.1", dataPort);
//        dataWriter = new PrintWriter(new OutputStreamWriter(dataTransfer.getOutputStream()));
//        dataWriter.println("Hello!");
//        dataWriter.flush();
//        instWriter.println("LIST");
//        instWriter.flush();
//        serverResponse = instReader.readLine();
//        System.out.println("\nCurrent Directory:\n" + serverResponse.replace('&', '\n'));

        instReader.close();
        instWriter.close();
        controller.close();
        dataTransfer.close();

    }
    private static boolean responseAnalyser(String response) {
        if(response.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }
}
