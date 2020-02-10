
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class EmExMultiUDPClient
{
    public static void main(String[] args) throws IOException
    {

        String hostName = "192.168.1.39"; // Default host, localhost
        int portNumber = 5555; // Default port to use
        if (args.length > 0)
        {
            hostName = args[0];
            if (args.length > 1)
            {
                portNumber = Integer.parseInt(args[1]);
                if (args.length > 2)
                {
                    System.err.println("Usage: java EchoClientTCP [<host name>] [<port number>]");
                    System.exit(1);
                }
            }
        }

        System.out.println("Koblet til UDP server!");

        try( DatagramSocket clientSocket = new DatagramSocket();
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
            )
        {
            String userInput;
            InetAddress address = InetAddress.getByName(hostName);
            byte[] buf = new byte[1024];
            DatagramPacket packet;

            System.out.print("I (Client) [" + InetAddress.getLocalHost()  + ":" + clientSocket.getLocalPort() + "] > ");
            while ((userInput = stdIn.readLine()) != null && !userInput.isEmpty()){

                // create datagram packet with the input text
                buf = userInput.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, portNumber);
                // send the packet
                clientSocket.send(packet);
                // clear buffer for the next reading
                Arrays.fill( buf, (byte) 0 );
                // read reply text from the socket
                clientSocket.receive(packet);
                // read received text
                String receivedText = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Server [" + hostName + ":" + portNumber + "] > " + receivedText);
                System.out.print("I (Client) [" + InetAddress.getLocalHost() + ":" + clientSocket.getLocalPort() + "] > Skriv inn URL: ");
            }
        } catch (UnknownHostException e){
            System.err.println("Kjenner ikke host " + hostName);
            System.exit(1);
        } catch (IOException e){
            System.err.println("Fikk ikke I/O for tilkobling til " +
                    hostName);
            System.exit(1);
        }
    }
}
