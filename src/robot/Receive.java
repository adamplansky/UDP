/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.io.*;

/**
 *
 * @author Adam Plansky
 */
public class Receive {

    int idCon;
    short seq, ack;
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

    public Receive() {
        data = new byte[255];
    }

    public void setMessage(byte[] message, int packetLen) throws IOException {
        this.message = message;
        dataLen = packetLen;
        convertFromPacket();
    }

    void convertFromPacket() throws IOException {
        bais = new ByteArrayInputStream(message);
        dais = new DataInputStream(bais);
        idCon = dais.readInt();
        seq = dais.readShort();
        ack = dais.readShort();
        flag = dais.readByte();
        dais.read(data);
        dataLen = message.length - 9;
        dais.close();
        bais.close();
    }

    public void print() {
        System.out.print((String.format("%8s", Integer.toHexString(idCon))).replace(' ', '0') + " RECV seq=" + seq + " ack=" + ack + " flag=" + flag + " data(255): ");
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());

    }

    public byte[] getMessage() throws IOException {
        convertFromPacket();
        return message;
    }

    public int getIdCon() {
        return idCon;
    }

    public short getSeq() {
        return seq;
    }

    public short getAck() {
        return ack;
    }

    public byte getFlag() {
        return flag;
    }
}
