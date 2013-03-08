package robot;

import java.net.*;
import java.io.*;

/**
 * TODO: nemá potvrzovací číslo v intervalu <seq - velikost okénka, seq> kde seq
 * pokud 20x za sebou posle prikaz FIN je sekvenční číslo příjemce
 *
 * @author Adam Plansky if you have some question please contact me:
 * plansada@fit.cvut.cz
 *
 *
 */
//It is DEVELOPER BRANCH
public class Robot {

    public static void main(String[] args) {
        if (args.length == 1) {
               try {
                Client c = new Client(args[0], 4000);
                c.receiveScreenshot();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (args.length == 2) {
            try {
                Client c = new Client(args[0], 4000);
                c.sendFirmware(args[1]);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            System.out.println("please insert ip adress as a first parametr and if you want to upload firmware please insert firmware as a second parameter");
        }

    }
}

class Client {

    DatagramSocket socket;
    DatagramPacket packet;
    Send s;

    public Client(String address, int port) throws UnknownHostException, SocketException, FileNotFoundException {
        socket = new DatagramSocket();
        socket.setSoTimeout(100);
        s = new Send(socket, InetAddress.getByName(address), port);
    }

    public void sendFirmware(String pathToFile) throws IOException {
        System.out.println("FIRMWARE");
        s.firmware(pathToFile);
        System.out.println("FIRMWARE");
    }

    public void receiveScreenshot() throws IOException {
        System.out.println("SCREENSHOT");
        s.screenshot();
        System.out.println("SCREENSHOT");
    }
}
