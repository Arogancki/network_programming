import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * @author Crunchify.com
 *
 */

public class CrunchifyGetPropertyValues {
    static public Map getPropValues(String file) throws IOException, Exception {
        Map<String,String> map=new HashMap<String,String>();
        String propFileName = file;
        FileInputStream fis =  new FileInputStream(file);

            Properties prop = new Properties();
            prop.load(fis);
            String server = prop.getProperty("server");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String port = prop.getProperty("port");
            String recipient = prop.getProperty("recipient");

            if (server==null){
                throw new Exception("server");
            }

            if (username==null){
                throw new Exception("username");
            }

            if (password==null){
                throw new Exception("password");
            }

            if (recipient==null){
            	throw new Exception("recipient");
            }

            map.put("server", server);
            map.put("username", username);
            map.put("password", password);
            map.put("port", port);
            map.put("recipient", recipient);


        fis.close();
        return map;
    }
}