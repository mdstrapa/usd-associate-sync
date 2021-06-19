package br.com.sicredi.usdassociatesync.teradata;

import java.util.ArrayList;
import java.util.List;

public class Teradata {


     public List<Associate> getNewAssociates(String thresholdDate){
        List<Associate> associates = new ArrayList<>();

        associates.add(new Associate("Rafaela","1993-01-16","5464523434","35335-2","011608"));
        associates.add(new Associate("Marcos","1984-03-31","2342342","2333-2","011608"));

        return associates;
     }
    
}
