import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * Created by Lawrence on 3/9/16.
 */
public class FTPServer {
    private static boolean loginStatus = false;
    private static boolean authStatus = false;
    private static boolean dataStatus = false;
    private static boolean closeStatus = false;
    private static ServerSocket controller = null;
    private static ServerSocket transfer = null;
    private static Socket contSocket = null;
    private static Socket transSocket = null;
    private static String instHeader = "";
    private static String instPara = "";
    private static String strDir = "./ftp";
    private static File currentDirectory = null;
    private static File currentFile = null;
    private static int dataPort = 0;

    public static void main(String[] args) throws IOException, NullPointerException {
        controller = new ServerSocket(2333);
        BufferedReader instReader;
        PrintWriter instWriter;
        String clientInst;
        while(true) {
            try {
                contSocket = controller.accept();
                instReader = new BufferedReader(new InputStreamReader(contSocket.getInputStream()));
                instWriter = new PrintWriter(new OutputStreamWriter(contSocket.getOutputStream()));
                authStatus = false;
                while((clientInst = instReader.readLine()) != null) {
                    System.out.println(clientInst); //Inst Log
                    StringTokenizer instAnal = new StringTokenizer(clientInst);
                    instHeader = instAnal.nextToken();
                    instPara = instAnal.hasMoreTokens() ? instAnal.nextToken() : "";
                    switch(instHeader) {
                        case "USER":
                            if(instPara.equals("root")) {
                                loginStatus = true;
                                instWriter.println("Logging in... Username: " + instPara);
                                instWriter.flush();
                            } else {
                                instWriter.println("No Such Users!");
                                instWriter.flush();
                            }
                            break;
                        case "PASS":
                            if(loginStatus) {
                                if(instPara.equals("root")) {
                                    loginStatus = false;
                                    authStatus = true;
                                    instWriter.println("OK!");
                                    instWriter.flush();
                                } else {
                                    instWriter.println("Wrong Password! Connection Closed!");
                                    instWriter.flush();
                                    closeStatus = true;
                                }
                            } else {
                                instWriter.println("Operation Refused!");
                                instWriter.flush();
                            }
                            break;
                        case "PASV":
                            if(!authStatus) {
                                instWriter.println("Operation Refused!");
                                instWriter.flush();
                                transfer = null;
                                break;
                            }
                            Random generator = new Random();
                            if(transfer != null) {
                                instWriter.println("Connection Already Set!");
                                instWriter.flush();
                            } else {
                                while (true) {
                                    int portHigh = 1 + generator.nextInt(20);
                                    int portLow = 100 + generator.nextInt(1000);
                                    dataPort = portHigh * 256 + portLow;
                                    try {
                                        transfer = new ServerSocket(dataPort);
                                        currentDirectory = new File(strDir);
                                        dataStatus = true;
                                        break;
                                    } catch (IOException e) {
                                        e.printStackTrace();
//                                    continue;
                                    }
                                }
                                instWriter.println(dataPort + "");
                                instWriter.flush();
                                transSocket = transfer.accept();
                            }
                            break;
                        case "LIST":
                            if(!dataStatus) {
                                instWriter.println("Operation Refused!");
                                instWriter.flush();
                                break;
                            }
                            if(currentDirectory.isDirectory()) {
                                File[] list = currentDirectory.listFiles();
                                String str = "";
                                if(list == null) {
                                    str = "";
                                } else {
                                    for(int i = 0; i < list.length; i++) {
                                        if(list[i].isDirectory()) {
                                            str += "\t" + list[i].getName() + "\t\t\tDIR^";
                                        } else {
                                            str += "\t" + list[i].getName() + "^";
                                        }
                                    }
                                }
                                instWriter.println(str);
                                instWriter.flush();
                            } else {
                                instWriter.println("System Error!");
                                instWriter.flush();
                            }
                            break;
                        case "RETR":
                            if(!dataStatus) {
                                instWriter.println("Operation Refused!");
                                instWriter.flush();
                                break;
                            }
                            if(currentDirectory.isDirectory()) {
                                String fileName = instPara;
                                File[] list = currentDirectory.listFiles();
                                boolean hasFile = false;

                                if(list != null) {
                                    for(File file: list) {
                                        if(file.getName().equals(fileName)) {
                                            currentFile = file;
                                            hasFile = true;
                                            break;
                                        }
                                    }
                                    if(!hasFile) {
                                        instWriter.println("No such file.");
                                        instWriter.flush();
                                    } else {
                                        if(currentFile.isDirectory()) {
                                            instWriter.println("Is a Directory!");
                                        } else {
                                            instWriter.println("OK");
                                            instWriter.flush();
                                            DataOutputStream dout;
                                            dout = new DataOutputStream(transSocket.getOutputStream());
                                            FileInputStream fin = new FileInputStream(currentFile);
                                            byte[] sendByte = new byte[1024];
                                            int length;
                                            dout.writeUTF(currentFile.getName());
                                            while((length = fin.read(sendByte, 0, sendByte.length)) >= 0) {
                                                dout.write(sendByte, 0, length);
                                                dout.flush();
                                            }
                                            dout.close();
                                            fin.close();
                                            transSocket = transfer.accept();

                                        }
                                    }
                                } else {
                                    instWriter.println("System Error!");
                                    instWriter.flush();
                                }
                            } else {
                                instWriter.println("System Error!");
                                instWriter.flush();
                            }
                            break;
                        case "STOR":
                            if(dataStatus) {
                                instWriter.println("OK");
                                instWriter.flush();
                                String response;
                                if((response = instReader.readLine()).equals("START")) {
                                    byte[] inputByte = new byte[1024];
                                    int length = 0;
                                    DataInputStream din;
                                    FileOutputStream fout = null;
                                    din = new DataInputStream(transSocket.getInputStream());
                                    fout = new FileOutputStream(new File("./ftp/" + din.readUTF()));
//                                System.out.println("Receiving...");
                                    while(true) {

                                        length = din.read(inputByte, 0, inputByte.length);

                                        if(length == -1) {
                                            break;
                                        }
//                                    System.out.println(length);
                                        fout.write(inputByte, 0, length);
                                        fout.flush();
                                    }
//                                System.out.println("Complete!");
                                    fout.close();
                                    din.close();
                                    transSocket = transfer.accept();
                                } else {
                                    System.out.println(response);
                                }
                            } else {
                                instWriter.println("Operation Refused!");
                                instWriter.flush();
                            }
                            break;
                        case "QUIT":
                            closeStatus = true;
                            break;
                        default:
                            instWriter.println("Wrong Operation!");
                            instWriter.flush();
                    }
                    if(closeStatus) break;
                }
                resetSocket();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void resetSocket() throws IOException, NullPointerException {
        System.out.println("Closing...");
        contSocket.close();
        if(transSocket != null) {
            transSocket.close();
            transfer.close();
            transSocket = null;
            transfer = null;
        }
        loginStatus = false;
        authStatus = false;
        closeStatus = false;
        dataStatus = false;
        contSocket = null;
        instHeader = "";
        instPara = "";
        strDir = "./ftp";
        currentDirectory = null;
        currentFile = null;
        dataPort = 0;

    }
}
