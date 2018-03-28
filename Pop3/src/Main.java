import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

//http://www.faqs.org/rfcs/rfc1939.html
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
        
        Pop pop;
        try {
        	pop = new Pop(config.get("server") , config.get("port"), 
        		config.get("username"), config.get("password"), config.get("pooling"));
        }
        catch (NumberFormatException e) {
            System.out.println("App.config file error.");
            return;
        }
        pop.start();
        Scanner scanner = new Scanner(System.in);
        char c;
        do{
        	c = scanner.next().charAt(0);
        }while(c!='q');
        pop.stop();  
    }
}
