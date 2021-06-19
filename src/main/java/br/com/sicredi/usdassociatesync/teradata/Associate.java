package br.com.sicredi.usdassociatesync.teradata;



public class Associate {
    String fullName;
    String bornDate;
    String cpfCnpj;
    String account;
    String entity;


    public Associate(String fullName, String bornDate, String cpfCnpj, String account, String entity){
        this.fullName = fullName;
        this.bornDate = bornDate;
        this.cpfCnpj = cpfCnpj;
        this.account = account;
        this.entity = entity;
    }

    public String getFullName(){
        return this.fullName;
    }

    public String getBorndate(){
        return this.bornDate;
    }

    public String getCpfCnpj(){
        return this.cpfCnpj;
    }

    public String getAccount(){
        return this.account;
    }

    public String getEntity(){
        return this.entity;
    }

}
