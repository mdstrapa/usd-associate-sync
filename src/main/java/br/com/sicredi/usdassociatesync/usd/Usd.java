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
import br.com.sicredi.usdassociatesync.Log;
import br.com.sicredi.usdassociatesync.LogType;
import br.com.sicredi.usdassociatesync.teradata.Associate;

import com.google.gson.Gson;

public class Usd {

    private static HttpClient httpClient = HttpClient.newHttpClient();
    private Gson gson = new Gson();
    private Configuration config = new Configuration();
    private Log log = new Log();
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
                if (config.isDebugMode()) log.addLogLine(LogType.INFO, httpResponse.body());
                responseBody = usdJsonFormatter.formatObjectResponse(httpResponse.body(),"rest_access");
                System.out.println(responseBody);
                usdRestAccess = new Gson().fromJson(responseBody, UsdRestAccess.class);

                usdRestAccess.registerNewAccessKey(String.valueOf(usdRestAccess.access_key) , String.valueOf(usdRestAccess.expiration_date));
    
            } catch (IOException | InterruptedException e) {
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
                .setHeader("X-AccessKey",String.valueOf(accessKey))
                .build();
        }

        return usdRequest;
    }

    public Boolean createAssociate(UsdAssociate associate){
        Boolean result = true;
        UsdRestAccess restAccess = getAccessKey();

        String requestBody = gson.toJson(associate);

        HttpRequest request = buildUsdRequest("POST","ca_contact/", requestBody, restAccess.access_key);

        System.out.println(requestBody);


        // try {            
            
        //     HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());

        //     if (config.isDebugMode()) log.addLogLine(LogType.INFO, httpResponse.body());

        //     if (httpResponse.statusCode()==2021) result = true;



        // } catch (IOException | InterruptedException e) {
        //     System.out.println("An error has occurred: " + e.getMessage());
        //     e.printStackTrace();
        // }
        return result;
    }
    
    private Connection createDBConnection(){

        Connection usdDBConnection = null;
        try{

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  

            String usdDBServer = config.getProperty("usdDBserver");
            String usdDBUser = config.getProperty("usdDBUser");
            String usdDBPassword = config.getProperty("usdDBPassword");
    
            String connectionUrl =
                        "jdbc:sqlserver://" + usdDBServer + ":1433;"
                        + "database=mdb;"
                        + "user=" + usdDBUser + ";"
                        + "password=" + usdDBPassword + ";"
                        + "loginTimeout=30;";
    
            usdDBConnection=DriverManager.getConnection(connectionUrl); 
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return usdDBConnection;

    }

    private String buildAssociateKey(String codCooperativa, String contaCorrente, String cpf){
        return codCooperativa + "0000" + contaCorrente + cpf + "1";
    }

    public Boolean checkIfAssociateExists(Associate associate){
        Boolean result = false;

        String codCooperativa = associate.getEntity();
        String contaCorrente = associate.getAccount();
        String cpf = associate.getCpfCnpj();

        String associateKey = buildAssociateKey(codCooperativa,contaCorrente,cpf);

        try{
            Connection usdDBConnection = createDBConnection();

            Statement sqlQuery = usdDBConnection.createStatement();  
    
            ResultSet rs = sqlQuery.executeQuery("select top 1 last_name from ca_contact where contact_type = 2310 and pager_email_address = '" + associateKey + "'");
            
            if (rs.next()) result = true;

            usdDBConnection.close();  

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public List<Entity> getEntities(){
        List<Entity> entities = new ArrayList<>();
        try{
            Connection usdDBConnection = createDBConnection();

            Statement sqlQuery = usdDBConnection.createStatement();  
    
            ResultSet rs = sqlQuery.executeQuery("select company_uuid, company_name from ca_company where inactive = 0 and company_type = 1000048 order by company_name");
            
            while(rs.next()) entities.add(new Entity(rs.getString(1),rs.getString(2)));

            usdDBConnection.close();  

        }catch (Exception e){
            e.printStackTrace();
        }

        return entities;
    }

}
