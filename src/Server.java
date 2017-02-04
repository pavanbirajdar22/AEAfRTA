import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

	private static Socket socket;
	private static ServerSocket serverSocket; 
	private static int port = 25000;

	private static int table[][];
	private static byte[] key;
	
	private static byte[] xorData;
	private static byte[] encryptedData;
	private static String decryptedData;
	
	public static void main(String[] args){
		
		try{
			serverSocket = new ServerSocket(port);
			System.out.println("Server Online");
			while(true){
				socket = serverSocket.accept();
							
				DataInputStream din = new DataInputStream(socket.getInputStream());
				
				int indexes[] = null;

				int length = din.readInt();                 
				if(length > 0) {
					indexes = new int[length];
					for (int i = 0; i < indexes.length; ++i) {
						indexes[i] = din.readInt();
					}

				}

				length = din.readInt();
				if(length > 0) {
					key = new byte[length];
					din.readFully(key, 0, key.length); 
				}

				//System.out.println(key);
				
				encryptedData = null;
				length = din.readInt();
				if(length > 0) {
					encryptedData = new byte[length];
				    din.readFully(encryptedData, 0, encryptedData.length); 
				}
				//System.out.println(encryptedData);
				
				xorData = XorDataNKey.XorDataWithKeyAtIndex(encryptedData, key, indexes);
				decryptedData = XorDataNKey.XorDataWithKey(xorData, key);
				System.out.println("Message received from user - " + decryptedData);
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