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
    private boolean connNumber;
    private Receive r;
    public Tmr t;

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
        System.out.println("ENDE1");
    }

    public void setHead(int idCon, short seq, short ack, byte flag) {
        this.idCon = idCon;
        this.seq = seq;
        this.ack = (ack & 0xFFFF);
        this.flag = flag;
    }

    public void send() throws IOException {
        buildMessage();
        packet = new DatagramPacket(message, message.length, address, port);
        socket.send(packet);
        dataLen = packet.getLength() - 9;
       print();
    }

    private void buildMessage() throws IOException {
        baos = new ByteArrayOutputStream();
        daos = new DataOutputStream(baos);
        daos.writeInt(idCon);
        daos.writeShort(seq);
        daos.writeShort((short) ack);
        daos.writeByte(flag);
        //download screenshort
        if (mode == 1) {
            if (flag == SYN) {
                daos.write(data, 0, data.length);
            }
        } else if (mode == 2) {
        }
        message = baos.toByteArray();
        daos.close();
        baos.close();
    }

    public void setMode(int mode) {
        this.mode = (byte) mode;
        data[0] = this.mode;
    }

    public void getMode() {
        System.out.println(String.format("%02X ", mode));
    }

    public void print() {
        System.out.print(t.getElapsedTime1() + " " + (String.format("%8s", Integer.toHexString(idCon))).replace(' ', '0') + " SEND seq=" + seq + " ack=" + ack + " flag=" + flag + " data(" + dataLen + "): ");
        StringBuilder sb = new StringBuilder();
        if (mode == 1) {
            if (flag == SYN) {
                for (byte b : data) {
                    sb.append(String.format("%02X ", b));
                }
            } else {
                System.out.println("--");
            }
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
}
