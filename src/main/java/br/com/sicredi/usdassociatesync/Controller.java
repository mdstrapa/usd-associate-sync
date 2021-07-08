package br.com.sicredi.usdassociatesync;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sicredi.usdassociatesync.usd.Usd;
import br.com.sicredi.usdassociatesync.usd.UsdAssociate;
import br.com.sicredi.usdassociatesync.usd.UsdCompany;
import br.com.sicredi.usdassociatesync.teradata.Associate;
import br.com.sicredi.usdassociatesync.teradata.Teradata;
import br.com.sicredi.usdassociatesync.usd.Entity;

import java.util.ArrayList;
import java.util.List;


@RestController
public class Controller {
    
    List<Entity> usdEntities = new ArrayList<>();

    @GetMapping("/")
    public String index(@RequestParam String thresholdDate){
        
        Usd usd = new Usd();
        usdEntities = usd.getEntities();

        String htmlBody = showUsdEntities(usdEntities);

        // Teradata teradata = new Teradata();
        // List<Associate> associatesToCreate = teradata.getNewAssociates(thresholdDate);

        // for (Associate associate : associatesToCreate) {
                        
        //     if(!usd.checkIfAssociateExists(associate)){

        //         Entity entity = getEntity(usdEntities, associate.getEntity());

        //         UsdAssociate usdAssociate = new UsdAssociate(associate.getFullName(), 
        //                     associate.getBorndate(), 
        //                     associate.getCpfCnpj(), 
        //                     associate.getAccount(), 
        //                     entity);
                
        //         usdAssociate.save();
        //     }
        // }
    
        return htmlBody;
    }


    private Entity getEntity(List<Entity> entities, String entityCode){
        for(Entity entity : entities){
            //System.out.println("codigo na lista: '" + entity.getCode() + "'");
            //System.out.println("codigo procurado: '" + entityCode + "'");
            
            if (entity.getCode().equals(entityCode)){
                System.out.println("ACHOU");
                return entity;
            }
        }
        return null;
    }

    private String showUsdEntities(List<Entity> entities) {
        String htmlBody = "<table>";
        for (Entity entity : entities) {
            htmlBody = htmlBody 
            + "<tr><td>" + entity.getUsdId() + "</td>"
            + "<td>" + entity.getCode() + "</td>"
            + "<td>" + entity.getName() + "</td></tr>";
        }
        htmlBody = htmlBody + "</table>";
        return htmlBody;
    }

    @GetMapping("showCooperativeAssociates")
    public String showCooperativeAssociates(@RequestParam String coopCode){
        String htmlBody = "<table>";
        
        Usd usd = new Usd();

        usdEntities = usd.getEntities();

        Teradata teraData = new Teradata();

        List<Associate> associates = teraData.getCooperativeAssociates(coopCode);

        for (Associate associate : associates) {
            htmlBody = htmlBody + "<tr><td>" + associate.getFullName() + "</td><td>" + associate.getBorndate() + "</td><td>";
            if (!usd.checkIfAssociateExists(associate)) {
                htmlBody = htmlBody + "Will be created";

                // System.out.println("Entity: " + associate.getEntity());
                // System.out.println("UA: " + associate.getUa());
                // System.out.println("Qtd Entities: " + usdEntities.size());

                Entity entity = getEntity(usdEntities, associate.getEntity() + associate.getUa());

                UsdCompany usdCompany = new UsdCompany(entity.getUsdId());

                usd.createAssociate(associate,usdCompany);

            }
            htmlBody = htmlBody + "</td></tr>";
        }

        htmlBody = htmlBody + "</table>";

        return htmlBody;
    }

    @GetMapping("/test")
    public String test(){
        String htmlBody = "";

        Usd usd = new Usd();

        usd.checkIfAssociateExists(new Associate("Rafaela","1993-01-16","5464523434","35335-2","0116","08"));


        return htmlBody;

    }


}
