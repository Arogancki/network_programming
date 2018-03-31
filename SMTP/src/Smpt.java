import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;

import javax.xml.bind.DatatypeConverter;

public class Smpt{
	private int port;
    private String host;
    private String user;
    private String password;
    private String recipient;
    private Socket socket;
    
    private BufferedReader reader;
    private BufferedWriter writer;
    public Smpt(String hostName, String portNumber, 
    		String username, String _password, String _recipient) throws NumberFormatException{
    	port = Integer.parseInt(portNumber);
        host = hostName;
        user = username;
        password = _password;
        recipient = _recipient;
    }
    private void connect() throws IOException{
    	socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
    private String send(String data, boolean read) throws IOException{
        System.out.println("C: '" + data+"'");
    	writer.write(data + "\n");
        writer.flush();
        if (read)
        	return read();
        return "";
    }
    private String read() throws IOException{
    	String res = reader.readLine();
    	System.out.println("S: '"+res+"'");
        return res;
    }
    private String disconnect() throws IOException{
    	if (!(socket != null && socket.isConnected())){
        	throw new Error("Not connected");
        }
        socket.close();
        reader = null;
        writer = null;
    	return "";
    }
    private String encode(String str) throws UnsupportedEncodingException{
    	String e = DatatypeConverter.printBase64Binary(str.getBytes("UTF-8"));
    	System.out.println("E: '"+str+"' -> '"+e+"'");
    	return e;
    }
    private String decode(String str) throws UnsupportedEncodingException{
    	String d = new String(DatatypeConverter.parseBase64Binary(str), "UTF-8");
    	System.out.println("D: '"+str+"' -> '"+d+"'");
    	return d;
    }
    private String flow() throws IOException{
    	String init = read();
    	String h = send("HELO " + host, true);

    	String a1 = send("AUTH LOGIN", true);
    	String u = send(encode(user), true);
    	String p = send(encode(password), true);
    	
    	if (p.contains("Error")){
    		System.out.println("Aunthentication failed! ("+p+")");
    		return "";
    	}
    	
    	String mf = send("MAIL FROM:<"+ user +">", true);
    	String rt = send("RCPT TO:<" + recipient + ">", true);
    	String d = send("DATA", true);
    
    	String d1 = send("From: <" + user + ">", false);
    	String d2 = send("To: <" + recipient + ">", false);
    	String d3 = send("Subject: PS LAB LATO 2018", false);
    	String d4 = send("", false);
    	String d5 = send("Artur Ziemba", false);
    	String d6 = send(".", true);
    	
    	String q = send("QUIT", true);
        return "";   
    }
    public void send() throws IOException{
    	connect();
    	flow();
    	disconnect();
    } 
}