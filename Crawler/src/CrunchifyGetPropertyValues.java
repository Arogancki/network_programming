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
            String port = prop.getProperty("port");
            String _file = prop.getProperty("file");
            String depth = prop.getProperty("depth");

            if (server==null){
                throw new Exception("server");
            }

            if (port==null){
                throw new Exception("port");
            }

            if (_file==null){
                throw new Exception("file");
            }

            if (depth==null){
            	depth="1";
            }

            map.put("server", server);
            map.put("file", _file);
            map.put("depth", depth);
            map.put("port", port);


        fis.close();
        return map;
    }
}