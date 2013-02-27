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

    public static void main(String[] args) {
        try {
            Client c = new Client("192.168.10.211", 4000);
            c.receiveScreenshot();
            //c.sendFirmware();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class Client {
    //identifikátor 'spojení'	sekvenční  číslo	číslo potvrzení     příznak         data
    //  4B                                  2B                    2B              1B            0-255B

    boolean end = false;
    int counter = 0;
    DatagramSocket socket;
    DatagramPacket packet;
    InetAddress address;
    int port, packetLen;
    byte[] messageSend, messageReceive;
    Send s;
    Receive r;

    public Client(String address, int port) throws UnknownHostException, SocketException {
        this.address = InetAddress.getByName(address);
        this.port = port;
        socket = new DatagramSocket();
        socket.setSoTimeout(100);
        messageSend = new byte[264]; //264 = 4+2+2+1+255
        messageReceive = new byte[264]; //264 = 4+2+2+1+255
    }

    public void sendFirmware() {
    }

    public void receiveScreenshot() throws IOException {
        s = new Send("DOWN");
        r = new Receive();
        connectionPacket();
        receive(); //prijme prvni packet s datama
        r.setMessage(messageReceive,packetLen);
        s.print();
        r.print();
//        while (end == false) {
//            //prijmu packet
//            receive();
//            r.setMessage(messageReceive,packetLen);
//
//            s.print();
//            r.print();
//
//            //odeslu packet
//            messageSend = s.getMessage();
//            send(messageSend);
//
//        }
    }

    public void connectionPacket() throws IOException {
        messageSend = s.getMessage();
        send(messageSend);
        receive();
        r.setMessage(messageReceive,packetLen);
        
        s.print();
        r.print();
        System.out.println("Connection established, id conn = " + Integer.toHexString(r.idCon));
    }

    private void send(byte[] msg) throws IOException {
        packet = new DatagramPacket(msg, msg.length, this.address, this.port);
        socket.send(packet);
    }

    private void receive() throws IOException {
        packet = new DatagramPacket(messageReceive, messageReceive.length);
        try {
            socket.receive(packet);
            
        } catch (SocketTimeoutException e) {
            if (counter++ == 20) {
                end = true;
            }
            send(messageSend);
            receive();
        } finally {
            counter = 0;
            packetLen = packet.getLength();
        }
    }
}
