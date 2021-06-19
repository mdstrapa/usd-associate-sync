package br.com.sicredi.usdassociatesync.usd;

public class Entity {
    String name;
    String code;
    String usdId;

    public Entity(String usdId, String name){
        this.name = name;
        this.code = name.substring(0,name.indexOf("-") - 1);
        this.usdId = usdId;
    }


    public String getName(){
        return this.name;
    }

    public String getCode(){
        return this.code;
    }
    
    public String getUsdId(){
        return this.usdId;
    }

}
