package robot;

import java.net.*;
import java.io.*;

/**
 * TODO: nemá potvrzovací číslo v intervalu <seq - velikost okénka, seq> kde seq
 * je sekvenční číslo příjemce
 *
 * @author Adam Plansky if you have some question please contact me:
 * plansada@fit.cvut.cz
 *
 *
 */
//It is DEVELOPER BRANCH
public class Robot {

    public static void main(String[] args) {

        try {
            Client c;
            //Client c = new Client("192.168.10.211", 4000);
            //147.32.232.173 = baryk.fit.cvut.cz 3261
            //192.168.10.211 = localhost 4000
            System.out.println("---------------------------------------------------------------");
            c = new Client("localhost", 4000);
            for(int i = 0 ; i < 270 ; i++){
                System.out.println((i*255%65536 )+ " "+((i*255 % 8)));
            }
           // c.receiveScreenshot();
//            short b2 = -1; // 0xFF
//            int i2 = b2 & 0xFFFF; // 0x000000FF
//            System.out.println("int = " + i2);
//            System.out.println("short = " + (short) i2);
            

            System.out.println("---------------------------------------------------------------");



        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

class Client {
    //identifikátor 'spojení'	sekvenční  číslo	číslo potvrzení     příznak         data
    //  4B                                  2B                    2B              1B            0-255B

    DatagramSocket socket;
    DatagramPacket packet;
    Send s;

    public Client(String address, int port) throws UnknownHostException, SocketException, FileNotFoundException {
        socket = new DatagramSocket();
        socket.setSoTimeout(100);
        s = new Send(socket, InetAddress.getByName(address), port);

    }

    public void sendFirmware() {
        s.setMode(2);
    }

    public void receiveScreenshot() throws IOException {
        s.screenshot();
    }
}
//    private void receive() throws IOException { 
//        try {
//            packet = new DatagramPacket(messageReceive, messageReceive.length);
//            socket.receive(packet);
//            packetLen = packet.getLength(); 
//        } catch (SocketTimeoutException e) {
//            if (counter++ == 20) {
//                end = true;
//            } else {
//                send(messageSend);
//                receive();
//            }
//        } finally {
//            counter = 0;
//
//        }
//    }
//}
