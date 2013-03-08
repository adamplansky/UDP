/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Adam Plansky
 */
public class Window {

    int top = 7;
    int seqTop = 0;
    final int sw = 255;
    byte[] w;
    int[] s;
    boolean end = false;
    int endDatLen = -10;
    int endSeq = 0;
    private int mode;
    FileOutputStream fos;
    FileInputStream fis;
    private int idx = -1;
    private int cntSamePacket = 3;
    int i1 = 0, i2 = 0, i3 = 0, i4 = 0;

    public Window() throws FileNotFoundException {
        w = new byte[2040];
        s = new int[8];
        this.mode = 1;
        fos = new FileOutputStream("foto.png");
    }

    public Window(String firmwarePathTofile) throws FileNotFoundException, IOException {
        w = new byte[2040];
        s = new int[8];
        this.mode = 2;
        System.out.println("OK FIRMWAR");
        fis = new FileInputStream(firmwarePathTofile);
        fos = new FileOutputStream("firmware");
        initLoadFirmware();
    }

    public void initLoadFirmware() throws IOException {
        int seqNumber = 0;
        for (int i = 0; i < 8; i++) {
            int idx = getIdx(seqNumber);
            s[idx] = seqNumber;
            fis.read(w, idx * 255, 255);
            /////////////////////
//            fos.write(w, idx * 255, 255);
            seqNumber += 255;
        }
        seqTop = 0;
    }

    public byte[] dataInPacket() {
        byte[] data = new byte[255];
        if (end == true && seqTop == endSeq) {
            System.arraycopy(w, seqTop*255, data, 0, endDatLen);
        } else {
            System.arraycopy(w, seqTop*255, data, 0, 255);
        }
        //Print();
        return data;
    }

    public byte[] dataInPacketAtIndex(int idx) {
        byte[] data = new byte[255];
        if (end == true && idx * 255 == endSeq) {
            System.arraycopy(w, idx * 255, data, 0, endDatLen);
        } else {
            System.arraycopy(w, idx * 255, data, 0, 255);
        }
        //Print();
        return data;
    }

    public int addNextPackets(int ack) throws IOException {
       
        Print();
        if (ack != s[seqTop]) {
            int ret = 0;
            if (end == false) {
                while (s[seqTop] != ack) {
                    if (end == true) {
                        seqTop = ++seqTop % 8;
                        continue;
                    }
                    ret = fis.read(w, seqTop * 255, 255);
                    if (ret != 255) {
                        //System.out.print("NACITAM POSLEDNI BYTE");
                        //Print();
                        if (ret == -1) {
                            s[seqTop] = (s[seqTop] + 2040) % 65536;
                            endSeq = s[seqTop];
                            end = true;
                            if (endDatLen <= 0) {
                                endDatLen = 255;
                            }
                        } else {
                            //System.out.println(s[seqTop]);
                            s[seqTop] = (s[seqTop] + 2040) % 65536;
                            
                            endDatLen = ret;
                            endSeq = s[seqTop] + ret;
                            ///////////////////////////////////
//                            fos.write(w, seqTop * 255, endDatLen);
//                            fos.close();
                            end = true;
                            int mm = seqTop;
                            for(; ; ){
                                System.out.println(s[mm++]);
                                if(mm==8)break;
                            }
                        }
                       // System.out.println(endSeq);
                    } else {
                       // fos.write(w, seqTop * 255, 255);
                        System.out.println(s[seqTop]);
                        s[seqTop] = (s[seqTop] + 2040) % 65536;
                    }
                    seqTop = getIdx(seqTop - 1);
                }
            }
        } else {
            while (s[seqTop] != ack) {
                System.out.println(s[seqTop]);
                ///////////////////////////////////
                fos.write(w, seqTop * 255, 255);
                seqTop = getIdx(seqTop - 1);
            }
        }
        Print();
        return s[seqTop];
    }

    public int getSeqOnIndex(int idx) {
        return s[idx];
    }

    boolean send8Packets() {
        if (cntSamePacket == 3) {
            return true;
        }
        return false;
    }
    //return packet I need

    public void Add(byte[] data, int seqNumber, int dataLen) throws IOException {

        int idx = getIdx(seqNumber);
        if (s[idx] == seqNumber && seqNumber != 0) {
            return;
        }
        s[idx] = seqNumber;
        System.arraycopy(data, 0, w, idx * 255, data.length);

        if (dataLen < 255) {
            end = true;
            endDatLen = dataLen;
            endSeq = seqNumber;
        }
        Print();
    }

    public int next(byte[] data, int seqNumber, int dataLen) throws IOException {
        System.out.print("seqNumber = " + seqNumber);
        Add(data, seqNumber, dataLen);
        int temp, temp2, start = 0;

        do {
            temp = s[seqTop] % 65536;
            toFile();
            --seqTop;
            ++start;
            seqTop = getIdx(seqTop);
            temp2 = (s[seqTop] % 65536);
            System.out.println("temp = " + temp + ", temp2 = " + temp2);
        } while ((temp2 > temp && (temp2 - temp) < 2040) || ((temp > temp2) && 65536 - (temp - temp2) < 2040));

        System.out.println(" | seqTop= " + seqTop);
        if (end == true && endSeq == s[getIdx(seqTop + 1)]) {
            fos.close();
            return endSeq + endDatLen;

        } else {
            System.out.println("ASD: " + i1 + "," + i2 + "," + i3 + "," + i4 + ",");
            if (temp - temp2 > 30000) {
                i1++;
                return temp2 + 255;
            } else if (temp > temp2) {
                i2++;
                return temp + 255;
            } else if (temp2 - temp > 30000) {
                i3++;
                return temp + 255;
            } else {
                i4++;
                return temp2 + 255;
            }
        }
    }

    private void toFile() throws IOException {
        if (end == true && endSeq == s[seqTop]) {
            fos.write(w, seqTop * 255, endDatLen);
        } else {
            fos.write(w, seqTop * 255, 255);
        }
        System.out.println("################## : " + s[seqTop]);
    }

    public void Print() {
        System.out.print("[ ");
        for (int i = 0; i < 8; i++) {
            System.out.print(s[i] + ", ");
        }
        System.out.println(" ]");
    }

    //get idx in ranged 0-7 for ack/seq
    public int getIdx(int number) {
        if (number < 1) {
            int a = (number) % 8;
            while (a < 0) {
                a += 8;
            }
            return a;
        } else {
            return (number) % 8;
        }
    }
}