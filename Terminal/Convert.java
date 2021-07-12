package com.company;

import java.math.BigInteger;
import java.util.Arrays;

public class Convert {
    public Convert(){};
    //clasa auxiliara pentru conversia intre HEX,INT si Stringuri
    //int->hex
    public byte ITH(int nr){
        Integer a = nr;
        // Convert Integer number to byte value
        byte b = a.byteValue();
        //System.out.println(b);
        return b;
    }

    //String->int
    public int STI(String nr){
        return Integer.parseInt(nr,10);
    }

    //int->String
    public String ITS(int nr){
        return Integer.toString(nr);
    }

    //hex->int
    public int HTI(byte nr){
        Byte b = nr;
        return b.intValue();
    }

    //hex->String
    public String HTS(byte nr){
        return String.format("%02x", nr);
    }

    //String->hex
    public byte STH(String nr){
        int it = Integer.parseInt(nr, 16);
        BigInteger bigInt = BigInteger.valueOf(it);
        byte[] bytearray = (bigInt.toByteArray());

        if(bytearray.length == 2) {
            return bytearray[1];
        }
        else {
            return bytearray[0];
        }
    }
}
