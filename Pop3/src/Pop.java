import java.io.*;
import java.net.*;

public class Pop {
    int port;
    String host;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    public Pop(String hostName, String portNumber){
        port = Integer.parseInt(portNumber);
        host = hostName;
    }
    public void connect(){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connected to the host");
            //System.out.println(read());
        } catch (IOException e) {
            System.out.println("Error during connection");
        }
    }
    public void disconnect(){
        if (!(socket != null && socket.isConnected())){
            System.out.println("Not connected");
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
        }
        reader = null;
        writer = null;
        System.out.println("Disconnected from the host");
    }
    private String send(String data){
        System.out.println("Sending " + data);
        try {
            writer.write(data + "\n");
            writer.flush();
            return read();
        } catch (IOException e) {
            System.out.println("Sending error");
            return "";
        }
    }
    private String read(){
        String response = null;
        try {
            response = reader.readLine();
        } catch (IOException e) {
            System.out.println("Cannot readline");
            return "";
        }
        if (response.startsWith("-ERR")){
            System.out.println("Server error");
        }
        return response;
    }
    public void login(String username, String password){
        send("USER " + username);
        send("PASS " + password);
    }
    public void logout(){
        send("QUIT");
    }
    public int getNumberOfNewMessages(){
        String response = send("STAT");
        String[] values = response.split(" ");
        return Integer.parseInt(values[1]);
    }
    public void getMessage(int index){
        String response = send("RETR " + index);

        String headerName = null;
        while ((response = read()).length() != 0) {
            if (response.startsWith("\t")) {
                continue; //no process of multiline headers
            }
            int colonPosition = response.indexOf(":");
            headerName = response.substring(0, colonPosition);
            String headerValue;
            if (headerName.length() > colonPosition) {
                headerValue = response.substring(colonPosition + 2);
            } else {
                headerValue = "";
            }
            System.out.println(headerName+" "+headerValue);
        }

        StringBuilder bodyBuilder = new StringBuilder();
        while (!(response = read()).equals(".")) {
            bodyBuilder.append(response + "\n");
        }
        System.out.println(bodyBuilder.toString());
    }
}