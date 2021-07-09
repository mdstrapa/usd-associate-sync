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

import java.sql.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@RestController
public class Controller {
    
    List<Entity> usdEntities = new ArrayList<>();
    Usd usd = new Usd();
    
    Teradata teraData = new Teradata();

    @GetMapping("/")
    public String index(@RequestParam String thresholdDate){
        return "This API syncs associates between TeraData and USD";
    }

    private Entity getEntity(List<Entity> entities, String entityCode){
        for(Entity entity : entities){     
            if (entity.getCode().equals(entityCode)){
                return entity;
            }
        }
        return null;
    }

    // private String showUsdEntities(List<Entity> entities) {
    //     String htmlBody = "<table>";
    //     for (Entity entity : entities) {
    //         htmlBody = htmlBody 
    //         + "<tr><td>" + entity.getUsdId() + "</td>"
    //         + "<td>" + entity.getCode() + "</td>"
    //         + "<td>" + entity.getName() + "</td></tr>";
    //     }
    //     htmlBody = htmlBody + "</table>";
    //     return htmlBody;
    // }

    @GetMapping("syncCooperativeAssociates")
    public String syncCooperativeAssociates(@RequestParam String coopCode){

        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

        String htmlBody = "<table>";

        int associateCreationCount = 0;
        
        usdEntities = usd.getEntities();
        Connection usdDBConnection = usd.createDBConnection();

        System.out.println("qtd: " + usdEntities.size());

        List<Associate> associates = teraData.getCooperativeAssociates(coopCode);

        try{
            for (Associate associate : associates) {
    
                htmlBody = htmlBody + "<tr><td>" + associate.getFullName() + "</td><td>" + associate.getBorndate() + "</td><td>";
                
                if (!usd.checkIfAssociateExists(associate,usdDBConnection)) {
                    htmlBody = htmlBody + "Will be created";
    
                    createAssociate(associate);
                    associateCreationCount++;
    
                }
                htmlBody = htmlBody + "</td></tr>";
            }
            usdDBConnection.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }

        htmlBody = htmlBody + "</table>";


        Timestamp finishTimestamp = new Timestamp(System.currentTimeMillis());
        htmlBody = "Begining: " + startTimestamp.toString() + "<br>Finish: " + finishTimestamp.toString() + "<br>Qtd Associates: " + associates.size() + "  Qtd created: " + associateCreationCount + "<br>" + htmlBody; 
        return htmlBody;
    }

    @GetMapping("syncNewAssociates")
    public String syncNewAssociates(@RequestParam String thresholdDate){
        String htmlBody = "";
        usdEntities = usd.getEntities();
        Connection usdDBConnection = usd.createDBConnection();

        List<Associate> associatesToCreate = teraData.getNewAssociates(thresholdDate);

        try{

            for (Associate associate : associatesToCreate) {
    
                htmlBody = htmlBody + "<tr><td>" + associate.getFullName() + "</td><td>" + associate.getBorndate() + "</td><td>";
                            
                if(!usd.checkIfAssociateExists(associate,usdDBConnection)){
    
                    htmlBody = htmlBody + "Will be created";
    
                    createAssociate(associate);
                }
                htmlBody = htmlBody + "</td></tr>";
            }
    
            usdDBConnection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return htmlBody;
    }


    private void createAssociate(Associate associate) {
        Entity entity = getEntity(usdEntities, associate.getEntity() + associate.getUa());

        UsdCompany usdCompany = new UsdCompany(entity.getUsdId());

        usd.createAssociate(associate,usdCompany);
    }

}
