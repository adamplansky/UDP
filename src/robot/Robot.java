package robot;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author Adam Plansky if you have some question please contact me:
 * plansada@fit.cvut.cz
 *
 *
 */
//It is DEVELOPER BRANCH
public class Robot {

    static void printBytes(String text) {
        byte[] bytes = text.getBytes();
        int a = 0;
        System.out.println(bytes[0]);
        for (byte b : bytes) {
//            System.out.println(b + " " + text.charAt(a++));
            System.out.println(String.format("0x%2X %03d " + text.charAt(a), (int) b, b));
            a++;
        }
    }

    public static void main(String[] args) throws IOException {


        //identifikátor 'spojení'	sekvenční  číslo	číslo potvrzení     příznak         data
        //  4B                              2B                      2B              1B              0-255B
        final int SYN = 4;
        final int FIN = 2;
        final int RST = 1;
        final int DOWNLOAD = 0x01;
        final int UPLOAD = 0x02;

//        int idNumber = 0;
//        int seqNumber = 0;
//        int confNumber = 0;

        byte[] idNumber = new byte[4];
        byte[] seqNumber = new byte[2];
        byte[] confNumber = new byte[2];
        byte[] flag = new byte[1];
        byte[] data = new byte[255];
        //id number
        data[0] = 0;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;
        //sequence number
        data[4] = 0;
        data[5] = 0;
        //confirm number
        data[6] = 0;
        data[7] = 0;
        //flag
        data[8] = SYN;
        //data
        data[9] = DOWNLOAD;
        
        byte[] msg = new byte[4+2+2+1+255];
        

        

        DatagramSocket socket;
        DatagramPacket packet;

        InetAddress address, fromAddress;
        String messageString;

        byte[] message;
        int fromPort, port = 4000;
        //
        // Send request
        //
        //messageString = new String("Ahoj");
        socket = new DatagramSocket();
        address = InetAddress.getByName("192.168.10.211");

        //convert string to byte array (you can send the message by bytes)
       // message = messageString.getBytes();
        //my own method for debugging (printing bytes)
       // printBytes(messageString);
        for(int i = 0 ; i < 10; i++){
            System.out.print(data[i]);
        }
        //datagram packet, which is to server (byte[],len,inetaddress,port);
        packet = new DatagramPacket(data, 10, address, port);
        //send it to the server
        socket.send(packet);

        //
        // Receive reply and print
        //
        packet = new DatagramPacket(data, 255);
        socket.receive(packet);
        int length = packet.getLength();
        fromAddress = packet.getAddress();
        fromPort = packet.getPort();
        String received = new String(packet.getData(), 0, length);
        System.out.println("Received: " + received + "   from: "
                + fromAddress + ":" + fromPort);
        socket.close();
    }
}
