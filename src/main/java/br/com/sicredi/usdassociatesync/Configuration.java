package br.com.sicredi.usdassociatesync;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

public class Configuration {
    
    InputStream inputStream;
    String propFileName = "application.properties";

    public String getProperty(String property){
        String result = "";
 
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            Properties prop = new Properties();
            prop.load(inputStream);
            result = prop.getProperty(property);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public boolean isDebugMode(){
        boolean result = false;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            Properties prop = new Properties();
            prop.load(inputStream);
            if (prop.getProperty("debugMode").equals("1")) result = true; 
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }



}
