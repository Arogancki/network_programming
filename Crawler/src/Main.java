import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

//http://www.samlogic.net/articles/smtp-commands-reference.htm
public class Main {
    static String configPath = "./App.config";
    public static void main(String[] args) {
        Map<String,String> config;
        try {
            config=CrunchifyGetPropertyValues.getPropValues(configPath);
        } catch (IOException e) {
            System.out.println("Unable to find config file");
            return;
        }
        catch (Exception e) {
            System.out.println("Unable to read " + e.getMessage() + " from config file.");
            return;
        }
        //System.out.println(config.keySet().toString());
        
        Crawler crawler;
        try {
        	crawler = new Crawler(config.get("server") , config.get("port"), 
        		config.get("file"), config.get("depth"));
        	crawler.generate();
        }
        catch (NumberFormatException e) {
            System.out.println("App.config file error.");
            return;
        } 
        catch (Exception e) {
			System.out.println("Error: "+e);
		}
         
    }
}
