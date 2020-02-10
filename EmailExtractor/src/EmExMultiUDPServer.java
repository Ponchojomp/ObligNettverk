import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class EmExMultiUDPServer {
    private static final int portNumber=5555;
    private static DatagramSocket datagramSocket;


    private static void run() {
        try {
            String messageIn,messageOut;
            InetAddress clientAddress;
            int clientPort;
            byte[] buffer;
            DatagramPacket inPacket,outPacket;


            while(true) {
                buffer= new byte[16];
                inPacket=new DatagramPacket(buffer,buffer.length);
                datagramSocket.receive(inPacket);
                clientAddress=inPacket.getAddress();
                clientPort=inPacket.getPort();

                messageIn=new String(inPacket.getData(),0,inPacket.getLength());
                System.out.print(clientAddress + " : " + messageIn +"\n");

                messageOut=EmExMultiUDPServer.findEmail(messageIn);
                outPacket=new DatagramPacket(messageOut.getBytes(),messageOut.length(),clientAddress,clientPort);
                datagramSocket.send(outPacket);
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValid (String email){

        String emailRegex = "^[a-zæøåA-ZÆØÅ0-9_+&*-]+(?:\\." +
                "[a-zæøåA-ZÆØÅ0-9_+&*-]+)*@" +
                "(?:[a-zæøåA-ZÆØÅ0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static String findEmail(String InnURL) {
        ArrayList<String> mailListe = new ArrayList<>();
        String utMelding = "";
        try {
            //Henter URL fra klient
            URL url = new URL(InnURL);

            InputStream stream = new BufferedInputStream(url.openConnection().getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            //Leser htmlen og legger til det som inneholder @ til mailListe
            String tmp;

            while ((tmp = reader.readLine()) != null) {
                String[] arrOfStr = tmp.split("<|>|/");

                for (int i = 0; i < arrOfStr.length; i++) {
                    String tekst = arrOfStr[i];
                    if (tekst.contains("@")) {
                        String[] arrOfTekst = tekst.split(":|;|,| ");
                        for (int y = 0; y < arrOfTekst.length; y++) {
                            if (isValid(arrOfTekst[y])) {
                                utMelding += (arrOfTekst[y]) + "\n";
                            }
                        }
                    }
                }
            }


            //Ser på data i mailListe og returnerer liste eller evt. feilkode
            if (utMelding.length() != 0) {
                utMelding = " Code 0: \n" + utMelding;
            } else {
                utMelding += "Code 1: !!!No email address found on the page!!!’";
            }
        } catch (IOException e) {
            utMelding += " Code 2: !!!Server couldnt find the web page!!!";
        }
        for (String i : mailListe) {
            utMelding += i;
        }

        System.out.println(utMelding);

        return utMelding;
    }

    public static void main(String[] args) {
        try {
            datagramSocket=new DatagramSocket(portNumber);
            System.out.println("Koblet til UDP server");
        } catch(SocketException sockEx) {
            System.out.println("Fårn ikke opp");
            System.exit(1);
        }
        run();
    }


}