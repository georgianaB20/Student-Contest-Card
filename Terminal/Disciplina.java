package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Disciplina {
    private int disc_id;
    private int id;
    private int nota;
    private int zi;
    private int luna;
    private int an;
    private int concurs;
    private int punctaj;
    private String denumire;
    private Convert c=new Convert();

    public Disciplina(int did){
        this.id=did;
    }

    public Disciplina(int disc_id,int id, String nume,int nota,int zi, int luna, int an) throws IOException {
        this.id=id;
        this.denumire=nume;
        this.nota=nota;
        this.zi=zi;
        this.luna=luna;
        this.an=an;
        this.setDisc_id(disc_id);
        getData();
    }

    //citirea codului concursului din BD
    private void getData() throws IOException {
        String row=new String();
        BufferedReader csvReader = new BufferedReader(new FileReader("C:/Users/georg/IdeaProjects/sca_try2/src/com/company/BD/discipline.csv"));
        while ((row = csvReader.readLine()) != null) {
            if (!row.contains("//") && row.length()>0) {
                String[] data = row.split(",");
                // luam cursurile
                if (this.getDisc_id() ==c.STI(data[0])){
                    this.concurs=c.STI(data[1]);
                }
            }
        }
        csvReader.close();
    }

    public void setNota(int value) throws IOException {
        String row;
        StringBuilder sb = new StringBuilder();
        BufferedReader csvReader = new BufferedReader(new FileReader("C:/Users/georg/IdeaProjects/sca_try2/src/com/company/BD/baza_date.csv"));
        while ((row = csvReader.readLine()) != null) {
            if (!row.contains("//") && row.length() > 0) {
                String[] data = row.split(",");
                // luam prioritate, nume, prenume
                if (getDisc_id() == c.STI(data[1]) && id == c.STI(data[0])) {
                    sb.append(c.ITS(this.id));
                    sb.append(",");
                    sb.append(c.ITS(this.getDisc_id()));
                    //this.prioritate = value;
                    sb.append(",");
                    sb.append(this.denumire);
                    sb.append(",");
                    sb.append(c.ITS(value));
                    this.nota=value;
                    sb.append(",");
                    sb.append(c.ITS(this.zi));
                    sb.append(",");
                    sb.append(c.ITS(this.luna));
                    sb.append(",");
                    sb.append(c.ITS(this.an));
                    sb.append("\n");
                } else {
                    sb.append(row);
                    sb.append("\n");
                }
            } else {
                sb.append(row);
                sb.append("\n");
            }
        }
        csvReader.close();

        FileWriter csvWriter = new FileWriter("C:/Users/georg/IdeaProjects/sca_try2/src/com/company/BD/baza_date.csv");
        csvWriter.append(sb.toString());
        csvWriter.flush();
        csvWriter.close();
    }

    public int getId() {
        return id;
    }

    public int getNota() {
        return nota;
    }

    public int getZi() {
        return zi;
    }

    public int getLuna() {
        return luna;
    }

    public int getAn() {
        return an;
    }

    public int getConcurs() {
        return concurs;
    }

    public String getDenumire() {
        return denumire;
    }


    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        sb.append(this.disc_id+" \tNota: "+this.nota+"\tData:"+this.zi+"-"+this.luna+"-"+this.an+" \t"+this.denumire+"\n");
        return sb.toString();
    }

    public String getCode(){
        StringBuilder sb= new StringBuilder();
        sb.append(this.concurs+" \t"+this.denumire+"\n");
        return sb.toString();
    }

    public int getDisc_id() {
        return disc_id;
    }

    public void setDisc_id(int disc_id) {
        this.disc_id = disc_id;
    }
}
