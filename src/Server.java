import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Lawrence on 3/8/16.
 *
 */
public class Server {
    private static boolean authStatus;
    public static void main(String[] args) throws IOException {
        ServerSocket controller = new ServerSocket(2333);
        ServerSocket dataTransfer = null;
        Socket contSocket;
        BufferedReader instReader;
        PrintWriter instWriter;
        String clientResponse;
        while(true) {
            try {
                contSocket = controller.accept();
                instReader = new BufferedReader(new InputStreamReader(contSocket.getInputStream()));
                instWriter = new PrintWriter(new OutputStreamWriter(contSocket.getOutputStream()));
                clientResponse = null;
                authStatus = false;
                while((clientResponse = instReader.readLine()) != null) {
                    System.out.println(clientResponse);
                    instWriter.println(instAnalyser(clientResponse));
                    instWriter.flush();
                }
                instReader.close();
                instWriter.close();
                contSocket.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
    private static String instAnalyser(String inst) {
        StringTokenizer cutter = new StringTokenizer(inst);
        String instAbbr = null;
        switch (instAbbr = cutter.nextToken()) {
            case "USER":
                if(cutter.nextToken().equals("root")) {
                    return "OK!";
                } else {
                    return "Error: Wrong Name!";
                }
//                break;
            case "PASS":
                if(cutter.nextToken().equals("root")) {
                    authStatus = true;
                    return "OK!";
                } else {
                    authStatus = false;
                    return "Error: Wrong Password!";
                }
//                break;
            default:
                return dataTransfer(instAbbr);
        }
    }
    private static String dataTransfer(String instAbbr) {
        if (instAbbr.equals("PASV")) {
            return "OK!";
        } else {
            return "Error: Wrong Instruction";
        }
    }
}
