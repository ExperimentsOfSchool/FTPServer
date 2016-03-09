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
                        case "RETR":
                            instWriter.println(inst);
                            instWriter.flush();
                            response = instReader.readLine();
                            if(response.equals("OK")) {
                                byte[] inputByte = new byte[1024];
                                int length = 0;
                                DataInputStream din;
                                FileOutputStream fout = null;
                                din = new DataInputStream(transfer.getInputStream());
                                fout = new FileOutputStream(new File("./" + din.readUTF()));
                                System.out.println("Receiving...");
                                while(true) {
                                    if(din != null) {
                                        length = din.read(inputByte, 0, inputByte.length);
                                    }
                                    if(length == -1) {
                                        break;
                                    }
                                    System.out.println(length);
                                    fout.write(inputByte, 0, length);
                                    fout.flush();
                                }
                                System.out.println("Complete!");
                                fout.close();
                                din.close();
                                transfer = new Socket(serverAddress, dataPort);
                            } else {
                                System.out.println(response);
                            }
                            break;
                    }
                }
            }
        }

    }
}
