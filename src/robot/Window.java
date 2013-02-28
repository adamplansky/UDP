/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

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

    public Window() {
        w = new byte[2040];
        s = new int[8];
    }

    public void Add(byte[] data, int seqNumber) {
        int seqIdx = (seqNumber - s[seqTop]) / 255;
        s[seqIdx % 8] = seqNumber;
        System.arraycopy(data, 0, w, (seqIdx % 8) * 255, data.length);
    }
    public void Print(){
        for(int i = 0; i < 8; i++){
            System.out.print(s[i] + ", ");
        }
    }
}
