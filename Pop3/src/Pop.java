import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;

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
    private List<String> headers;
    private Thread bgWorker; 
    public Pop(String hostName, String portNumber, 
    		String username, String _password,
    		String _poolingtime) throws NumberFormatException{
    	port = Integer.parseInt(portNumber);
    	poolingtime = Integer.parseInt(_poolingtime);
        host = hostName;
        user = username;
        password = _password;
        headers = new ArrayList<String>();
        bgWorker = new Thread(this);
        
        File f = new File(user);
		Scanner s;
		try {
			s = new Scanner(f);
			while (s.hasNext()){
        	    headers.add(s.next());
        	}
			s.close();
		} catch (FileNotFoundException e1) {
		}
    }
    private void connect() throws IOException{
    	socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String wel = read();
        String log = login();
        //System.out.println("Connecting to the " + host + ":"+ port + " " + wel + " " + log);
    }
    private void disconnect() throws IOException{
        if (!(socket != null && socket.isConnected())){
        	throw new Error("Not connected");
        }
    	String res = logout();
        socket.close();
        reader = null;
        writer = null;
        //System.out.println("Disconnected from the host " + res);
    }
    private String send(String data) throws IOException{
        //System.out.println("Sending " + data);
    	writer.write(data + "\n");
        writer.flush();
        return read();
    }
    private String read() throws IOException{
        String response = reader.readLine();
        if (response.startsWith("-ERR")){
            throw new Error("Server sent error "+response);
        }
        return response;
    }
    private String login() throws IOException{
        return send("USER " + user) + send("PASS " + password);   
    }
    private String logout() throws IOException{
        return send("QUIT");
    }
    private int getNumberOfNewMessages() throws NumberFormatException, IOException{
        return Integer.parseInt(send("STAT").split(" ")[1]);
    }
    private void getNewMessages() throws NumberFormatException, IOException{
		String message = "";
    	for (int i=1; i<=getNumberOfNewMessages(); i++){
    		send("RETR " + i);
        	while(!(message=read()).equals(".")){
        		if (message.startsWith("Subject: ")){
        			message = message.substring(9);
        			// unique by subject
        			if (!headers.contains(message)){
        				headers.add(message);
        				System.out.println(message);
        			}
        			// unique by subject and message
        			/*
        			if (!headers.contains(i+message)){
        				headers.add(i+message);
        				System.out.println(message);
        			}
        			*/
        		}
        		if (!isWorking){
        			break;
        		}
        	}
        	if (!isWorking){
    			break;
    		}
    	}
    }
    public void stop(){
    	isWorking = false;
    	bgWorker.interrupt();
    	try {
			bgWorker.join();
		} catch (InterruptedException e) {
		}
    }
    public void start(){
    	bgWorker.start();
    }
    public void run() {
    	isWorking = true;
    	System.out.println("Pop3 client\nchecking for new messages...");
        try {
        	while (isWorking){
        		try {
					connect();
					getNewMessages();
					disconnect();
				} catch (IOException e) {
					System.out.println("Connection error.");
				}
            	Thread.sleep(poolingtime*1000);
            }
        } catch(InterruptedException v) {
        	System.out.println("Bye!");
        }
        // save headers
        BufferedWriter fileWriter =null;
        try {
        	fileWriter = new BufferedWriter( new FileWriter(user));
        	for (String header:headers){
        		fileWriter.write(header+"\n");
        	}
        	fileWriter.close();
		} catch (IOException e) {
			try {
				fileWriter.close();
			} catch (IOException e1) {}
			System.out.println("Coudn't read headers from "+user+" file.");
		}
    }
}