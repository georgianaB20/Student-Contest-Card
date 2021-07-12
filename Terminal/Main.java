package com.company;

import com.sun.javacard.apduio.*;
import com.company.wallet;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, CadTransportException {
//        String crefFilePath = "C:/Program Files (x86)/Oracle/Java Card Development Kit Simulator 3.1.0/bin/cref.bat";
//        Process process;
//        process = Runtime.getRuntime().exec(crefFilePath);
//        byte[] a=new byte[]{(byte) 0x80, (byte) 0xb8, (byte) 0x00, (byte) 0x00, (byte) 0x14};
//        System.out.println(Arrays.toString(a));

        CadClientInterface cad;
        Socket sock;
        Apdu apdu = new Apdu();
        try {
            sock = new Socket("localhost", 9025);
            InputStream is = sock.getInputStream();
            OutputStream os = sock.getOutputStream();
            cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, is, os);

            byte[] ATR = cad.powerUp();
            wallet w=new wallet(cad,apdu);

            Scanner keyboard = new Scanner(System.in);
            System.out.println("Introduceti ID:");
            int id=keyboard.nextInt();

            w.sendCAPWallet();
            w.createWallet(cad,apdu,(byte)id);

            System.out.print("Operatiune:\n 1.Inregistrare punctaj\n 2.Echivalare nota\n 3.Actualizare\n 4.Iesire\n Cod: ");
            int cod=keyboard.nextInt();
            while(true){

                switch (cod){
                    case 1:
                        w.registerTerminal();
                        break;
                    case 2:
                        w.validationTerminal();
                        break;
                    case 3:
                        w.updateTerminal();
                        break;
                    case 4:
                        System.out.println(w.student.toString());
                        break;
                    default: break;

                }
                System.out.println("Operatiune finalizata!");
                System.out.print("\nOperatiune:\n 1.Inregistrare punctaj\n 2.Echivalare nota\n 3.Actualizare\n 4.Situatie scolara\n 5.Iesire\n Cod: ");
                cod=keyboard.nextInt();
                if(cod == 5) //cod de iesire
                    break;


            }

        } catch (Exception e) {
            System.out.println(e);
        }



    }

}

