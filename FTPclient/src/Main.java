import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

// best help - http://www.java2s.com/Code/Java/Network-Protocol/Ftp.htm
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
        
        FTPclient fc = null;
        try {
        	fc = new FTPclient(config.get("server") , config.get("port"), 
        		config.get("username"), config.get("password"), config.get("path"));
        	fc.connect();
        	
        	Scanner scanner = new Scanner(System.in);
        	System.out.print("---------- FTPClient ----------\n" +
        			"manual - .help\n\n" +
        			"wd: "+fc.Get()+"\n");
            String userInput;
            do{
            	System.out.print("-------------------------------\n"+">");
            	userInput = scanner.next();
            	System.out.print("\n");
            	if (userInput.equalsIgnoreCase(".quit")){
            		break;
            	}
            	if (userInput.equalsIgnoreCase(".tree")){
            		System.out.print(fc.GetTree()+"\n");
            		userInput = fc.getCwd();
            	}
            	if (userInput.equalsIgnoreCase(".help")){
            		System.out.println("help:\n" +
            				"  .quit to exit \n" + 
            				"  .tree to print tree view \n" +
            				"  [path] to navigate \n");
            		userInput = fc.getCwd();
            	}
            	System.out.print("wd: "+fc.Get(userInput)+"\n");
            }while(true);
        }
        catch (NumberFormatException e) {
            System.out.println("App.config file error.");
            return;
        } catch (IOException e) {
			System.out.println("Connection error: "+e);
		}
        finally{
        	try {
        		fc.disconnect();
        	}
        	catch(Exception e){}
        }
        System.out.println("Bye!");
    }
}
