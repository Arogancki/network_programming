import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;

import javax.xml.bind.DatatypeConverter;

public class FTPclient{
	private boolean debug = false;
	
	private int port;
    private String host;
    private String user;
    private String password;
    private String cwd;
    private Socket controlSocket;
    private BufferedReader controlReader;
    private BufferedWriter controlWriter;
    private Socket dataSocket;
    private BufferedReader dataReader;
    private BufferedWriter dataWriter;
    
    public FTPclient(String hostName, String portNumber, 
    		String username, String _password, String _path) throws NumberFormatException{
    	port = Integer.parseInt(portNumber);
        host = hostName;
        user = username;
        password = _password;
        cwd= _path;
    }
    public void connect() throws IOException{
    	controlSocket = new Socket();
        controlSocket.connect(new InetSocketAddress(host, port));
        controlReader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        controlWriter = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));
        
        String response = readControl();
        if (!response.startsWith("220 ")) {
          throw new IOException("unknown response when connecting to the FTP server: "+ response);
        }

        response = sendControl("USER " + user, true);
        if (!response.startsWith("331 ")) {
          throw new IOException("Unable to log in " + response);
        }
        
        response = sendControl("PASS " + password, true);
        if (!response.startsWith("230 ")) {
          throw new IOException("Unable to log in "+ response);
        }
    }
    private String sendControl(String data, boolean read) throws IOException{
    	if (debug) System.out.println("C: '" + data+"'");
    	controlWriter.write(data + "\r\n");
        controlWriter.flush();
        if (read)
        	return readControl();
        return "";
    }
    private String sendData(String data, boolean read) throws IOException{
    	if (debug) System.out.println("C: '" + data+"'");
    	dataWriter.write(data + "\r\n");
        dataWriter.flush();
        if (read)
        	return readControl();
        return "";
    }
    private String readControl() throws IOException{
    	String res = controlReader.readLine();
    	if (debug) System.out.println("S: '"+res+"'");
        return res;
    }
    private String readData() throws IOException{
    	String res = dataReader.readLine();
    	if (debug) System.out.println("S: '"+res+"'");
        return res;
    }
    public String disconnect() throws IOException{
    	sendControl("QUIT", false);
    	if (!(controlSocket != null && controlSocket.isConnected())){
        	throw new Error("Not connected");
        }
        controlSocket.close();
        controlReader = null;
        controlWriter = null;
        
        if (!(dataSocket != null && dataSocket.isConnected())){
        	throw new Error("Not connected");
        }
        dataSocket.close();
        dataReader = null;
        dataWriter = null;
    	return "";
    }
    public String Get() throws IOException{
    	return Get(cwd);
    }
    public String Get(String path) throws IOException{
    	if (!cwd(path)){
    		return "Coudn't cd to "+path+"\n\n" + Get(cwd);
    	}
    	cwd = pwd();
    	StringBuilder sb = new StringBuilder();
    	for (String s : GetList(path)){
    	    sb.append(s+"\n");
    	}
    	return cwd + "\n" + sb.toString();
    } 
    private boolean cwd(String dir) throws IOException {
        String response = sendControl("CWD " + dir, true);
        return (response.startsWith("250 "));
    }
    public synchronized String pwd() throws IOException {
        String dir = cwd;
        String response = sendControl("PWD", true);
        if (response.startsWith("257 ")) {
          int firstQuote = response.indexOf('\"');
          int secondQuote = response.indexOf('\"', firstQuote + 1);
          if (secondQuote > 0) {
            dir = response.substring(firstQuote + 1, secondQuote);
          }
        }
        return dir;
      }
    public String GetTree() throws IOException{
    	return GetSubTree(cwd, 0);
    }
    private String GetSubTree(String path, int dashes) throws IOException{
    	StringBuilder sb = new StringBuilder();
    	if (dashes==0)
    		sb.append("|"+path + "\n");
    	else
    		sb.append(path.substring(path.lastIndexOf("/")+1) + "\n");
    	for (String s : GetList(path, true)){
    		for (int i=0; i<dashes+1; i++){
    			sb.append("-");
    		}
    	    sb.append("|"+GetSubTree(path+"/"+s, dashes+1));
    	}
    	return sb.toString();
    }
    private List<String> GetList(String dir) throws IOException{
    	List<String> files = new ArrayList<String>();
    	
    	String response = sendControl("PASV", true);
        if (!response.startsWith("227 ")) {
          throw new IOException("Cannot set pasive mode");
        }
        
        try {
        	String[] addressS = response.substring(response.lastIndexOf("(")+1, response.lastIndexOf(")")).split(",");
        	int[] address = new int[addressS.length];
        	int i=0;
        	for (String str : addressS)
        	    address[i++] = Integer.parseInt(str);
        	if (dataSocket!=null && !dataSocket.isClosed())
        		dataSocket.close();
        	dataSocket = new Socket();
        	String host2 = address[0]+"."+address[1]+"."+address[2]+"."+address[3];
        	int port2 = (address[4]*256)+address[5];
            dataSocket.connect(new InetSocketAddress(host2, port2));
            dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
        }catch(Exception e ){
        	throw new IOException("Cannot set second socket.");
        }
    	
    	sendControl("LIST", true);
    	do {
    		String file = readData();
    		if (file==null || file.equalsIgnoreCase("null")){
    			break;
    		}
    		file = file.trim().replaceAll(" +", " ");
    		file = file.split(" ")[3];
    		files.add(file);
    	}while(true);
    	readControl();
    	return files;
    }
    private List<String> GetList(String dir, boolean checkAndGoback) throws IOException{
    	String temp = pwd();
    	List<String> list = new ArrayList<String>();
    	if (cwd(dir)){
    		list = GetList(dir);
        	cwd(temp);
    	}
    	return list;
    }
    public String getCwd(){
    	return cwd;
    }
}