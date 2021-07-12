package com.company;

import com.sun.javacard.apduio.Apdu;

import java.math.BigInteger;
import java.util.Arrays;

public class Command {
    private byte[] command;
    private Convert c=new Convert();

    public Command(byte[] command){
        this.command = command;
    }

    public Command(String command){
        command = command.replace(" ","");
        String[] bytess = command.split("0x|;");
        bytess= Arrays.copyOfRange(bytess,1,bytess.length);

        this.command = new byte[bytess.length];
        int i=0;


        for(String b : bytess) {
            this.command[i]=c.STH(b);
//            int it = Integer.parseInt(b, 16);
//            BigInteger bigInt = BigInteger.valueOf(it);
//            byte[] bytearray = (bigInt.toByteArray());
//
//            if(bytearray.length == 2) {
//                //command[i] = bytearray[0];
//                this.command[i]=bytearray[1];
//                //i++;
//            }
//            else {
//                this.command[i] = bytearray[0];
//            }
            i++;
        }
    }

    public void setApdu(Apdu apdu){
        apdu.command = new byte[]{command[0],command[1],command[2],command[3]};
        apdu.setLc(command[4]);
        apdu.setLe(command[command.length-1]);
        if(command.length > 6) {
            byte[] dataIn = new byte[command[4]];
            int j = 0;
            for(int k=5;k<dataIn.length+5;k++) {
                dataIn[j] = command[k];
                j++;
            }
            apdu.setDataIn(dataIn);
        }
    }
}
