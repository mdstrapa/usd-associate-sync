package br.com.sicredi.usdassociatesync;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.Date;


public class Log {

    //private Log log = new Log();

    public void createLogFile(){
        try{
            File newLogFile = new File("src/main/resources/operation.log");
            if (newLogFile.createNewFile()){
                System.out.println("The file was created");
            }else{
                System.out.println("The file already exists");
            }

        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public String readLogFile(){

        String logLines = "";
        InputStream inputStream;
        String logFileName = "operation.log";
        inputStream = getClass().getClassLoader().getResourceAsStream(logFileName);
        
        Scanner fileReader = new Scanner(inputStream);
        while (fileReader.hasNextLine()){
            String logLine = fileReader.nextLine();
            logLines = logLines + "<br>" + logLine;
        }
        fileReader.close();

        return logLines;
    }

    public void addLogLine(LogType type, String message){

        try {
            FileHandler handler = new FileHandler("src/main/resources/operation.log",true);
    
            Logger log = Logger.getLogger("br.com.sicredi");
    
            handler.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
      
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage()
                    );
                }
            });
    
            log.addHandler(handler);

            switch (type){
                case INFO:
                    log.info(message);
                    break;
                case ERROR:
                    log.severe(message);
                    break;
                case WARNING:
                    log.warning(message);
                    break;
            }

            handler.close();
            
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    
}
