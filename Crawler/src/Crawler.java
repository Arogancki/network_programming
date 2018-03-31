import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;

import javax.xml.bind.DatatypeConverter;

public class Crawler{
	private int pauseTimeSec=1000;
	private int port;
    private String host;
    private String file;
    private int depth;
    private String path;
    List<String> visited = new ArrayList<String>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    public Crawler(String hostName, String portNumber, 
    		String _file, String _depth) throws NumberFormatException{
    	port = Integer.parseInt(portNumber);
        host = hostName;
        file = _file;
        depth = Integer.parseInt(_depth);
        path= "/";
        if (depth<0)
        	depth=0;
    }
    private void Connect() throws UnknownHostException, IOException{
    	socket = new Socket(host, port);
    	writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
    	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    private void Disconnect(){
    	try {
			reader.close();
		} catch (IOException e2) {}
        try {
			writer.close();
		} catch (IOException e1) {}
        try {
			socket.close();
		} catch (IOException e) {}
        reader=null;
        writer=null;
        socket=null;
    }
    private String get(String path) throws Exception{
    	Thread.sleep(pauseTimeSec);
    	Connect();
    	writer.write("GET " + path + " HTTP/1.0\r\n");
    	writer.write("Host: "+host+"\r\n");
    	writer.write("Cache-Control: no-cache\r\n");
    	writer.write("\r\n");
        writer.flush();
    	StringBuffer sb = new StringBuffer();
    	String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line+"\n");
        }
    	Disconnect();
        return sb.toString();
        
    }
    private boolean checkIfVisited(String path){
    	if (path.contains("."))
    		path = path.substring(0, path.lastIndexOf("."));
    	if (path.equalsIgnoreCase("/") || path.equalsIgnoreCase(""))
    		path="index";
    	path = path.toLowerCase();
    	return visited.contains(path);
    }
    private void addNotVisited(String path){
    	if (checkIfVisited(path))
    		visited.add(path);
    }
    private String addElement(String name, String content){
    	return "<"+name+">"+content+"</"+name+">";
    }
    private String addElement(String name, String params, String content){
    	return "<"+name+" "+params+" >"+content+"</"+name+">";
    }
    private String resolveHref(String path, String resource){
    	if (resource.startsWith("/"))
    		return resource;
    	if (path.endsWith("/"))
    		return path + resource;
    	return path + "/" + resource;
    }
    private String resolvePath(String href){
    	int lios = href.lastIndexOf("/");
    	if (lios==-1)
    		return "/";
    	return href.substring(0, lios+1);
    }
    private List<String> getAllMatches(String text, String regex, int iSsubstring) {
    	List<String> allMatches = new ArrayList<String>();
    	Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
    	    .matcher(text);
    	while (m.find()) {
    		if (iSsubstring==0)
    			allMatches.add(m.group());
    		else
    			allMatches.add(m.group(iSsubstring));
    	}
    	return new ArrayList<String>(new LinkedHashSet<String>(allMatches));
    }
    private String crawl(String path, String resource, int currentDepth) throws Exception{
    	if (currentDepth>depth || checkIfVisited(path+resource))
    		return "";
    	addNotVisited(path+resource);
    	String content = get(path+resource);
    	List<String> imgs = getAllMatches(content, "<img.*?>", 0);
    	List<String> a = getAllMatches(content, "<a .*?>", 0);
    	List<String> emails = getAllMatches(content, "[A-Z0-9._%+-]+@[A-Z0-9.-]+.[A-Z]{2,4}", 0);
    	
    	// images
    	String imgsS="";
    	for (String img : imgs){
    		List<String> imgL = getAllMatches(img, "src=[\"'](.*?)[\"']", 1);
    		if (imgL.size() > 0){
    			imgsS += addElement("IMAGE", imgL.get(0))+"\n";
    		}
    	}
    	
    	// emails
    	for (String e : a){
    		List<String> emailsL = getAllMatches(e, "href=[\"']?mailto:(.*?)([\"'])", 1);
    		if (emailsL.size() > 0){
    			String mail = emailsL.get(0);
    			if (!emails.contains(mail))
    				emails.add(mail);
    		}
    	}
    	String emailsS="";
    	for (String e : emails){
    		emailsS += addElement("EMAIL", e)+"\n";
    	}
    	// emails
    	
    	// files
    	String htmls= "";
    	for (String h : a){
    		List<String> hrefs = getAllMatches(h, "href=[\"'](.*?)[\"']", 1);
    		if (hrefs.size() > 0){
    			String link = hrefs.get(0);
    			// SKIP DIFFERENT DOMAINS
    			if (link.startsWith("https://") || link.startsWith("http://"))
    				continue;
    			if (!link.endsWith(".html") && !link.endsWith(".htm"))
    				continue;
    			String href = resolveHref(path, link);
    			String _path = resolvePath(href);
    			String _resource = href.substring(_path.length());
    			htmls += addElement("FILE", "href=\""+href+"\"", "\n"+crawl(_path, _resource, currentDepth+1))+"\n";
    		}
    	}
    	// files    	
    	return htmls + imgsS + emailsS;
    }
    public void generate() throws Exception{
    	String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	String root = "<SITE url=\""+host+path+"\" depth=\""+depth+"\">";
    	String content = crawl("/", "", 0);
    	String rootEnd = "</SITE>";
    	PrintWriter out;
    	try{
    		out = new PrintWriter(file);
    	    out.print(
    	    		header + "\n" + 
    	    		root + "\n" +
    	    		content + "\n" + 
    	    		rootEnd);
    	    out.flush();
    	    out.close();
    	}
    	catch (Exception e){
    		System.out.println("Cannot save the file.");
    	}
    	System.out.println("File "+file+" saved.");
    }
}