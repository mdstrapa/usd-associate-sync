package br.com.sicredi.usdassociatesync.usd;



public class UsdAssociate {
    String last_name;
    String zdata_nascimento;
    String zcpf;
    String znumero_conta;
    String pemail_address;
    UsdCompany company;
    UsdContactType type;


    public UsdAssociate(String last_name, String zDataNascimento, String zCpf, String zContaCorrente, UsdCompany company){
        this.last_name = last_name;
        this.zdata_nascimento = zDataNascimento;
        this.zcpf = zCpf;
        this.znumero_conta = zContaCorrente;
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
