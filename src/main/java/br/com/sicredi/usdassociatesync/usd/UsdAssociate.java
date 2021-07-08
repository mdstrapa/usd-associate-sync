package br.com.sicredi.usdassociatesync.usd;



public class UsdAssociate {
    String last_name;
    String zDataNascimento;
    String zCpf;
    String zContaCorrente;
    String pemail_address;
    UsdCompany company;
    UsdContactType type;


    public UsdAssociate(String last_name, String zDataNascimento, String zCpf, String zContaCorrente, UsdCompany company){
        this.last_name = last_name;
        this.zDataNascimento = zDataNascimento;
        this.zCpf = zCpf;
        this.zContaCorrente = zContaCorrente;
        this.pemail_address = zCpf + zContaCorrente;
        this.company = company;
        this.type = new UsdContactType();
    }

    public Boolean save(){
        Boolean result = false;

        return result;
    }

    public void setAssociateKey(String associateKey){
        this.pemail_address = associateKey;
    }
}
