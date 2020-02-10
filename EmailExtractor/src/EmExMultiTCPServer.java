import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class EmExMultiTCPServer {
    public static void main(String[] args) {
        int portNumber = 5555; // Default port to use

        if (args.length > 0) {
            if (args.length == 1)
                portNumber = Integer.parseInt(args[0]);
            else {
                System.err.println("Usage: java EchoUcaseServerMutiClients [<port number>]");
                System.exit(1);
            }
        }

        System.out.println("Du er koblet til EmExMultiTCPServeren!");

        try (
                // Create server socket with the given port number
                ServerSocket serverSocket =
                        new ServerSocket(portNumber)
        ) {
            // continuously listening for clients
            while (true) {
                // create and start a new ClientServer thread for each connected client
                ClientService clientserver = new ClientService(serverSocket.accept());
                clientserver.start();
            }
        } catch (IOException e) {

            System.out.println("Exception occurred when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }

    //server for en ny klient
    static class ClientService extends Thread {
        Socket connectSocket;
        InetAddress clientAddr;
        int serverPort, clientPort;

        public ClientService(Socket connectSocket) {
            this.connectSocket = connectSocket;
            clientAddr = connectSocket.getInetAddress();
            clientPort = connectSocket.getPort();
            serverPort = connectSocket.getLocalPort();
        }

        public void run() {
            try (
                    // Create server socket with the given port number
                    PrintWriter out =
                            new PrintWriter(connectSocket.getOutputStream(), true);
                    // Stream reader from the connection socket
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connectSocket.getInputStream()))
            ) {

                String receivedText;
                // read from the connection socket
                while (((receivedText = in.readLine()) != null)) {
                    System.out.println("\n" + clientAddr + " : " + receivedText);
                    String outText = findEmail(receivedText);

                    //Send message to client
                    out.println(outText);
                }

                // close the connection socket
                connectSocket.close();

            } catch (IOException e) {
                System.out.println("Exception occurred when trying to communicate with the client " + clientAddr.getHostAddress());
                System.out.println(e.getMessage());
            }
        }

        private String findEmail(String InnURL) {
            StringBuilder utMelding = new StringBuilder();

            try {
                //Henter URL fra klient
                URL url = new URL(InnURL);

                InputStream stream = new BufferedInputStream(url.openConnection().getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                //Leser htmlen og legger til det som inneholder @ til mailListe
                String tmp;
                while ((tmp = reader.readLine()) != null) {
                    if (tmp.contains("@")) {
                        utMelding.append(tmp).append("\n");
                    }
                }

                //Ser på data i mailListe og returnerer liste eller evt. feilkode
                if (utMelding.length()!=0) {
                    utMelding.append(" Code 0: \n");
                } else {
                    utMelding.append("Code 1: !!!No email address found on the page!!!’");
                }
            } catch (IOException e) {
                utMelding.append(" Code 2: !!!Server couldn’t find the web page!!!");
            }


            System.out.println(utMelding);

            return utMelding.toString();
        }

    }
}
