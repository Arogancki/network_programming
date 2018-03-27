import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Pop implements Runnable {
	private int port;
	private int poolingtime;
    private String host;
    private String user;
    private String password;
    private Socket socket;
    private boolean isWorking;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int lastMessageIndex;
    private List<String> headers = new ArrayList<String>();
    public Pop(String hostName, String portNumber, 
    		String username, String _password,
    		String _poolingtime){
    	port = Integer.parseInt(portNumber);
    	poolingtime = Integer.parseInt(_poolingtime);
        host = hostName;
        user = username;
        password = _password;
    }
    public void connect(){
        isWorking = true;
    	lastMessageIndex = 1;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connecting to the " + host + ":"+ port + " " + read() + " " + login());
        } catch (IOException e) {
        	 System.out.println("Error during connection");
        }
    }
    public void disconnect(){
    	isWorking = false;
    	headers.clear();
        if (!(socket != null && socket.isConnected())){
        	System.out.println("Not connected");
        }
    	String res = logout();
        try {
            socket.close();
        } catch (IOException e) {
        }
        reader = null;
        writer = null;
        System.out.println("Disconnected from the host " + res);
    }
    private String send(String data){
        //System.out.println("Sending " + data);
        try {
            writer.write(data + "\n");
            writer.flush();
            return read();
        } catch (IOException e) {
            System.out.println("Sending error");
            return "";
        }
    }
    /*
    private String read(int size){
    	int count=0;
    	char[] buffer = new char[size];
        try {
        	int c;
        	while ((c = reader.read()) != -1 && count < size){
        		buffer[count++]=(char)c;
        	}      
        } catch (IOException e) {
            System.out.println("Cannot read");
        }
        String response = new String(buffer);
        if (response.startsWith("-ERR")){
            System.out.println("Server sent error "+response);
        }
        return response;
    }
    */
    private String read(){
        String response = "";
        try {
            response = reader.readLine();            
        } catch (IOException e) {
            System.out.println("Cannot readline");
            return "";
        }
        if (response.startsWith("-ERR")){
            System.out.println("Server sent error "+response);
        }
        return response;
    }
    private String login(){
        return send("USER " + user) + send("PASS " + password);   
    }
    private String logout(){
        return send("QUIT");
    }
    private int getNumberOfNewMessages(){
        String response = send("STAT");
        String[] values = response.split(" ");
        return Integer.parseInt(values[1]);
    }
    private void getNewMessages(){
    	int size = getNumberOfNewMessages();
		String message = "";
    	for (; lastMessageIndex<=size; lastMessageIndex++){
    		send("RETR " + lastMessageIndex);
        	while(!(message=read()).equals(".")){
        		if (message.startsWith("Subject: ")){
        			message = message.substring(9);
        			if (!headers.contains(message)){
        				headers.add(message);
        				System.out.println(message);
        			}
        		}
        	}
    	}
    }
    public void run() {
        try {
        	while (isWorking){
        		getNewMessages();
            	Thread.sleep(poolingtime*1000);
            }
        } catch(InterruptedException v) {}
    }
}