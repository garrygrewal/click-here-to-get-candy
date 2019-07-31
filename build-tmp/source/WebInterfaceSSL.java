import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.*; 
import java.security.*; 
import javax.net.ssl.*; 
import java.util.HashMap; 
import java.io.BufferedOutputStream; 
import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.io.OutputStream; 
import java.io.PrintWriter; 
import java.net.ServerSocket; 
import java.net.Socket; 
import java.util.Date; 
import java.util.StringTokenizer; 
import java.io.UnsupportedEncodingException; 
import java.net.URLDecoder; 
import java.net.URLEncoder; 
import java.util.HashMap; 
import java.util.Map; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class WebInterfaceSSL extends PApplet {






static final String ROOT_LOCATION        = "/Users/alvinleung/Documents/iat 222 reading summery/iat 222 final project/WebInterfaceSSL";

static final String DEFAULT_FILE         = "index.html";
static final String FILE_NOT_FOUND       = "404.html";
static final String METHOD_NOT_SUPPORTED = "not_supported.html";

static final File   WEB_ROOT             = new File(ROOT_LOCATION);

// port to listen connection
static final int PORT = 8080;
	
// verbose mode
static final boolean verbose = true;

String ksName = "selfsigned.jks";
char ksPass[] = "password".toCharArray();
char ctPass[] = "password".toCharArray();

// SSL code from
// http://www.herongyang.com/JDK/HTTPS-Server-Test-Program-HttpsHello.html

// simple ssl server
// https://www.codeproject.com/Tips/1043003/Create-a-Simple-Web-Server-in-Java-HTTPS-Server

public void setup() {

	try {

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(ROOT_LOCATION+"/"+ksName), ksPass);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, ctPass);

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(kmf.getKeyManagers(), null, null);

		SSLServerSocketFactory ssf = sc.getServerSocketFactory();
		SSLServerSocket serverConnect = (SSLServerSocket) ssf.createServerSocket(PORT);
         
		//System.out.println("Server started:");

		// ServerSocket serverConnect = new ServerSocket(PORT);
		System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			
		// // we listen until user halts server execution
		while (true) {
			JavaHTTPServer myServer = new JavaHTTPServer((SSLSocket)serverConnect.accept());
				
		 	if (verbose) {
				System.out.println("Connecton opened. (" + new Date() + ")");
		 	}
				
			// create dedicated thread to manage the client connection
			Thread thread = new Thread(myServer);
			thread.start();
		}
			
	} catch (IOException e) {
		System.err.println("Server Connection error : " + e.getMessage());
	} catch (Exception e) {
		System.err.println(" Exception : " + e.getMessage());
	}

}

public void draw() {

}


public class HttpRequestParser {

	private HashMap<String,String> data = new HashMap<String,String>();

	private String requestHeader = "";
	private String requestBody = "";

	public String fileRequested;
	public String method;

	public HttpRequestParser(BufferedReader inputBuffer) {

		try {

			// get first line of the request from the client
			String input = inputBuffer.readLine();
			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			// we get file requested
			fileRequested = parse.nextToken().toLowerCase();

			requestHeader += input;

			// read the rest of the header
			String line;

			while((line = inputBuffer.readLine()) != null) {

				// header stop when there is a an empty line
				if (line.isEmpty()) {
					break;
				}

				String[] keyAndEntry = line.split(":",2);

				data.put(keyAndEntry[0], keyAndEntry[1].trim());

				requestHeader += line+"\n";
			}

			if(data.get("Content-Length") != null) {

				requestBody = "";

				// picks up where the header end and procced into reading the 
				for (int i = 0; i < getInt("Content-Length"); ++i) {
					requestBody += (char)inputBuffer.read();
				}
				
				//println("Content Length: " +getInt("Content-Length"));
				//println("Content:");
				//println(requestBody);

			} else {
				if (this.method == "POST")
					println("POST Request Bad Format: Content Length Not Specified");
			}

			
			//int contentLength

		} catch (IOException exception) {

			println("IOException: "+exception);

		}

	}

	public String getString(String key) {
		return data.get(key);
	}

	public int getInt(String key) {

		if(data.get(key) == null) return 0;

		return parseInt(data.get(key));
	}

	public String getHeader() {
		return this.requestHeader;
	}

	public String getBody() {
		return this.requestBody;
	}

	public String toString() {
		return requestHeader+"\n"+requestBody;
	}

}


