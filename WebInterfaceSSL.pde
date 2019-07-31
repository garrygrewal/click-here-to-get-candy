import java.io.*;
import java.security.*;
import javax.net.ssl.*;
import processing.serial.*;

Serial myPort;    

static final String ROOT_LOCATION        = "/Users/garry/IAT222/WebInterfaceSSL";

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

void setup() {
  
  //printArray(Serial.list());
  //myPort = new Serial(this, Serial.list()[9], 9600);


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

void draw() {

}