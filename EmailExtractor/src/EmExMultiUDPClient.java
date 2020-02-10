
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class EmExMultiUDPClient
{
    public static void main(String[] args) throws IOException
    {

        String hostName = "10.253.31.205"; // Default host, localhost
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

        System.out.println("Hi, I am EchoUCase UDP client!");

        try
                (
                        // Create an UDP/datagram socket for client
                        DatagramSocket clientSocket = new DatagramSocket();

                        // Keyboard reader
                        BufferedReader stdIn =
                                new BufferedReader(
                                        new InputStreamReader(System.in))
                )
        {
            String userInput;
            InetAddress address = InetAddress.getByName(hostName);
            byte[] buf = new byte[1024];
            DatagramPacket packet;

            System.out.print("I (Client) [" + InetAddress.getLocalHost()  + ":" + clientSocket.getLocalPort() + "] > ");
            while ((userInput = stdIn.readLine()) != null && !userInput.isEmpty())
            {
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
                String receivedText = new String(packet.getData());

                System.out.println("Server [" + hostName + ":" + portNumber + "] > " + receivedText.trim());
                System.out.print("I (Client) [" + InetAddress.getLocalHost() + ":" + clientSocket.getLocalPort() + "] > ");
            }
        } catch (UnknownHostException e)
        {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