public class QueryStringParser {

	private HashMap<String,String> map = new HashMap<String,String>();

	public QueryStringParser(String input) {

		String[] nameValuePairs = input.split("&");
		for (String nameValuePair : nameValuePairs) {
			String[] nameValue = nameValuePair.split("=");
			try {
				map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(
				nameValue[1], "UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("This method requires UTF-8 encoding support", e);
			}
		}
	}

	public String get(String key) {
		return this.map.get(key);
	}

	public String toString() {
		return this.map.toString();
	}

}

public class PostRequestHandler {
  
  public void onRequest(QueryStringParser params) {
    if(params.get("canHaveCandy").equals("true")) {
      // can get candy
      //myPort.write('1');
      //myPort.write("N");
    } else {
      // cannot get candy
    }
  }
}
// /* HttpsHello.java
//  * Copyright (c) 2014 HerongYang.com, All Rights Reserved.
//  */
// import java.io.*;
// import java.security.*;
// import javax.net.ssl.*;

// public class HttpsHello {

//    public static void inits() {
      
//       String ksName = "herong.jks";
//       char ksPass[] = "HerongJKS".toCharArray();
//       char ctPass[] = "My1stKey".toCharArray();

//       try {
//          KeyStore ks = KeyStore.getInstance("JKS");
//          ks.load(new FileInputStream(ksName), ksPass);

//          KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//          kmf.init(ks, ctPass);

//          SSLContext sc = SSLContext.getInstance("TLS");
//          sc.init(kmf.getKeyManagers(), null, null);

//          SSLServerSocketFactory ssf = sc.getServerSocketFactory();
//          SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(8888);
         
//          System.out.println("Server started:");

//          printServerSocketInfo(s);
         
//          // Listening to the port
//          SSLSocket c = (SSLSocket) s.accept();
//          printSocketInfo(c);
//          BufferedWriter w = new BufferedWriter(
//             new OutputStreamWriter(c.getOutputStream()));
//          BufferedReader r = new BufferedReader(
//             new InputStreamReader(c.getInputStream()));
         
//          String m = r.readLine();
         
//          w.write("HTTP/1.0 200 OK");
//          w.newLine();
//          w.write("Content-Type: text/html");
//          w.newLine();
//          w.newLine();
//          w.write("<html><body>Hello world!</body></html>");
//          w.newLine();
//          w.flush();
//          w.close();
//          r.close();
//          c.close();
//       } catch (Exception e) {
//          e.printStackTrace();
//       }
//    }
//    private static void printSocketInfo(SSLSocket s) {
//       System.out.println("Socket class: "+s.getClass());
//       System.out.println("   Remote address = "
//          +s.getInetAddress().toString());
//       System.out.println("   Remote port = "+s.getPort());
//       System.out.println("   Local socket address = "
//          +s.getLocalSocketAddress().toString());
//       System.out.println("   Local address = "
//          +s.getLocalAddress().toString());
//       System.out.println("   Local port = "+s.getLocalPort());
//       System.out.println("   Need client authentication = "
//          +s.getNeedClientAuth());
//       SSLSession ss = s.getSession();
//       System.out.println("   Cipher suite = "+ss.getCipherSuite());
//       System.out.println("   Protocol = "+ss.getProtocol());
//    }
//    private static void printServerSocketInfo(SSLServerSocket s) {
//       System.out.println("Server socket class: "+s.getClass());
//       System.out.println("   Socket address = "
//          +s.getInetAddress().toString());
//       System.out.println("   Socket port = "
//          +s.getLocalPort());
//       System.out.println("   Need client authentication = "
//          +s.getNeedClientAuth());
//       System.out.println("   Want client authentication = "
//          +s.getWantClientAuth());
//       System.out.println("   Use client mode = "
//          +s.getUseClientMode());
//    } 
// }















// The tutorial can be found just here on the SSaurel's Blog : 
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class JavaHTTPServer implements Runnable{ 
	
	// Client Connection via Socket Class
	private SSLSocket connect;

  public PostRequestHandler postHandler = new PostRequestHandler();
	
	public JavaHTTPServer(SSLSocket c) {
		connect = c;
	}
	
	

	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
		String fileRequested = null;
		
		try {
			// we read characters from the client via input stream on the socket
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			// we get character output stream to client (for headers)
			out = new PrintWriter(connect.getOutputStream());
			// get binary output stream to client (for requested data)
			dataOut = new BufferedOutputStream(connect.getOutputStream());

			HttpRequestParser httpRequestParser = new HttpRequestParser(in);
			/*
			// get first line of the request from the client
			String input = in.readLine();
			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			// we get file requested
			fileRequested = parse.nextToken().toLowerCase();*/

			String method = httpRequestParser.method;
			fileRequested = httpRequestParser.fileRequested;

			if (method == null)return;
			
			// we support only GET and HEAD methods, we check
			if (!method.equals("GET")  &&  !method.equals("HEAD") && !method.equals("POST")) {
				if (verbose) {
					System.out.println("501 Not Implemented : " + method + " method.");
				}
				
				// we return the not supported file to the client
				File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
				int  fileLength = (int) file.length();
				String contentMimeType = "text/html";
				//read content to return to client
				byte[] fileData = readFileData(file, fileLength);
					
				// we send HTTP Headers with data to client
				out.println("HTTP/1.1 501 Not Implemented");
				out.println("Server: Java HTTP Server from SSaurel : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + contentMimeType);
				out.println("Content-length: " + fileLength);
				out.println(); // blank line between headers and content, very important !
				out.flush(); // flush character output stream buffer
				// file
				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
				
			} else {
				// GET or HEAD method
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}
				
				File file = new File(WEB_ROOT, fileRequested);
				int fileLength = (int) file.length();
				String content = getContentType(fileRequested);
				
				if (method.equals("GET")) { // GET method so we return content

					byte[] fileData = readFileData(file, fileLength);
					
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Server: Java HTTP Server from SSaurel : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: " + content);
					out.println("Content-length: " + fileLength);
					out.println(); // blank line between headers and content, very important !
					out.flush(); // flush character output stream buffer
					
					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();

				} else if (method.equals("POST")) {

					println("Incoming POST request");
					
					onPostRequest(httpRequestParser);

				}
				
				if (verbose) {
					System.out.println("File " + fileRequested + " of type " + content + " returned");
				}
				
			}
			
		} catch (FileNotFoundException fnfe) {
			try {
				fileNotFound(out, dataOut, fileRequested);
			} catch (IOException ioe) {
				System.err.println("Error with file not found exception : " + ioe.getMessage());
			}
			
		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			
			if (verbose) {
				System.out.println("Connection closed.\n");
			}
		}
		
	}

