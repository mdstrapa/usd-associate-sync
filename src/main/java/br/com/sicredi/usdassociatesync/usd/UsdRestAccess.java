package br.com.sicredi.usdassociatesync.usd;

import br.com.sicredi.usdassociatesync.Configuration;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class UsdRestAccess {
    int access_key;
    int expiration_date;


    Configuration config = new Configuration();

    String accessKeyFileLocation = config.getProperty("usdAccessKeyFile");

    public boolean isAccessKeyStillValid(){
        String accessKeyFileValues[];
        int expirationDate = 0;
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        try{
            File accessKeyFile = new File(accessKeyFileLocation);
            Scanner fileReader = new Scanner(accessKeyFile);
            while (fileReader.hasNextLine()){
                String fileLine = fileReader.nextLine();
                accessKeyFileValues = fileLine.split(";");

                expirationDate = Integer.parseInt(accessKeyFileValues[1]);
            }
            fileReader.close();

        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        return (expirationDate > (currentTimestamp.getTime() / 1000));
    }

    public void loadAccessKeyFromFile(){
        String accessKeyFileValues[];

        try{
            File accessKeyFile = new File(accessKeyFileLocation);
            Scanner fileReader = new Scanner(accessKeyFile);
            while (fileReader.hasNextLine()){
                String fileLine = fileReader.nextLine();
                accessKeyFileValues = fileLine.split(";");
                this.access_key = Integer.parseInt(accessKeyFileValues[0]);
            }
            fileReader.close();

        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public void registerNewAccessKey(String accessKey, String expirationDate){
        
        try (FileWriter accessKeyFile = new FileWriter(accessKeyFileLocation) ){
            accessKeyFile.append(accessKey + ";" + expirationDate);
            accessKeyFile.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
