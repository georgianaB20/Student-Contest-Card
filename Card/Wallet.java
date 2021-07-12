/** 
 * Copyright (c) 1998, 2021, Oracle and/or its affiliates. All rights reserved.
 * 
 */

/*
 */

/*
 * @(#)Wallet.java	1.11 06/01/03
 */

// Project->Build All

package com.oracle.jcclassic.samples.wallet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.OwnerPIN;
import javacard.framework.PIN;
//import com.oracle.jcclassic.samples.wallet.Disciplina;
import java.lang.*;


public class Wallet extends Applet {

    /* constants declaration */

    // code of CLA byte in the command APDU header
    final static byte Wallet_CLA = (byte) 0x80;

    // codes of INS byte in the command APDU header
    final static byte VERIFY = (byte) 0x20;
    final static byte RESET = (byte) 0x2C;
    //functiile pentru student wallet
    final static byte REGISTER = (byte) 0x65;
    final static byte UPDATE = (byte) 0x66;
    final static byte GET_ALL = (byte) 0x62;
    final static byte GET_DATA = (byte) 0x63;
    final static byte SET_DATA  = (byte) 0x64;
    
    

    // maximum number of incorrect tries before the
    // PIN is blocked
    final static byte PIN_TRY_LIMIT = (byte) 0x03;
    // maximum size PIN
    final static byte MAX_PIN_SIZE = (byte) 0x08;

    // signal that the PIN verification failed
    final static short SW_VERIFICATION_FAILED = 0x6300;
    // signal the the PIN validation is required
    // for a credit or a debit transaction
    final static short SW_PIN_VERIFICATION_REQUIRED = 0x6301;
    // signal invalid transaction amount
    
    final static short SW_SECURITY_STATUS_NOT_SATISFIED= 0x6982;
    
    //erori student wallet
    final static short SW_BAD_COURSE = 0x6A20;

     
    

    /* instance variables declaration */
    OwnerPIN pin;
    short balance;
    Disciplina[] discipline= new Disciplina[5];
    short nr_discipline = 0; //retinem nr de discipline ocupate in vector ca sa adaugam la finalul vectorului disciplinele noi

