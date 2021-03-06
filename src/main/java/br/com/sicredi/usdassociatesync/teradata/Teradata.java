package br.com.sicredi.usdassociatesync.teradata;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import br.com.sicredi.usdassociatesync.Configuration;
import java.sql.*;

@Slf4j
public class Teradata {

   Configuration config = new Configuration();

   private Connection createDBConnection(){

      Connection teraDataConnection = null;
      try{

         Class.forName("com.teradata.jdbc.TeraDriver");
         
         String teraDataServer = config.getProperty("teraDataServer");
         String teraDataUser = config.getProperty("teraDataUser");
         String teraDataPassword = config.getProperty("teraDataPassword");
  
         String connectionUrl = "jdbc:teradata://" + teraDataServer + "/DATABASE=DBC,TMODE=TERA, LOGMECH=LDAP, CHARSET=UTF16";

         teraDataConnection = DriverManager.getConnection(connectionUrl, teraDataUser, teraDataPassword); 

      }catch (Exception ex){
          ex.printStackTrace();
      }

      return teraDataConnection;
  } 

     public List<Associate> getNewAssociates(String thresholdDate){
        List<Associate> associates = new ArrayList<>();

        try{
         Connection teraDataConnection = createDBConnection();
   
         Statement sqlQuery = teraDataConnection.createStatement();  
   
         ResultSet rs = sqlQuery.executeQuery("select e.COOP  ,e.UA  ,f.CONTA_PRINCIPAL  ,f.CPF_CNPJ  ,p.DES_PESSOA ,p.DAT_NASCIMENTO " +
                                             "from P_SDS_DW_OWNER_V.VW_DW_F_ASSOCIADO_DIA f " +
                                             "join P_SDS_DW_OWNER_V.VW_PESSOA p on p.NUM_CPF_CNPJ = f.CPF_CNPJ " +
                                             "inner join P_SDS_DW_OWNER_V.VW_tempo t on f.OID_TEMPO_MOVIMENTO=t.OID_TEMPO " +
                                             "inner join P_SDS_DW_OWNER_V.VW_VW_ENTIDADE_FLAT e on f.OID_ENTIDADE=e.oid_entidade " +
                                             "where  t.dat_data='" + thresholdDate + " 00:00:00' " +                                             
                                             "and p.DAT_ASSOCIACAO = '" + thresholdDate + " 00:00:00'  " + 
                                             "and f.FLG_ASSOCIADO = 'S' " +
                                             "and f.FLG_FCCORRENT = 'S' " +
                                             "and p.FLG_CORRENTE = 'S'"
                                             );
         
         while(rs.next()) associates.add(new Associate(
                                          rs.getString(5),
                                          rs.getString(6),
                                          rs.getString(4),
                                          rs.getString(3),
                                          rs.getString(1),
                                          rs.getString(2))
                                          );
   
         teraDataConnection.close();  

      }catch(Exception e){
            log.error(e.getMessage());
         e.printStackTrace();
      }

        return associates;
     }


     public List<Associate> getCooperativeAssociates(String coopCod){
      List<Associate> associates = new ArrayList<>();


      try{
         Connection teraDataConnection = createDBConnection();
   
         Statement sqlQuery = teraDataConnection.createStatement();  
   
         ResultSet rs = sqlQuery.executeQuery("select e.COOP  ,e.UA  ,f.CONTA_PRINCIPAL  ,f.CPF_CNPJ  ,p.DES_PESSOA ,p.DAT_NASCIMENTO " +
                                             "from P_SDS_DW_OWNER_V.VW_DW_F_ASSOCIADO_DIA f " +
                                             "join P_SDS_DW_OWNER_V.VW_PESSOA p on p.NUM_CPF_CNPJ = f.CPF_CNPJ " +
                                             "inner join P_SDS_DW_OWNER_V.VW_tempo t on f.OID_TEMPO_MOVIMENTO=t.OID_TEMPO " +
                                             "inner join P_SDS_DW_OWNER_V.VW_VW_ENTIDADE_FLAT e on f.OID_ENTIDADE=e.oid_entidade " +
                                             "where  t.dat_data='2021-07-07 00:00:00' " +
                                             "and f.COD_AG='" + coopCod + "' " +
                                             //"and p.DAT_ASSOCIACAO = '2019-08-22 00:00:00' and f.COD_AG='AG0116' " + //test
                                             //"and f.CPF_CNPJ = '64089835020' " + // teste
                                             "and f.FLG_ASSOCIADO = 'S' " +
                                             "and f.FLG_FCCORRENT = 'S' " +
                                             "and p.FLG_CORRENTE = 'S'"
                                             );
         
         while(rs.next()) associates.add(new Associate(
                                          rs.getString(5),
                                          rs.getString(6),
                                          rs.getString(4),
                                          rs.getString(3),
                                          rs.getString(1),
                                          rs.getString(2))
                                          );
   
         teraDataConnection.close();  

      }catch(Exception e){
          log.error(e.getMessage());
         e.printStackTrace();
      }
   
      return associates;

     }
    
}
