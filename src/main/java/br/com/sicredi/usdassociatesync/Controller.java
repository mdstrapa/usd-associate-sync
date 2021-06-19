package br.com.sicredi.usdassociatesync;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sicredi.usdassociatesync.usd.Usd;
import br.com.sicredi.usdassociatesync.usd.UsdAssociate;
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

        Teradata teradata = new Teradata();
        List<Associate> associatesToCreate = teradata.getNewAssociates(thresholdDate);

        for (Associate associate : associatesToCreate) {
            UsdAssociate usdAssociate = createUsdAssociate(associate);
            usdAssociate.save();
        }
    
        return htmlBody;
    }

    private UsdAssociate createUsdAssociate(Associate associateToCreate){
        
        Entity entity = getEntity(usdEntities, associateToCreate.getEntity());

        UsdAssociate usdAssociate = new UsdAssociate(associateToCreate.getFullName(), 
                    associateToCreate.getBorndate(), 
                    associateToCreate.getCpfCnpj(), 
                    associateToCreate.getAccount(), 
                    entity);
        return usdAssociate;
    }

    private Entity getEntity(List<Entity> entities, String entityCode){
        Entity entity = null;

        return entity;
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


}
