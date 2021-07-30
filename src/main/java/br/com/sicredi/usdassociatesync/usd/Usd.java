package br.com.sicredi.usdassociatesync.usd;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.sql.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import br.com.sicredi.usdassociatesync.Configuration;
import br.com.sicredi.usdassociatesync.teradata.Associate;
import lombok.extern.slf4j.Slf4j;

import com.google.gson.Gson;

@Slf4j
public class Usd {

    private static HttpClient httpClient = HttpClient.newHttpClient();
    private Configuration config = new Configuration();
    private UsdJsonFormatter usdJsonFormatter = new UsdJsonFormatter();

    private UsdRestAccess getAccessKey(){
        UsdRestAccess usdRestAccess = new UsdRestAccess();

        if (usdRestAccess.isAccessKeyStillValid()) usdRestAccess.loadAccessKeyFromFile();
        else{
            String responseBody;
            String requestBody = "{ \"rest_access\" : {} }";
    
            HttpRequest request = buildUsdRequest("POST","rest_access",requestBody,0);
    
            try {
                HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
                if (log.isDebugEnabled()) log.info(httpResponse.body());
                responseBody = usdJsonFormatter.formatObjectResponse(httpResponse.body(),"rest_access");

                usdRestAccess = new Gson().fromJson(responseBody, UsdRestAccess.class);

                usdRestAccess.registerNewAccessKey(String.valueOf(usdRestAccess.access_key) , String.valueOf(usdRestAccess.expiration_date));
    
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }

        return usdRestAccess;
    }

    private HttpRequest buildUsdRequest(String method, String usdObject, String requestBody, int accessKey){

        Configuration configuration = new Configuration();

        URI usdEndPoint = URI.create(configuration.getProperty("usdEndPoint")+ usdObject);
        
        HttpRequest usdRequest = null;

        if (usdObject == "rest_access"){
            usdRequest = HttpRequest.newBuilder()
                .uri(usdEndPoint)
                .method(method, BodyPublishers.ofString(requestBody))
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .setHeader("Authorization", "Basic bWFyY29zX3N0cmFwYXpvbjpFVUEyMDIxZWhub2lz")
                .build();
        }else if (method == "POST") {
            usdRequest = HttpRequest.newBuilder()
                .uri(usdEndPoint)
                .method(method, BodyPublishers.ofString(requestBody))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-AccessKey","1784302696")
                .build();

                //.setHeader("X-AccessKey",String.valueOf(accessKey))
                //.setHeader("Authorization", "Basic bWFyY29zX3N0cmFwYXpvbjpFVUEyMDIxZWhub2lz")
                //.setHeader("X-AccessKey","435608289") --PROD
        }

        return usdRequest;
    }

    public Boolean createAssociate(Associate associate, UsdCompany company){
        Boolean result = true;
        UsdRestAccess restAccess = getAccessKey();
        
        String associateKey = buildAssociateKey(associate.getEntity(),associate.getAccount(),associate.getCpfCnpj());

        UsdAssociate  usdAssociate = new UsdAssociate(
                            associate.getFullName(), 
                            associate.getBorndate(), 
                            associate.getCpfCnpj(), 
                            associate.getAccount(), 
                            company
                );

        usdAssociate.setAssociateKey(associateKey);

        String requestBody = usdJsonFormatter.formatRequestBodyForCreation(usdAssociate, "cnt");

        if (log.isDebugEnabled()) log.info(requestBody);

        HttpRequest request = buildUsdRequest("POST","cnt", requestBody, restAccess.access_key);

        try {            
            
            HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());

            //if (config.isDebugMode()) log.addLogLine(LogType.INFO, httpResponse.body());
            log.info(httpResponse.body());

            if (httpResponse.statusCode()==201) result = true;

        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    public Connection createDBConnection(){

        Connection usdDBConnection = null;
        try{

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  

            String usdDBServer = config.getProperty("usdDBServer");
            String usdDBUser = config.getProperty("usdDBUser");
            String usdDBPassword = config.getProperty("usdDBPassword");
    
            String connectionUrl =
                        "jdbc:sqlserver://" + usdDBServer + ":1433;"
                        + "database=mdb;"
                        + "user=" + usdDBUser + ";"
                        + "password=" + usdDBPassword + ";"
                        + "loginTimeout=30;";

            //System.out.println(connectionUrl);
    
            usdDBConnection=DriverManager.getConnection(connectionUrl); 
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return usdDBConnection;

    }

    private String buildAssociateKey(String codCooperativa, String contaCorrente, String cpf){
        String codCooperativaConvertido = "";
        if (codCooperativa.substring(0, 1).equals("0")) codCooperativaConvertido = codCooperativa.substring(1);
        else codCooperativaConvertido = codCooperativa;
        return codCooperativaConvertido + "0000" + contaCorrente.replace("-", "") + cpf + "1";
    }

    public Boolean checkIfAssociateExists(Associate associate, Connection usdDBConnection){
        Boolean result = true;

        String associateKey = buildAssociateKey(associate.getEntity(),associate.getAccount(),associate.getCpfCnpj());

        
        try{
            //Connection usdDBConnection = createDBConnection();
            
            Statement sqlQuery = usdDBConnection.createStatement();  
            
            ResultSet rs = sqlQuery.executeQuery("select top 1 last_name from ca_contact where contact_type = 2310 and inactive = 0 and pager_email_address = '" + associateKey + "'");
            
            if (!rs.next()) result = false;
            
            //usdDBConnection.close();  
            
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
        

        if (log.isDebugEnabled()) {
            String action = "";

            if (result) action = "Exist - WILL NOT be created";
            else action = "Does NOT Exist - WILL BE created";

            log.info(("The key for " + associate.getFullName() + " is: " + associateKey + " | " + action));
        }

        return result;
    }

    public List<Entity> getEntities(){
        List<Entity> entities = new ArrayList<>();
        try{
            Connection usdDBConnection = createDBConnection();

            Statement sqlQuery = usdDBConnection.createStatement();  
    
            ResultSet rs = sqlQuery.executeQuery("select company_uuid, company_name from ca_company where inactive = 0 and company_type = 1000058 order by company_name");
            
            while(rs.next()) entities.add(new Entity(rs.getString(1),rs.getString(2)));

            usdDBConnection.close();  

        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return entities;
    }

}