    private Wallet(byte[] bArray, short bOffset, byte bLength) {
    	//register();
        // It is good programming practice to allocate
        // all the memory that an applet needs during
        // its lifetime inside the constructor
        pin = new OwnerPIN(PIN_TRY_LIMIT, MAX_PIN_SIZE);

        byte iLen = bArray[bOffset]; // aid length
        bOffset = (short) (bOffset + iLen + 1);
        byte cLen = bArray[bOffset]; // info length
        bOffset = (short) (bOffset + cLen + 1);
        byte aLen = bArray[bOffset]; // applet data length

        // The installation parameters contain the PIN
        // initialization value
        pin.update(bArray, (short) (bOffset + 1), aLen);
        register();

    } // end of the constructor

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        // create a Wallet applet instance
        new Wallet(bArray, bOffset, bLength);
    } // end of install method

    @Override
    public boolean select() {

        // The applet declines to be selected
        // if the pin is blocked.

    	if (pin.getTriesRemaining()==0)
    		return false;
    	
        return true;

    }// end of select method

    @Override
    public void deselect() {

        // reset the pin value
        pin.reset();

    }

    @Override
    public void process(APDU apdu) {

        // APDU object carries a byte array (buffer) to
        // transfer incoming and outgoing APDU header
        // and data bytes between card and CAD

        // At this point, only the first header bytes
        // [CLA, INS, P1, P2, P3] are available in
        // the APDU buffer.
        // The interface javacard.framework.ISO7816
        // declares constants to denote the offset of
        // these bytes in the APDU buffer

        byte[] buffer = apdu.getBuffer();
        // check SELECT APDU command

        if (apdu.isISOInterindustryCLA()) {
            if (buffer[ISO7816.OFFSET_INS] == (byte) (0xA4)) {
                return;
            }
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        // verify the reset of commands have the
        // correct CLA byte, which specifies the
        // command structure
        if (buffer[ISO7816.OFFSET_CLA] != Wallet_CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        switch (buffer[ISO7816.OFFSET_INS]) {
            case VERIFY:
                verify(apdu);
                return;
            case RESET:
            	reset_pin_try_counter(apdu);
            	return;
            case REGISTER:
            	register(apdu);
            	return;
            case UPDATE:
            	update(apdu);
            	return;
            case GET_ALL:
            	get_all(apdu);
            	return;
            case GET_DATA:
            	get_data(apdu);
            	return;
            case SET_DATA:
            	set_data(apdu);
            	return;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }

    } // end of process method

   

    private void verify(APDU apdu) {
    	if(pin.getTriesRemaining() == 0) {
        	ISOException.throwIt(SW_SECURITY_STATUS_NOT_SATISFIED);
        }
    	
        byte[] buffer = apdu.getBuffer();
        // retrieve the PIN data for validation.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());
        
        // check pin
        // the PIN data is read into the APDU buffer
        // at the offset ISO7816.OFFSET_CDATA
        // the PIN data length = byteRead
        if (pin.check(buffer, ISO7816.OFFSET_CDATA, byteRead) == false) {
        	ISOException.throwIt(SW_VERIFICATION_FAILED);
        }

    } // end of validate method
    
    private void reset_pin_try_counter(APDU apdu) {
    	if(pin.getTriesRemaining()==0) {
	    	byte[] buffer = apdu.getBuffer();
	    	boolean corect_puk=true;
	    	
	    	for(short i=0;i<8;i++) {
	    		if(buffer[(short)(i+5)]!=(byte)0x09) {
	    			corect_puk=false;
	    			break;
	    		}//if
	    	}//for
	    	
	    	if(corect_puk==true) {
	    		pin.resetAndUnblock();
	    	}
	    	
	    }//if
    }//end reset_pin_try_counter
    
    //primeste codul unui concurs corespunzator unei discipline si punctajul obtinut
    //actualizeaza punctajul de la concurs pe card
    private void register(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
    	byte bad_course=1; //verficam daca disciplina exista
    	for(byte i = 0;i<5;i++) {
    		if(buffer[5]==discipline[i].cod_concurs) { //am gasit disciplina
    			bad_course=0;
    			discipline[i].punctaj=buffer[6]; //actualizam punctajul
    		}
    	}
    	if(bad_course == 1) {
        	ISOException.throwIt(SW_BAD_COURSE);
    	}
    }
    
    //primeste codul unei discipline
    //actualizeaza nota de pe card a disciplinei la 10
    private void update(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
    	byte bad_course =(byte)0x01; //pentru a verifica daca disciplina exista pe card
    	for(byte i=0;i<5;i++) {
    		if(buffer[5]==(byte)discipline[i].cod_disciplina) { //am gasit disciplina
    			bad_course=0; 
    			discipline[i].nota = (byte)0x0A; //actualizam datele
    		}
    	}
    	if(bad_course==1) {
    		ISOException.throwIt(SW_BAD_COURSE);
    	}
    }
    
    //trimite toate punctajele de la concursuri la Terminal
    private void get_all(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
    	short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // setam lungimea raspunsului
        apdu.setOutgoingLength((byte) 5);

        //scriem in raspuns punctajele disciplinelor
    	for(byte i=0;i<5;i++) {
    		buffer[i] = (byte) (discipline[i].punctaj);
    	}
    	//trimitem raspunsul
    	apdu.sendBytes((short) 0, (short) 5);
    	
    }
    
    //primeste cod_disciplina
    //trimite datele despre o disciplina : nota, data, cod_concurs si punctaj 
    private void get_data(APDU apdu){
    	byte[] buffer = apdu.getBuffer();
    	byte bad_course = 0x01; //pentru a verifica daca cursul dat exista in disciplinele de pe card
    	short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

    	for(byte i=0;i<5;i++) {
    		if(buffer[5]==(byte)discipline[i].cod_disciplina) {
    			bad_course=0x00;
    			
    	        // scriem cati bytes trimitem in raspuns
    	        apdu.setOutgoingLength((byte) 6);

    	        //scriem in buffer-ul de output datele
    	        buffer[0] = (byte) (discipline[i].nota);
    	        buffer[1] = (byte) (discipline[i].zi);
    	        buffer[2] = (byte) (discipline[i].luna);
    	        buffer[3] = (byte) (discipline[i].an);
    	        buffer[4] = (byte) (discipline[i].cod_concurs);
    	        buffer[5] = (byte) (discipline[i].punctaj);

    	        // trimitem datele
    	        apdu.sendBytes((short) 0, (short) 6);
    		}
    	}
    	if(bad_course==(byte)0x01) {
    		ISOException.throwIt(SW_BAD_COURSE);
    	}
    }
    
    //adaugam in vectorul de discipline o disciplina noua si incrementam nr de discipline memorate
    private void set_data(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	discipline[nr_discipline] = new Disciplina(buffer[5],buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],(byte)0x00);
    	nr_discipline += 1;
    }
    
} // end of class Wallet

