/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

/**
 *
 * @author Adam Plansky
 */
public class Data {

    public void compareData(Send send, Receive r) {
        //first packet
        if (send.getFlag() == 4) {
        }
    }

    public boolean faultyPacket(Send s, Receive r) {

        //failure connection
        if (s.getFlag() != 4 && (r.getIdCon() != s.getIdCon())) {
            return false;
        } else if (r.getFlag()> 4 || r.getFlag() ==3 ) {
            return false;
        } else if (r.getFlag() == 4 && r.data.length == 1) {
            if (r.data[0] == 0x01 || r.data[0] == 0x02) {
                return true;
            } else {
                return false;
            }
        } else if(r.getFlag() == 4 && r.dataLen != 1){
            System.out.println("xxxxxx");
            return false;
        } else if(r.getFlag() == 2 && r.data.length != 0){
            System.out.println("--------");
            return false;
        }
        return true;
    }
    public void setPackets(Send s, Receive r){
        if(s.getAck() == r.getSeq()){
            
        }
    }
}
