package com.edw.bean;
// Generated Jun 25, 2011 3:23:26 PM by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * Dosen generated by hbm2java
 */
public class Dosen  implements java.io.Serializable {


     private String iddosen;
     private String namadosen;
     private Set matakuliahs = new HashSet(0);

    public Dosen() {
    }

	
    public Dosen(String iddosen) {
        this.iddosen = iddosen;
    }

    public Dosen(String iddosen, String namadosen) {
        this.iddosen = iddosen;
        this.namadosen = namadosen;
    }

    public Dosen(String iddosen, String namadosen, Set matakuliahs) {
       this.iddosen = iddosen;
       this.namadosen = namadosen;
       this.matakuliahs = matakuliahs;
    }
   
    public String getIddosen() {
        return this.iddosen;
    }
    
    public void setIddosen(String iddosen) {
        this.iddosen = iddosen;
    }
    public String getNamadosen() {
        return this.namadosen;
    }
    
    public void setNamadosen(String namadosen) {
        this.namadosen = namadosen;
    }
    public Set getMatakuliahs() {
        return this.matakuliahs;
    }
    
    public void setMatakuliahs(Set matakuliahs) {
        this.matakuliahs = matakuliahs;
    }

    @Override
    public String toString() {
        return "Dosen{" + "iddosen=" + iddosen + ",namadosen=" + namadosen + ",matakuliahs=" + matakuliahs + '}';
    }

}


