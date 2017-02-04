import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

	private static Socket socket;
	private static ServerSocket serverSocket; 
	
	private static int table[][];
	private static byte[] key;
	private static byte[] xorData;
	
	public static void main(String[] args){
		
		try{

			int port = 25000;
			serverSocket = new ServerSocket(port);
			System.out.println("Server Started and listening to the port 25000");

			while(true){
				
				//Reading the message from the client
				socket = serverSocket.accept();
				
				/*InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String key = br.readLine();
				System.out.println("Message received from client is " + key);
*/
				
				
				DataInputStream din = new DataInputStream(socket.getInputStream());
				
				int indexes[];
				int length = din.readInt();                 
				if(length > 0) {
					indexes = new int[length];
					for (int i = 0; i < indexes.length; ++i) {
						indexes[i] = din.readInt();
						System.out.print(indexes[i] + " ");
					}
						
				}
								
				length = din.readInt();
				if(length > 0) {
					key = new byte[length];
				    din.readFully(key, 0, key.length); 
				}
				System.out.println(key);
				
				byte[] encryptedMessage = null;
				length = din.readInt();
				if(length > 0) {
					encryptedMessage = new byte[length];
				    din.readFully(encryptedMessage, 0, encryptedMessage.length); 
				}
				System.out.println(encryptedMessage);
				
				
				//Sending the response back to the client.
				/*String returnMessage = key;
				OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				bw.write(returnMessage);
				System.out.println("Message sent to the client is " + returnMessage);
				bw.flush();*/
				
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				socket.close();
				serverSocket.close();
			}
			catch(Exception e){}
		}
	}
}