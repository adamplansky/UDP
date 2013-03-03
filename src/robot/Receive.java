/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.io.*;
import java.net.*;

/**
 *
 * @author Adam Plansky
 */
public class Receive {

    int idCon;
    
    int seq,ack;
    byte flag;
    byte[] data;
    int dataLen;
    final byte SYN = 4;
    final byte FIN = 2;
    final byte RST = 1;
    final int DOWNLOAD = 0x01;
    final int UPLOAD = 0x02;
    byte[] message;
    private ByteArrayInputStream bais;
    private DataInputStream dais;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private Send s;
    private int seq1;
    private int lastSeq1;
    private boolean overFlow = false;
    private int printHelp = 0;
    private static int counter = 0;
    Window w;
    WindowTemp w1;

    public Receive(DatagramSocket socket, Send s) throws FileNotFoundException {
        this.socket = socket;
        data = new byte[255];
        message = new byte[255 + 9];
        this.s = s;
        w = new Window();
        w1 = new WindowTemp();
        seq1 = 0;
        lastSeq1 = 0;
        counter = 0;
    }

    public void receive() throws IOException {
   
        try {
            packet = new DatagramPacket(message, message.length);
            socket.receive(packet);

            dataLen = packet.getLength() - 9; //buffer for data - head of packet
  

            convertFromPacket();
           print();
     
            if (s.idCon == idCon) {
                //if it is correct packet
                if (s.flag == 0 && flag == 0) {
                    //received packet is packet what i needed
                    doAction();


                } else if (flag == FIN && dataLen > 0) {
                    //System.out.println("RST FLAG");
                } else if (flag == FIN) {
                    System.out.println("ENDE ");
                    s.setHead(idCon, (short) 0, (short) (seq1), FIN);
                }
            } else {

                if (flag == SYN && s.flag == SYN && s.getIdCon() == 0 && dataLen == 1 && (data[0] == 1)) {
                    //System.out.println("nastavuju ID");
                    setSendPacket();
                } else if (s.getFlag() == SYN && flag == 0) {
                    //System.out.println("packet zahazuju");
                } else {
                    //System.out.println("RST IDCON MISSING");
                }
            }
            if (flag > 4 || s.flag == 3);//RST

        } catch (SocketTimeoutException e) {
            System.out.println("TIMEOUT");
            counter++;
            if (counter >= 20) {
                s.setHead(idCon, (short)seq, (short)ack, RST);
                System.out.println("Posilam RST packet");
            } else {
                s.send();
                receive();
            }

        }
        counter = 0;
    }

    void doAction() throws IOException {
        
        if (seq == s.ack) {
            seq1 = w.next(data, seq, dataLen);
            if(seq1 >= 65536){
                seq1%=65536;
                seq%=65536;
            }
            s.setHead(idCon, (short) 0, (short) (seq1), flag);
        } else if (seq - s.ack < 2040 )//i needed remember this packet
        {
//            System.out.println("tento packet si budu pamatovat");
//            System.out.println("dataLen = " + dataLen);
            w.Add(data, seq, dataLen);
        } else if((65536-(seq+s.ack)) < 2040 && (65536-(seq+s.ack)) > -2040 && s.ack > seq ){
//            System.out.println("tento packet si budu pamatovat PRETEJKAM");
//            System.out.println("dataLen = " + dataLen);
            w.Add(data, seq, dataLen);
        }

//        } else if (seq < 0 && s.ack > 0) {
//            if (seq + s.ack < 2000) {
//                System.out.println("tento packet si budu pamatovat");
//                System.out.println("dataLen = " + dataLen);
//                w.Add(data, seq & 0xFFFF, dataLen);
//            }
//        }}

    }

    void convertFromPacket() throws IOException {
        bais = new ByteArrayInputStream(message);
        dais = new DataInputStream(bais);
        idCon = dais.readInt();

        seq = (dais.readShort() & 0xFFFF);

        ack = dais.readShort();
        flag = dais.readByte();
        dais.read(data);
        dais.close();
        bais.close();
        if (overFlow == true) {
            System.out.println("OVERFLOW!");
        }
    }

    void setSendPacket() {
        if (s.getIdCon() == 0) {
            if (getFlag() == SYN) {
                //prijmul sem packet ktery sem potreboval muzu zacit prijmat data
                //nastav send na id con
                s.setHead(idCon, (short)seq, (short)ack, (byte) 0);
            }
        }
    }

    public void print() {

        System.out.print(s.t.getElapsedTime1() + " " + (String.format("%8s", Integer.toHexString(idCon))).replace(' ', '0') + " RECV seq=" + (seq & 0xFFFF) + " ack=" + (ack & 0xFFFF) + " flag=" + flag + " data(" + dataLen + "): ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataLen; i++) {
            sb.append(String.format("%02X ", data[i]));
        }
        System.out.println(sb.toString());
        if (printHelp == 50000) {
            System.out.println("packet se mi nehodi do rady, navic uz sem ho jednou prijal ");
        }
    }

    public int getIdCon() {
        return idCon;
    }

    public short getSeq() {
        return (short)seq;
    }

    public int getAck() {
        return ack;
    }

    public byte getFlag() {
        return flag;
    }
}
