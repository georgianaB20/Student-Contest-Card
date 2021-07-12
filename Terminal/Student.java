package com.company;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Student {
    private int id;
    private String nume;
    private String prenume;
    private int prioritate;
    List<Disciplina> discipline=new ArrayList<Disciplina>();
    private Convert c=new Convert();

    public Student(int id) throws IOException {
        boolean check_data=false; // verificam ca studentul exista
        //citim din fisier toate datele despre student
        String row=new String();
        BufferedReader csvReader = new BufferedReader(new FileReader("C:/Users/georg/IdeaProjects/sca_try2/src/com/company/BD/baza_date.csv"));
        while ((row = csvReader.readLine()) != null) {
            //verificam ca linia are caractere si nu este comentata
            if (!row.contains("//") && row.length()>0) {
                String[] data = row.split(",");
                // luam cursurile si le adaugam la vectorul de discipline ale studentului
                if (id==Integer.parseInt(data[0],10)){
                    this.id=id;
                    check_data=true;
                    int disc_id=Integer.parseInt(data[1],10);
                    int nota=Integer.parseInt(data[3],10);
                    int zi=Integer.parseInt(data[4],10);
                    int luna = Integer.parseInt(data[5],10);
                    int an = Integer.parseInt(data[6],10);
                    int sid = Integer.parseInt(data[0],10);
                    discipline.add(new Disciplina(disc_id,sid,data[2],nota,zi,luna,an));
                }
                //System.out.println(Arrays.toString(data));
            }
        }
        csvReader.close();
        if(!check_data){ //studentul nu exista -> afisam mesaj
            this.id=0;
            System.out.println("Studentul cu ID "+id+" nu exista.");
            return;
        }
        else { //studentul exista
            //luam numele si prenumele studentului
            csvReader = new BufferedReader(new FileReader("C:/Users/georg/IdeaProjects/sca_try2/src/com/company/BD/studenti.csv"));
            while ((row = csvReader.readLine()) != null) {
                if (!row.contains("//") && row.length() > 0) {
                    String[] data = row.split(",");
                    // luam prioritate, nume, prenume
                    if (id == StringToByte(data[0])) {
                        this.nume = data[2];
                        this.prenume = data[3];
                        this.prioritate = c.STI(data[1]);
                    }
                    //System.out.println(Arrays.toString(data));
                }
            }
            csvReader.close();
        }
    }

    public int getId(){return this.id;}

    //functie auxiliara, folosita la debug
    private byte StringToByte(String s){
        int it = Integer.parseInt(s, 16);
        BigInteger bigInt = BigInteger.valueOf(it);
        byte[] bytearray = (bigInt.toByteArray());

        if(bytearray.length == 2) {
            return bytearray[1];
        }
        else {
            return bytearray[0];
        }
    }

    //afisare date pe Terminal
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(this.nume+" "+this.prenume+"\n");
        sb.append("ID: "+this.id+" PRIORITATE: "+this.prioritate+"\n");
        for(Disciplina d : this.discipline){
            sb.append(d.toString());
        }
        return sb.toString();
    }

    public String getCodes(){
        StringBuilder sb=new StringBuilder();
        for(Disciplina d:this.discipline){
            sb.append(d.getCode());
        }
        return sb.toString();
    }

    public String getDiscipline(){
        StringBuilder sb=new StringBuilder();
        for(Disciplina d:this.discipline){
            sb.append(d.getDisc_id());
            sb.append("\t");
            sb.append(d.getDenumire());
            sb.append("\n");
        }
        return sb.toString();
    }

    //modificam campul Prioritate in fisier
    public void setPrioritate(int value) throws IOException {
        String row;
        StringBuilder sb=new StringBuilder();
        BufferedReader csvReader = new BufferedReader(new FileReader("C:/Users/georg/IdeaProjects/sca_try2/src/com/company/BD/studenti.csv"));
        while ((row = csvReader.readLine()) != null) { //citim fisierul linie cu linie
            if (!row.contains("//") && row.length()>0) {
                String[] data = row.split(",");
                // luam prioritate, nume, prenume
                if (id==c.STI(data[0])){ //am gasit studentul, modificam campul prioritate
                    sb.append(c.ITS(this.id));
                    sb.append(",");
                    sb.append(c.ITS(value));
                    this.prioritate=value;
                    sb.append(",");
                    sb.append(this.nume);
                    sb.append(",");
                    sb.append(this.prenume);
                    sb.append("\n");
                }
                else{
                    sb.append(row); //nu este studentul cautat -> apendam randul
                    sb.append("\n");
                }
            }
            else{
                sb.append(row); //linie de comentariu sau linie goala -> apendam randul
                sb.append("\n");
            }
        }
        csvReader.close();

        //suprascriem fisierul de studenti cu modificarile facute pentru studentul vizat
        FileWriter csvWriter = new FileWriter("C:/Users/georg/IdeaProjects/sca_try2/src/com/company/BD/studenti.csv");
        csvWriter.append(sb.toString());
        csvWriter.flush();
        csvWriter.close();
    }
}
