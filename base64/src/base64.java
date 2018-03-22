import java.io.*;

// do obliczenia uzylem
// https://en.wikipedia.org/wiki/Base64

public class base64 {
    static String[] codeArray = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
            "Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f",
            "g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
            "w","x","y","z","0","1","2","3","4","5","6","7","8","9","+","/" };
    static private int indexOf(String i){
        if (i.equalsIgnoreCase("=")){
            return -2;
        }
        for (int j = 0; j < codeArray.length; j++) {
            if (codeArray[j].equals(i))
                return j;
        }
        return -1;
    }
    static private int indexOf(int i){
        return indexOf(""+((char)i));
    }
    static private String intToBits(int x, int base){
        if (x == -1){
            return "-1";
        }
        String res =  Integer.toString(x,2);
        while (res.length() < base){
            res = "0" + res;
        }
        return res;
    }
    static void decode(String path) throws IOException {
        InputStream is;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            System.out.print("File not found "+path);
            return;
        }
        BufferedWriter writer = null;
        boolean i3Pad = false;
        boolean i43Pad = false;
        try {
            writer = new BufferedWriter( new FileWriter(path + " decoded"));
            while (is.available() > 0) {
                int i1 = indexOf(is.read());
                int i2 = indexOf(is.read());
                int i3 = indexOf(is.read());
                int i4 = indexOf(is.read());
                if (i1==-1 || i2==-1 || i3==-1 || i4==-1){
                    throw new Error("file error");
                }
                if (i3==-2){
                    i3Pad = true;
                }
                if (i4==-2){
                    i43Pad = true;
                }
                // get 6bits long values
                String b61 = intToBits(i1, 6);
                String b62 = intToBits(i2, 6);
                String b63 = i3Pad?"00000000":intToBits(i3, 6);
                String b64 = i43Pad?"00000000":intToBits(i4, 6);
                String joined = b61 + b62 + b63 + b64;
                String o1 = ""+((char)Integer.parseInt(joined.substring(0, 8), 2));
                String o2 = ""+((char)Integer.parseInt(joined.substring(8, 16), 2));
                String o3 = ""+((char)Integer.parseInt(joined.substring(16, 24), 2));
                String result = o1;
                if (o2!=null)
                    result += o2;
                if (o3!=null)
                    result += o3;
                writer.write(result);
            }
        }catch ( IOException e)
        {
            System.out.print(e);
        }
        catch(Exception e){
            System.out.print("The file is not coded correctly.");
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
            }
        }
        is.close();
    }
    static void code(String path) throws IOException {
        InputStream is;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            System.out.print("File not found "+path);
            return;
        }
        boolean b2Pad = false;
        boolean b3Pad = false;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter( new FileWriter(path+".b64"));
            while (is.available() > 0) {
                String b1 = intToBits(is.read(), 8);
                String b2 = intToBits(is.read(), 8);
                String b3 = intToBits(is.read(), 8);
                if (b2.equalsIgnoreCase("-1")) {
                    b2 = "00000000";
                    b2Pad = true;
                }
                if (b3.equalsIgnoreCase("-1")) {
                    b3 = "00000000";
                    b3Pad = true;
                }
                String joined = b1 + b2 + b3;
                int bsf1 = Integer.parseInt("00" + joined.substring(0, 6), 2);
                int bsf2 = Integer.parseInt("00" + joined.substring(6, 12), 2);
                int bsf3 = Integer.parseInt("00" + joined.substring(12, 18), 2);
                int bsf4 = Integer.parseInt("00" + joined.substring(18, 24), 2);
                String result = codeArray[bsf1] + codeArray[bsf2] + (b2Pad ? '=' : codeArray[bsf3]) + (b3Pad ? '=' : codeArray[bsf4]);
                writer.write(result);
            }
        }catch ( IOException e)
        {
            System.out.print(e);
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
            }
        }
        is.close();
    }
}
