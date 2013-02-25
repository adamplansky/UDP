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

    static byte[] msgMet(byte[] idNumber, byte[] seqNumber, byte[] confNumber, byte[] flag, byte[] data, byte[] msg) {
        System.arraycopy(idNumber, 0, msg, 0, 4);
        System.arraycopy(seqNumber, 0, msg, 4, 2);
        System.arraycopy(confNumber, 0, msg, 6, 2);
        System.arraycopy(flag, 0, msg, 8, 1);
        System.arraycopy(data, 0, msg, 9, 1);
        return msg;
    }

    static void printByte(byte[] msg) {
        
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


        idNumber[0] = idNumber[1] = idNumber[2] = idNumber[3] = 0;
        seqNumber[0] = seqNumber[1] = 0;
        confNumber[0] = confNumber[1] = 0;
        flag[0] = SYN;
        data[0] = DOWNLOAD;

        byte[] msg = new byte[4 + 2 + 2 + 1 + 255];
        msg = msgMet(idNumber, seqNumber, confNumber, flag, data, msg);
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
        address = InetAddress.getByName("10.4.241.221");

        //convert string to byte array (you can send the message by bytes)
        // message = messageString.getBytes();
        //my own method for debugging (printing bytes)
        // printBytes(messageString);
//        for(int i = 0 ; i < 10; i++){
//            System.out.print(data[i]);
//        }
        //datagram packet, which is to server (byte[],len,inetaddress,port);
        packet = new DatagramPacket(msg, 10, address, port);
        //send it to the server
        socket.send(packet);

        //
        // Receive reply and print
        //
        int[] data1 = new int[10];
        packet = new DatagramPacket(data, 10);

        socket.receive(packet);
        int length = packet.getLength();
        fromAddress = packet.getAddress();
        fromPort = packet.getPort();
        // String received = new String(packet.getData(), 0, length);
        msg = packet.getData();
        System.out.println(msg[0] + " " + (int) msg[0]);
        System.out.println(msg[1] + " " + (int) msg[1]);
        System.out.println(msg[2] + " " + (int) msg[2]);
        System.out.println(msg[3] + " " + (int) msg[3]);
        int i = 0;
        i |= ((int) msg[0]) << 24;
        i |= ((int) msg[1]) << 16;
        i |= ((int) msg[2]) << 8;
        i |= ((int) msg[3]);
        //i = ((int) msg[0]) << 24 + (((int) msg[1]) << 16) + ((int) msg[2]) << 8 + ((int) msg[3]);
        System.out.println((Integer.toHexString(i).toUpperCase()));
        //System.out.println(Integer.toHexString(Byte.valueO.intValue()f(msg[0]).intValue()));

        //msg[0] = Integer.to;

        //System.out.println("Received: " + received + "   from: " + fromAddress + ":" + fromPort);
        socket.close();
    }
}
