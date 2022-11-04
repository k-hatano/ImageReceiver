
import com.sun.net.httpserver.*;
import java.nio.charset.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class ImageReceiverServer {
	ImageReceiver parent;

	public ImageReceiverServer(ImageReceiver ImageReceiver) {
		parent = ImageReceiver;

		try {
		    HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
		    server.createContext("/", new ImageReceiverServerHandler());
		    server.start();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}

	}

	private class ImageReceiverServerHandler implements HttpHandler {
		public void handle(HttpExchange exchange) {
			try {
				String boundary = null;

				String startLine =
					exchange.getRequestMethod() + " " +
					exchange.getRequestURI().toString() + " " +
					exchange.getProtocol();
				System.out.println(startLine);

				Headers requestHeaders = exchange.getRequestHeaders();
				for (String name : requestHeaders.keySet()) {
					System.out.println(name + ": " + requestHeaders.getFirst(name));
					if (name.equals("Content-type")) {
						String aHeader = requestHeaders.getFirst(name);

						System.out.println("***");
						Pattern pattern = Pattern.compile("boundary=([^=; ]+)");
						Matcher matcher = pattern.matcher(aHeader);
						if (matcher.find()) {
							boundary = matcher.group(1);
							System.out.println(boundary);
						}
					}
				}
				System.out.println("boundary : " + boundary);

				InputStream inputStream = exchange.getRequestBody();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "US-ASCII"));
				byte[] bytes = inputStream.readAllBytes();
				String bytesString = new String(bytes, StandardCharsets.US_ASCII);
				int startIndex = bytesString.indexOf("--" + boundary);
				int firstLinefeedIndex = bytesString.indexOf("\r\n\r\n"); // TODO: Unix系の場合は？
				int endIndex = bytesString.indexOf("--" + boundary + "--");
				System.out.println("index : " + startIndex + "(" + firstLinefeedIndex + ")" + " - " + endIndex);

				byte[] imageBytes = null; 
				if (firstLinefeedIndex > 0 && endIndex > 0) {
					imageBytes = Arrays.copyOfRange(bytes, firstLinefeedIndex + 4, endIndex);
					ImageReceiverServer.printBytesAsHex(imageBytes);
				}

				inputStream.close();

				String response = "<form action='http://localhost:3000' method='POST' enctype='multipart/form-data'><input type='file' name='file'><input type='submit'></form>";

				Headers responseHeaders = exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/html");

				exchange.sendResponseHeaders(200, response.length());
				OutputStream outputStream = exchange.getResponseBody();
				outputStream.write(response.getBytes());
				outputStream.close();

				if (imageBytes != null && imageBytes.length > 0) {
					parent.imageReceiverImport.importFromBinary(imageBytes);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void printBytesAsHex(byte[] bytes) {
		System.out.println("" + bytes.length + " bytes");
		for (int l = 0; l < bytes.length / 16; l += 16) {
			for (int i = 0; i < 16; i++) {
				if (bytes.length <= l + i) {
					break;
				}
				System.out.print(Integer.toHexString(bytes[l + i] & 0xFF));
				System.out.print(" ");
			}
			System.out.println("");
		}
	}

}
