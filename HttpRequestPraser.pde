import java.util.HashMap;

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