import java.net.*;
import java.io.*;

public class EmExMultiUDPServer
{
    public static void main(String[] args) throws IOException
    {

        int portNumber = 5555; // Default port to use

        if (args.length > 0)
        {
            if (args.length == 1)
                portNumber = Integer.parseInt(args[0]);
            else
            {
                System.err.println("Usage: java EchoUcaseServerUDP [<port number>]");
                System.exit(1);
            }
        }

        System.out.println("Hi, I am EchoUCase UDP server!");

        try
                (
                        // create an UDP/datagram socket for server on the given port
                        DatagramSocket serverSocket =
                                new DatagramSocket(portNumber);
                )
        {
            String receivedText;
            do
            {
                byte[] buf = new byte[1024];


                // create datagram packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                // read datagram packet from the socket
                serverSocket.receive(packet);

                // extract text from the packet
                receivedText = new String(packet.getData());
                receivedText = receivedText.trim();

                // convert to uppercase
                String outText = receivedText.toUpperCase();

                // put the processed output text as array of bytes into the buffer
                buf = outText.getBytes();

                // get client's internet "address" and "port" from the hostname from the packet
                InetAddress clientAddr = packet.getAddress();
                int clientPort = packet.getPort();

                System.out.println("Client [" + clientAddr.getHostAddress() +  ":" + clientPort +"] > " + receivedText);

                // create datagram packet with the uppercase text to send back to the client
                packet = new DatagramPacket(buf, buf.length, clientAddr, clientPort);

                // send the uppercase text back to the client
                serverSocket.send(packet);

                System.out.println("I (Server) [" + InetAddress.getLocalHost() + ":" + portNumber + "] > " + outText);
            } while (receivedText != null);

            System.out.println("I am done, Bye!");

        } catch (IOException e)
        {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
