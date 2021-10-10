package br.com.sicredi.usdassociatesync;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sicredi.usdassociatesync.usd.Usd;
import br.com.sicredi.usdassociatesync.usd.UsdCompany;
import br.com.sicredi.usdassociatesync.teradata.Associate;
import br.com.sicredi.usdassociatesync.teradata.Teradata;
import br.com.sicredi.usdassociatesync.usd.Entity;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
@Slf4j
public class Controller {
    
    List<Entity> usdEntities = new ArrayList<>();
    Usd usd = new Usd();
    
    Teradata teraData = new Teradata();

    @GetMapping("/")
    public String index(){
        log.info("This API is up.");
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


    @PostMapping("syncCooperativeAssociates")
    public String syncCooperativeAssociates(@RequestParam String coopCode){
        log.info("Process Begins ---------------------------");

        List<Associate> associates = teraData.getCooperativeAssociates(coopCode);

        String htmlBody = syncAssociates(associates);

        log.info("Process Ends ---------------------------");

        return htmlBody;
    }

    @PostMapping("syncNewAssociates")
    public String syncNewAssociates(@RequestParam String thresholdDate){
        log.info("Process Begins ---------------------------");

        List<Associate> associates = teraData.getNewAssociates(thresholdDate);

        String htmlBody = syncAssociates(associates);

        log.info("Process Ends ---------------------------");

        return htmlBody;
    }


    private void createAssociate(Associate associate) {
        Entity entity = getEntity(usdEntities, associate.getEntity() + associate.getUa());

        try{

            if (!Objects.isNull(entity)){

                UsdCompany usdCompany = new UsdCompany(entity.getUsdId());
                usd.createAssociate(associate,usdCompany);
            }
        }catch (Exception ex){

            System.out.println("the missing entity is: " + associate.getEntity() + associate.getUa());
        }
    }

    private String syncAssociates(List<Associate> associates){
        String htmlBody = "<table>";

        int associateCreationCount = 0;

        usdEntities = usd.getEntities();
        log.info("Qtd Entities: " + usdEntities.size());
        log.info("Qtd Associates to sync: " + associates.size());
        Connection usdDBConnection = usd.createDBConnection();


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

        htmlBody = "Qtd Associates: " + associates.size() + "  Qtd created: " + associateCreationCount + "<br>" + htmlBody;

        return  htmlBody;

    }

}