	public void onPostRequest(HttpRequestParser httpRequestParser) {


		println(httpRequestParser.getHeader());

		QueryStringParser qs = new QueryStringParser(httpRequestParser.getBody());
    
    
    postHandler.onRequest(qs);
		
    //println(qs.get("canHaveCandy"));

		//if(qs.get("canHaveCandy").equals("true")) {
		//	println("user can get the candy");
		//} else {
		//	println("user candy request denied");
		//}

	}

	public void onGetRequest(HttpRequestParser httpRequestParser) {
  
	}

	
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		
		return fileData;
	}
	
	// return supported MIME Types
	private String getContentType(String fileRequested) {

		if (fileRequested.endsWith(".css"))
			return "text/css";

		if (fileRequested.endsWith(".js"))
			return "text/javascript";

		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}

	
	private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server from SSaurel : 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
		
		if (verbose) {
			System.out.println("File " + fileRequested + " not found");
		}
	}
	
}





/*
public class URLStringUil {
 public static String mapToString(Map<String, String> map) {
   StringBuilder stringBuilder = new StringBuilder();

   for (String key : map.keySet()) {
    if (stringBuilder.length() > 0) {
     stringBuilder.append("&");
    }
    String value = map.get(key);
    try {
     stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
     stringBuilder.append("=");
     stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
    } catch (UnsupportedEncodingException e) {
     throw new RuntimeException("This method requires UTF-8 encoding support", e);
    }
   }

   return stringBuilder.toString();
  }

  public static Map<String, String> stringToMap(String input) {
   Map<String, String> map = new HashMap<String, String>();

   String[] nameValuePairs = input.split("&");
   for (String nameValuePair : nameValuePairs) {
    String[] nameValue = nameValuePair.split("=");
    try {
     map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(
     nameValue[1], "UTF-8") : "");
    } catch (UnsupportedEncodingException e) {
     throw new RuntimeException("This method requires UTF-8 encoding support", e);
    }
   }

   return map;
  }
}*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "WebInterfaceSSL" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
