package br.com.sicredi.usdassociatesync.usd;



public class UsdAssociate {
    String last_name;
    String zDataNascimento;
    String zCpf;
    String zContaCorrente;
    String pager_addres;
    Entity company;

    public UsdAssociate(String last_name, String zDataNascimento, String zCpf, String zContaCorrente, Entity company){
        this.last_name = last_name;
        this.zDataNascimento = zDataNascimento;
        this.zCpf = zCpf;
        this.zContaCorrente = zContaCorrente;
        this.pager_addres = zCpf + zContaCorrente;
        this.company = company;
    }

    public Boolean save(){
        Boolean result = false;

        return result;
    }
}
