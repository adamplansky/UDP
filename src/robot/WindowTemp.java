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
public class WindowTemp {

    int top = 0;
    int seqTop = 0;
    final int sw = 255;
    byte[] w;
    int[] s;
    int[] s1;
    boolean end = false;
    int endDatLen = -10;
    int endSeq = 0;
    private int idx = -1;

    public WindowTemp() throws FileNotFoundException {
        w = new byte[2040];
        s = new int[8];

        s1 = new int[8];
    }

    //return packet I need
    public void Add(byte[] data, int seqNumber, int dataLen) throws IOException {
        System.out.println("ADDING TO TEMP ARRAYS");
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

    public byte[] getW() {
        return w;
    }

    public int[] getS() {
        return s;
    }
}
