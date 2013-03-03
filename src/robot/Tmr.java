/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

/**
 *
 * @author Adam Plansky
 */
public class Tmr {

    public long startTime;

    public Tmr() {
        this.startTime = System.currentTimeMillis();
    }

    public long getElapsedTime1() {
        long elapsed;
        elapsed = (System.currentTimeMillis() - startTime);
        return elapsed;
    }
}
