package robot;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.*;
import java.io.*;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

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
            c.receiveScreenshot();
            System.out.println("---------------------------------------------------------------");

//            File f = new File("/home/impy/Projects/UDP_robot/UDP/build/classes/robot/image.jpeg");
//            System.out.println(f.exists());
//            FileInputStream fis = new FileInputStream(f);
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            FileOutputStream fos = new FileOutputStream("foto.png");
//            byte[] buf = new byte[1024];
//            try {
//                for (int readNum; (readNum = fis.read(buf)) != -1;) {
//                    fos.write(buf,0,readNum);
//                    bos.write(buf, 0, readNum);
//                    System.out.println("read " + readNum + " bytes,");
//                }
//            } catch (IOException ex) {
//                System.out.println(ex);
//            }
//            byte[] bytes = bos.toByteArray();
//            
//            
//            fos.close();
            
        } catch (Exception e) {
            System.out.println(e);
        }


//        short i = 32767;
//        System.out.println(i);
//        System.out.println(Integer.toBinaryString(i));
//        System.out.println(i+=1);
//        System.out.println(Integer.toBinaryString(i).replaceFirst("1111111111111111", ""));
//        int a = 32768;
//        System.out.println(Integer.toBinaryString(a));
    }
}

class Client {
    //identifikátor 'spojení'	sekvenční  číslo	číslo potvrzení     příznak         data
    //  4B                                  2B                    2B              1B            0-255B

    DatagramSocket socket;
    DatagramPacket packet;
    Send s;

    public Client(String address, int port) throws UnknownHostException, SocketException, FileNotFoundException{
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
