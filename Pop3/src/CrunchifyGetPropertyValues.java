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
            String pooling = prop.getProperty("poolingtime");

            if (server==null){
                throw new Exception("server");
            }

            if (username==null){
                throw new Exception("username");
            }

            if (password==null){
                throw new Exception("password");
            }

            if (port==null){
                port = "110";
            }

            if (pooling==null){
                pooling = "6";
            }

            map.put("server", server);
            map.put("username", username);
            map.put("password", password);
            map.put("port", port);
            map.put("pooling", pooling);


        fis.close();
        return map;
    }
}