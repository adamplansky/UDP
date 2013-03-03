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
    int end = -10;
    int endDatLen = -10;
    FileOutputStream fos;
    private int idx = -1;

    public Window() throws FileNotFoundException {
        w = new byte[2040];
        s = new int[8];
        fos = new FileOutputStream("foto.png");
    }

    public int Add(byte[] data, int seqNumber, int dataLen) throws IOException {
        if (dataLen != 255) {
            end = seqNumber;
            endDatLen = dataLen;
        }
        if (seqNumber == 255) {
            System.out.println("");
        }

        int seqIdx = getIdx(seqNumber / 255);
        //s[seqIdx % 8] = seqNumber;

        System.arraycopy(data, 0, w, seqIdx*255, data.length);
        s[seqIdx] = seqNumber;
        int cnt = seqTop;
        int a = cnt;
        if (seqTop == seqIdx) {

            while (s[seqIdx] < s[(seqIdx + 1) % 8]) {
                fos.write(w, seqTop * 255, 255);
                a++;
                seqIdx++;
                seqIdx %= 8;
                seqTop++;
                seqTop %= 8;
            }

            seqTop++;
            a++;
            seqTop %= 8;
            
            idx = getIdx(seqTop - 1);
            if (s[idx] == end) {
                fos.write(w, idx * 255, endDatLen);
            } else {
                fos.write(w, idx * 255, dataLen);
            }

        }
        if (idx != -1 && end == s[idx]) {
            return seqNumber + (a - 1 - cnt) * 255 + endDatLen;
        }



        //posun pointer
        //vypis data
        //vrat cislo jakyej byte chci

        System.out.println("");
        return seqNumber + (a - cnt) * 255;
    }
    //return seqNumber + cnt * 255;

    public void Print() {
        for (int i = 0; i < 8; i++) {
            System.out.print(s[i] + ", ");
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
}
