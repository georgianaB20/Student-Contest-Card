package com.company;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadClientInterface;
import com.sun.javacard.apduio.CadTransportException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class wallet {
    private CadClientInterface cad;
    private Apdu apdu;
    public Student student;
    private Convert c=new Convert();

    public wallet(CadClientInterface cad,Apdu apdu){
        this.cad = cad;
        this.apdu = apdu;
    }

    /////////     INITIALIZARE WALLET
    public void createWallet(CadClientInterface cad, Apdu apdu, byte id) throws IOException, CadTransportException {
        Scanner keyboard = new Scanner(System.in);

        //luam datele din BD ale studentului
        byte[] pin=new byte[]{id, (byte) ((id+1)%10), (byte) ((id+2)%10), (byte) ((id+3)%10)};

        //executam comand de createWallet pentru a putea lucra cu el
        byte[] base=new byte[]{(byte) 0x80, (byte) 0xB8, 0x00, 0x00, 0x14, 0x0a, (byte) 0xa0, 0x0, 0x0, 0x0, 0x62, 0x3, 0x1, 0xc, 0x6, 0x1, 0x08, 0x0, 0x0, 0x04};
        byte[] createWallet = concat(base,pin);

        Command command=new Command(createWallet);
        command.setApdu(apdu);
        cad.exchangeApdu(apdu);

        //verificam daca s-a creat wallet-ul
        if(!check(apdu.getSw1Sw2())){
            System.out.println("Eroare la conectare");
        }

        //selectam walletul
        selectWallet();

        //verificam pinul
        verifyPin(pin);

        //pregatim walletul - trimitem datele despre fiecare disciplina la card
        this.student=new Student(id);
        if(this.student.getId()!=0)
            setWallet();
        else
            return;

    }

    //executa comanda APDU de selectare a Wallet-ului
    private void selectWallet() throws IOException, CadTransportException {
        Command command=new Command(new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, 0x0a, (byte) 0xa0, 0x0, 0x0, 0x0, 0x62, 0x3, 0x1, 0xc, 0x6, 0x1, 0x7F});
        command.setApdu(apdu);
        cad.exchangeApdu(apdu);

        if(!check(apdu.getSw1Sw2())){
            System.out.println("Eroare la selectare wallet");
        }
    }

    //parcurge toate disciplinele si le trimite datele la wallet
    private void setWallet() throws IOException, CadTransportException {
        for (Disciplina d : student.discipline){
            setData(d);
        }
    }

    /////////     CITIRE DE LA TASTATURA A DATELOR SI VALIDAREA LOR

    //citeste de la utilizator pinul si returneaza bytes din pin
    public byte[] getPin(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Introduceti PIN");
        short pinNumbers = keyboard.nextShort();
        byte[] pin = new byte[4];

        pin[0]=(byte)(pinNumbers/1000);
        pinNumbers%=1000;
        pin[1]=(byte)(pinNumbers/100);
        pinNumbers%=100;
        pin[2]=(byte)(pinNumbers/10);
        pinNumbers%=10;
        pin[3]=(byte)pinNumbers;

        return pin;
    }

    //citeste de la utilizator codul disciplinei si punctajul obtinut, returneaza un vector de bytes cu cele 2 valori
    public byte[] registerKeyboard(){
        boolean check_data=false;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Alegeti codul concursului:");
        System.out.println(this.student.getCodes()); //afisam codurile concursurilor si denumirile materiilor
        System.out.print("Cod: ");
        byte[] data=new byte[2];
        data[0] = (byte)keyboard.nextShort();

        for(Disciplina d:this.student.discipline){ //verificam daca codul dat este corect
            if(d.getConcurs()==data[0])
                check_data=true;
        }
        if(!check_data){ //codul este gresit -> afisam eroare
            System.out.println("Cod gresit");
            return new byte[]{0x00,0x00};
        }else { // codul este corect -> preluam punctajul
            System.out.println("Introduceti punctajul:");
            data[1] = (byte) keyboard.nextShort();
            return data;
        }

    }


    /////////     CONSTRUIREA COMENZILOR APDU SI TRIMITEREA LA SIMULATOR

    // trimite comanda VERIFY la simulator pentru validarea pin-ului
    public boolean verifyPin(byte[] pin) throws IOException, CadTransportException {
        byte[] base=new byte[]{(byte) 0x80, 0x20 ,0x00, 0x00, 0x04};

        byte[] verify=concat(base,pin);

        Command command=new Command(verify);
        command.setApdu(apdu);
        cad.exchangeApdu(apdu);

        if(!check(apdu.getSw1Sw2())){
            System.out.println("Eroare la validare pin");
            return false;
        }
        else{
            return true;
        }
    }

    // trimite comanda REGISTER la simulator pentru inregistrarea punctajului la un concurs
    public void register(int cod,int punctaj) throws IOException, CadTransportException {
        byte[] base=new byte[]{(byte) 0x80, 0x65 ,0x00, 0x00, 0x02};
        byte[] data=new byte[]{c.ITH(cod),c.ITH(punctaj)};
        byte[] verify=concat(base,data);
//        System.out.println(Arrays.toString(verify));

        Command command=new Command(verify);
        command.setApdu(apdu);
        cad.exchangeApdu(apdu);

        if(!check(apdu.getSw1Sw2())){
//            System.out.println(getHexString(apdu.getResponseApduBytes()));
            System.out.println("Eroare la inregistrare punctaj!");
        }
        else
        {
            System.out.println("Punctajul a fost inregistrat");
        }
    }

    //trimite comanda UPDATE la simulator pentru echivalarea notei la o materie cu punctaj la concurs >= 80
    public void update(int id_disc) throws IOException, CadTransportException {
        byte[] base=new byte[]{(byte) 0x80, 0x66 ,0x00, 0x00, 0x01};
        byte[] data=new byte[]{c.ITH(id_disc)};
        byte[] verify=concat(base,data);

        Command command=new Command(verify);
        command.setApdu(apdu);
        cad.exchangeApdu(apdu);

        if(!check(apdu.getSw1Sw2())){
            System.out.println("Eroare la echivalare nota!");
        }
    }

    //trimite comanda GET_DATA
    public void getData(int id) throws IOException, CadTransportException {
        byte[] base=new byte[]{(byte) 0x80, 0x63 ,0x00, 0x00, 0x01,c.ITH(id),0x7F};

        Command command = new Command(base);
        command.setApdu(apdu);
//        System.out.println(getHexString(apdu.getCommandApduBytes()));
        cad.exchangeApdu(apdu);

        if(!check(apdu.getSw1Sw2())){
            System.out.println(getHexString(apdu.getSw1Sw2()));
            System.out.println("Eroare la preluare date!");
        }
        else{
            byte[] response = apdu.getResponseApduBytes();
            System.out.println(getHexString(response));
            int punctaj=c.HTI(response[12]); //preluam punctajul din raspunsul de la simulator
            if(punctaj >= 80){ //verificam conditia >=80
                for(Disciplina d:this.student.discipline){
                    if(d.getDisc_id()==id){ //am gasit disciplina cu punctajul>=80
                        System.out.println("Nota la materia "+d.getDenumire()+" a fost actualizata de la "+d.getNota()+" la 10.");
                        d.setNota(10); // actualizam nota  in BD
                        this.update(id);// actualizam nota pe card
                    }
                }
            }
            else
                System.out.println("Nota nu a fost actualizata.");
        }
    }

    //trimite comanda GET_ALL la simulator
    public void getAllPoints() throws IOException, CadTransportException {
        byte[] base=new byte[]{(byte) 0x80, 0x62 ,0x00, 0x00, 0x00,0x7F};
//        byte[] data=new byte[]{c.ITH(id_disc)};
//        byte[] verify=concat(base,new byte[]);

        Command command=new Command(base);
        command.setApdu(apdu);
        cad.exchangeApdu(apdu);

        if(!check(apdu.getSw1Sw2())){
            System.out.println("Eroare la echivalare nota!");
        }
        else{
            byte[] response = apdu.getResponseApduBytes();
//            System.out.println(getHexString(response));
            int count=0;
            for(int i=6;i<11;i++) {
                if(c.HTI(response[i])>=80){
                    count++;
                }
            }
            if(count >= 3){
                this.student.setPrioritate(1);
                System.out.println("Campul prioritate a fost actualizat cu succes!");
            }
            else{
                System.out.println("Campul prioritate nu a fost actualizat!");
            }
        }
    }

    //trimite comanda SET_DATA la simulator pentru a memora datele unei discipline pe card
    private void setData(Disciplina d) throws IOException, CadTransportException {
        byte[] base=new byte[]{(byte) 0x80,0x64,0x00,0x00,0x06};
        byte[] data=new byte[]{c.ITH(d.getDisc_id()), c.ITH(d.getNota()), c.ITH(d.getZi()), c.ITH( d.getLuna()), c.ITH(d.getAn()), c.ITH(d.getConcurs())};
        byte[] send = concat(base,data);
//        System.out.println(Arrays.);

        Command command=new Command(send);
        command.setApdu(apdu);
//        System.out.println(getHexString(apdu.getCommandApduBytes()));
        cad.exchangeApdu(apdu);

        if(!check(apdu.getSw1Sw2())) {
            System.out.println("Eroare la trimitere date.");
        }
    }


    /////////     FUNCTII APELATE DIN TERMINAL

    //citesc de la utilizator datele necesare si apeleaza functiile pentru comenzile APDU, afiseaza raspunsurile in Terminal
    public void registerTerminal() throws IOException, CadTransportException {
        byte[] pin=getPin();
        if(verifyPin(pin)) {
            byte[] data = this.registerKeyboard();
            if(data[0]==0x00 && data[1]==0x00)
                return;
            else
                this.register(data[0], data[1]);
        }

    }
    public void validationTerminal() throws IOException, CadTransportException {
        byte[] pin=getPin();
        if(verifyPin(pin)) {
            Scanner keyboard = new Scanner(System.in);
            boolean check_data=false;
            System.out.println("Alegeti materia la care doriti sa echivalati nota");
            System.out.println(this.student.getDiscipline());
            System.out.print("Cod: ");
            int cod_disc = keyboard.nextInt();
            for(Disciplina d: this.student.discipline){
                if(d.getDisc_id()==cod_disc){
                    check_data=true;
                }
            }
            if(check_data)
                this.getData(cod_disc);
            else
            {
                System.out.println("Cod incorect");
                return;
            }
        }
    }
    public void updateTerminal() throws IOException, CadTransportException {
        byte[] pin=getPin();
        if(verifyPin(pin)) {
            System.out.println("Incepem actualizarea...");
            this.getAllPoints();
        }
    }

    /////////     FUNCTII AUXILIARE PENTRU MODELAREA COMENZILOR APDU SI VERIFICAREA CODURILOR DE EROARE

    // verifica daca SW1 SW2 : 90 00
    private boolean check(byte[] sw1sw2){
        //System.out.println(Arrays.toString(sw1sw2));
        boolean sw1 = sw1sw2[0] == -112;
        boolean sw2 = sw1sw2[1] == 0;
        return sw1==true&&sw2==true;
    }

    //concateneaza doi vectori de bytes, folosit in construirea comenzilor APDU
    private byte[] concat(byte[] base,byte[] data){
        byte[] command = new byte[base.length+data.length+1];
        System.arraycopy(base, 0, command, 0, base.length);
        System.arraycopy(data, 0, command, base.length, data.length);
        command[command.length-1] = 0x7F;
        return command;
    }

    //afiseaza un vector de bytes in reprezentarea lui hexadecimala, functie folosita la debug
    public String getHexString(byte[] numbers){
        StringBuilder sb = new StringBuilder(numbers.length * 2);
        for(byte b: numbers) {
            sb.append(String.format("%02x", b));
            sb.append(" ");
        }
        return sb.toString();

    }

    /////////     FUNCTII PENTRU CITIREA CAP-WALLET SI TRIMITEREA LA SIMULATOR
    private void setApdu(String[] bytess){
        byte[] command = new byte[bytess.length];
        int i=0;

        //convertim sirurile ce contin reprezentari ale numerelor hexadecimale in bytes
        for(String b : bytess) {
            int it = Integer.parseInt(b, 16);
            BigInteger bigInt = BigInteger.valueOf(it);
            byte[] bytearray = (bigInt.toByteArray());

            if(bytearray.length == 2) {
                command[i]=bytearray[1];
            }
            else {
                command[i] = bytearray[0];
            }
            i++;
        }

        //pregatim comanda APDU
        apdu.command = new byte[]{command[0],command[1],command[2],command[3]}; //setam CLA,INS,P1,P2
        apdu.setLc(command[4]); //setam Lc
        apdu.setLe(command[command.length-1]); //setam Le
        if(command.length > 6) { // daca comanda are date de trimis, le adaugam aici
            byte[] dataIn = new byte[command[4]];
            int j = 0;
            for(int k=5;k<dataIn.length+5;k++) {
                dataIn[j] = command[k];
                j++;
            }
            apdu.setDataIn(dataIn);
        }

    }
    public void sendCAPWallet(){
        String path="C:/Program Files (x86)/Oracle/Java Card Development Kit Simulator 3.1.0/samples/classic_applets/Wallet/applet/apdu_scripts/cap-Wallet.script";
        byte[] command;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path)); //citim fisierul cap-Wallet.script linie cu linie
            String line = reader.readLine();
            while (line != null) {
                if(line.length() > 0) {
                    //verificam daca linia are o comanda pe ea
                    if(!line.contains("//") && !line.contains("powerup") && !line.contains("powerdown") && !line.contains("output on")){
                        //convertim linia intr-un vector de Stringuri ce contin bytes din comanda
                        line = line.replace(" ","");
                        String[] bytess = line.split("0x|;");
                        bytess=Arrays.copyOfRange(bytess,1,bytess.length);

                        //setam comanda
                        setApdu(bytess);
                        cad.exchangeApdu(apdu); //trimitem la simulator

                        line = reader.readLine();
                    }else{
                        line = reader.readLine();
                    }
                }else {
                    line = reader.readLine();
                }
            }
            reader.close();
        } catch (IOException | CadTransportException e ) {
            e.printStackTrace();
        }

    }
}
