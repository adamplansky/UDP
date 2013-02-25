package robot;

import java.net.*;
import java.io.*;

/**
 *
 * @author Adam Plansky if you have some question please contact me:
 * plansada@fit.cvut.cz
 *
 *
 */
//It is DEVELOPER BRANCH
public class Robot {

    public static void main(String[] args) throws IOException {


        //identifikátor 'spojení'	sekvenční  číslo	číslo potvrzení     příznak         data
        //  4B                              2B                      2B              1B              0-255B
        final int SYN = 4;
        final int FIN = 2;
        final int RST = 1;
        final int DOWNLOAD = 0x01;
        final int UPLOAD = 0x02;


        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream daos = new DataOutputStream(baos);
        daos.writeInt(0);
        daos.writeShort(0);
        daos.writeShort(0);
        daos.writeByte(SYN);
        daos.writeByte(DOWNLOAD);
        daos.close();
        final byte[] bytes = baos.toByteArray();

        DatagramSocket socket;
        DatagramPacket packet;
        InetAddress address, fromAddress;
        String messageString;
        byte[] message;

        int fromPort, port = 4000;

        //
        // Send request
        //
        socket = new DatagramSocket();
        address = InetAddress.getByName("192.168.10.211");
        message = bytes;
        System.out.println(message.length);
        packet = new DatagramPacket(message, message.length, address, port);
        socket.send(packet);

        //
        // Receive reply and print
        //
        packet = new DatagramPacket(message, message.length);
        socket.receive(packet);
        int length = packet.getLength();
        fromAddress = packet.getAddress();
        fromPort = packet.getPort();



        final ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
        final DataInputStream dais = new DataInputStream(bais);

        int IDNO = dais.readInt();
        short seqNo = dais.readShort();
        short confNo = dais.readShort();
        byte falgNo = dais.readByte();
        byte cmdNO = dais.readByte();
        System.out.println(Integer.toHexString(IDNO).toUpperCase() + " RECV " + fromAddress.getHostAddress() + ":" + port + " seq=" + seqNo + " ack=" + confNo + " flags=" + falgNo);
        socket.close();
    }
}
