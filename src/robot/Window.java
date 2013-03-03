/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Adam Plansky
 */
public class Window {

    int top = 0;
    int seqTop = 0;
    final int sw = 255;
    byte[] w;
    int[] s;
    int[] s1;
    boolean end = false;
    int endDatLen = -10;
    int endSeq = 0;
    FileOutputStream fos;
    private int idx = -1;

    public Window() throws FileNotFoundException {
        w = new byte[2040];
        s = new int[8];

        s1 = new int[8];
        fos = new FileOutputStream("foto.png");
    }

    //return packet I need
    public void Add(byte[] data, int seqNumber, int dataLen) throws IOException {
        int idx = getIdx(seqNumber / 255);
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
        int temp, start = 0;

        do {
            temp = s[seqTop];

            toFile();
            ++seqTop;
            ++start;
            seqTop %= 8;
        } while (s[seqTop] > temp && (s[seqTop] - temp) < 5000 && (s[seqTop] - temp) > -5000);
        System.out.println(" | seqTop= " + seqTop);
        if (end == true && endSeq == s[getIdx(seqTop - 1)]) {
            fos.close();
            return endSeq + endDatLen;

        } else {
            seqNumber %= 65536;
            return seqNumber + 255 * start;
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

    public int getIdx(int number) {
        if (number < 1) {
            int a = (number) % -8;
            while (a < 0) {
                a += 8;
            }
            return a;
        } else {
            return (number) % 8;
        }
    }
}
