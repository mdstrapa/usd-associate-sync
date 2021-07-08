package br.com.sicredi.usdassociatesync.teradata;



public class Associate {
    String fullName;
    String bornDate;
    String cpfCnpj;
    String account;
    String entity;
    String ua;


    public Associate(String fullName, String bornDate, String cpfCnpj, String account, String entity, String ua){
        this.fullName = fullName;
        this.bornDate = bornDate;
        this.cpfCnpj = cpfCnpj;
        this.account = account;
        this.entity = entity;
        this.ua = ua;
    }

    public String getFullName(){
        return this.fullName;
    }

    public String getBorndate(){
        String dateConverted = this.bornDate.substring(0,10);

        String[] dateElements = dateConverted.split("-");

        return dateElements[2] + "-" + dateElements[1] + "-" + dateElements[0];
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

    public String getUa(){
        return this.ua;
    }

}
