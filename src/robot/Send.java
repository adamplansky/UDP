package robot;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Adam Plansky
 */
public class Send {

    int idCon;
    int ack, seq;
    byte flag;
    byte[] data;
    int dataLen;
    //baryk
    final byte SYN = 4;
    final byte FIN = 2;
    final byte RST = 1;
    private byte mode = 0;
    byte[] message;
    private ByteArrayOutputStream baos;
    private DataOutputStream daos;
    private DatagramPacket packet;
    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    private String firmwareString;
    private boolean connNumber;
    private Receive r;
    public Tmr t;
    private Window w;
    byte counterSamePacket;

    ////TIME
    public Send(DatagramSocket socket, InetAddress address, int port) throws FileNotFoundException {
        this.socket = socket;
        this.address = address;
        this.port = port;
        idCon = seq = 0;
        ack = 0;
        flag = SYN;
        data = new byte[1];
        connNumber = false;
        r = new Receive(socket, this);
        t = new Tmr();

    }

    public void screenshot() throws IOException {
        setMode(1);
        while (idCon == 0) {
            send();
            r.receive();
            //if(t.getElapsedTime1() > 2000)break;//RST;
            if (t.getElapsedTime1() > 2000) {
                break;
            }
        }
        while (flag == 0) {
            r.receive();
            send();
        }
        r.w.fos.close();
        if (r.w.end == true) {
            System.out.println("Prenaseni probehlo uspesne.");
        }
        System.out.println("ENDE1");
    }

    public void firmware(String pathToFile) throws IOException {
        this.firmwareString = pathToFile;
        setMode(2);
        while (idCon == 0) {
            sendF();
            r.receiveF();
            if (t.getElapsedTime1() > 2000) {
                break;
            }
        }
        if (flag == 0) {
            System.out.println("SPOJENI NAVAZANO ZACINAM ODESILAT PACKETY");
            sendAll();
        }
        while (flag == 0) {
            r.receiveF();
            if (counterSamePacket == 3) {
                sendAll();
                counterSamePacket = 0;
            } else {
                sendF();
            }
        }
//        if (flag == FIN) {
//            r.receive();
//        }

        r.w.fis.close();
        if (r.w.end == true) {
            System.out.println("Prenaseni probehlo uspesne.");
        }
    }

    public void send() throws IOException {
        buildMessage();
        packet = new DatagramPacket(message, message.length, address, port);
        socket.send(packet);
        dataLen = packet.getLength() - 9;
        print();
    }

    public void sendF() throws IOException {
        if (flag == 0) {
            data = w.dataInPacket();
        }
        if(flag == FIN){
            System.out.println("");
        }
        buildMessageF();
        packet = new DatagramPacket(message, message.length, address, port);
        socket.send(packet);
        dataLen = packet.getLength() - 9;
        printF();
    }

    public void sendAll() throws IOException {
        for (int i = 0; i < 8; i++) {
            this.seq = w.getSeqOnIndex(i);
            data = w.dataInPacketAtIndex(i);
            buildMessageF();
            packet = new DatagramPacket(message, message.length, address, port);
            socket.send(packet);
            dataLen = packet.getLength() - 9;
            printF();
        }
    }

    public void setHead(int idCon, short seq, short ack, byte flag) {
        this.idCon = idCon;
        this.seq = (seq & 0xFFFF);
        this.ack = (ack & 0xFFFF);
        this.flag = flag;
    }

    private void buildMessage() throws IOException {
        baos = new ByteArrayOutputStream();
        daos = new DataOutputStream(baos);
        daos.writeInt(idCon);
        daos.writeShort((short) seq);
        daos.writeShort((short) ack);
        daos.writeByte(flag);
        //download screenshort
        if (mode == 1) {
            if (flag == SYN) {
                daos.write(data, 0, data.length);
            }
        } else if (mode == 2) {
            if (flag == SYN) {
                daos.write(data, 0, data.length);
            }
        }
        message = baos.toByteArray();
        daos.close();
        baos.close();
    }
    //for F = for firmware

    private void buildMessageF() throws IOException {
        baos = new ByteArrayOutputStream();
        daos = new DataOutputStream(baos);
        daos.writeInt(idCon);
        daos.writeShort(seq);
        daos.writeShort((short) ack);
        daos.writeByte(flag);
        //download screenshort

        if (flag == SYN) {
            daos.write(data, 0, data.length);
        } //else if (flag == FIN) {}
        
        else {
            daos.write(data, 0, data.length);
        }
        message = baos.toByteArray();
        daos.close();
        baos.close();
    }

    public void setMode(int mode) throws FileNotFoundException, IOException {
        this.mode = (byte) mode;
        data[0] = this.mode;
        if (mode == 1) {
            w = new Window();
        } else if (mode == 2) {
            w = new Window(firmwareString);
        }
        r.setWindow(w);

    }

    public int getMode() {
        System.out.println(String.format("%02X ", mode));
        return mode;
    }

    public void print() {
        System.out.print(t.getElapsedTime1() + " " + (String.format("%8s", Integer.toHexString(idCon))).replace(' ', '0') + " SEND seq=" + seq + " ack=" + ack + " flag=" + flag + " data(" + dataLen + "): ");
        StringBuilder sb = new StringBuilder();

        if (flag == SYN) {
            for (byte b : data) {
                sb.append(String.format("%02X ", b));
            }
        } else {
            System.out.println("--");
        }
        System.out.println(sb.toString());
    }

    public void printF() {
        System.out.print(t.getElapsedTime1() + " " + (String.format("%8s", Integer.toHexString(idCon))).replace(' ', '0') + " SEND seq=" + seq + " ack=" + ack + " flag=" + flag + " data(" + dataLen + "): ");
        StringBuilder sb = new StringBuilder();

        if (flag == SYN) {
            for (byte b : data) {
                sb.append(String.format("%02X ", b));
            }
        } else if (flag == 0) {
            for (byte b : data) {
                sb.append(String.format("%02X ", b));
            }
        } else {
            System.out.println("VYPIS DEFAULTNE NASTAVEN NA TOTO");
            System.out.println("--");
        }
        System.out.println(sb.toString());
    }

    public void getHead() {
    }

    public int getIdCon() {
        return idCon;
    }

    public int getSeq() {
        return seq;
    }

    public int getAck() {
        return ack;
    }

    public byte getFlag() {
        return flag;
    }

    public String getFirmwareString() {
        return firmwareString;
    }
}
