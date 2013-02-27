package robot;

import java.io.*;

/**
 *
 * @author Adam Plansky
 */
public class Send {

    int idCon;
    short seq, ack;
    byte flag;
    byte[] data;
    final byte SYN = 4;
    final byte FIN = 2;
    final byte RST = 1;
    final int DOWNLOAD = 0x01;
    final int UPLOAD = 0x02;
    byte[] message;
    private ByteArrayOutputStream baos;
    private DataOutputStream daos;

    public Send(String mode) {
        idCon = seq = ack = 0;
        flag = SYN;
        data = new byte[1];
        if (mode.equals("DOWN")) {
            data[0] = DOWNLOAD;
        } else if (mode.equals("UPLOAD")) {
            data[0] = UPLOAD;
        }
    }

    public void print() {
        System.out.print((String.format("%8s", Integer.toHexString(idCon))).replace(' ', '0') + " SEND seq=" + seq + " ack=" + ack + " flag=" + flag + " data(255): ");
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());

    }

    void convertToPacket() throws IOException {
        baos = new ByteArrayOutputStream();
        daos = new DataOutputStream(baos);
        daos.writeInt(idCon);
        daos.writeShort(seq);
        daos.writeShort(ack);
        daos.writeByte(flag);
        daos.write(data, 0, data.length);
        message = baos.toByteArray();
        daos.close();
        baos.close();
    }

    public byte[] getMessage() throws IOException {
        convertToPacket();
        return message;
    }

    public void getHead() {
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
