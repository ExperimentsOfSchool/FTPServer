import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * Created by Lawrence on 3/9/16.
 */
public class FTPClienet {
    private static Socket controller;
    private static Socket transfer;
    private static int dataPort = 0;
    private static int controlPort = 0;
    private static String serverAddress;
    public static void main(String[] args) throws IOException {
        //Establish Control Connection.
        Scanner scanner;
        while(true) {
            scanner = new Scanner(System.in);
            System.out.println("Please Input Server Address:");
            serverAddress = scanner.nextLine();
            System.out.println("Please Input Server Port:");
            controlPort = scanner.nextInt();
            try {
                controller = new Socket(serverAddress, controlPort);
                break;
            } catch(Exception e) {
                System.out.println("Connection Failed!");
            }
        }
        System.out.println("Connected!");
        String inst;
        String response;
        BufferedReader instReader = new BufferedReader(new InputStreamReader(controller.getInputStream()));
        PrintWriter instWriter = new PrintWriter(new OutputStreamWriter(controller.getOutputStream()));
        while(true) {
            scanner = new Scanner(System.in);
            inst = scanner.nextLine();
            if(inst != null && !inst.equals("")) {
                if(inst.equals("QUIT")) {
                    instWriter.println(inst);
                    instWriter.flush();
                    controller.close();
                    if(transfer != null) {
                        transfer.close();
                    }
                    break;
                } else {
                    StringTokenizer cutter = new StringTokenizer(inst);
                    String instHeader = cutter.nextToken();
                    String instPara = cutter.hasMoreTokens() ? cutter.nextToken() : "";
                    switch(instHeader) {
                        case "USER":
                            instWriter.println(inst);
                            instWriter.flush();
                            response = instReader.readLine();
                            System.out.println(response);
                            break;
                        case "PASS":
                            instWriter.println(inst);
                            instWriter.flush();
                            response = instReader.readLine();
                            System.out.println(response);
                            if(response.equals("Wrong Password! Connection Closed!")) {
                                controller.close();
                                System.exit(0);
                            }
                            break;
                        case "PASV":
                            instWriter.println(inst);
                            instWriter.flush();
                            response = instReader.readLine();
                            try {
                                dataPort = Integer.parseInt(response);
                                transfer = new Socket(serverAddress, dataPort);
                                System.out.println("Data Transfer Port: " + dataPort);
                            } catch (NumberFormatException e) {
                                System.out.println(response);
                            }
                            break;
                        case "LIST":
                            instWriter.println(inst);
                            instWriter.flush();
                            response = instReader.readLine();
                            System.out.println(response.replace('^', '\n'));
                            break;
                    }
                }
            }
        }

    }
}
