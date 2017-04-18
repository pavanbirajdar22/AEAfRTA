import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

	private static Socket socket;
	private static ServerSocket serverSocket; 
	private static int port = 25000;
	private static byte[] key;
	private static byte[] xorData;
	private static byte[] encryptedData;
	private static String decryptedData;


	public static void main(String[] args){

		try{

			RSA rsa = new RSA();
			byte[] publicKey = rsa.getPublicKey();
			//System.out.println(publicKey);

			serverSocket = new ServerSocket(port);
			
			System.out.println("Server Online");

			socket = serverSocket.accept();

			//Send Public Key
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			dOut.writeInt(publicKey.length);
			dOut.write(publicKey);
			dOut.flush();
			System.out.println("Step 1: Public Key sent");

			//Get encrypted key
			DataInputStream din = new DataInputStream(socket.getInputStream());
			int length = din.readInt();
			byte[] encryptedKey = null;
			if(length > 0) {
				encryptedKey = new byte[length];
				din.readFully(encryptedKey, 0, encryptedKey.length); 
			}
			System.out.println("Step 2: Key received");

			//Decrypt session key
			key = rsa.decrypt(encryptedKey);				

			while(true){

				// Get indexes

				int indexes[] = null;

				length = din.readInt();
				if(length > 0) {
					indexes = new int[length];
					for (int i = 0; i < indexes.length; ++i) {
						indexes[i] = din.readInt();
					}
				}
				else{
					System.out.println("Session over");
					break;
				}

				System.out.println();

				// Get encrypted data

				encryptedData = null;
				length = din.readInt();

				if(length > 0) {
					encryptedData = new byte[length];
					din.readFully(encryptedData, 0, encryptedData.length); 
				}

				xorData = XorDataNKey.XorDataWithKeyAtIndex(encryptedData, key,indexes);
				decryptedData = XorDataNKey.XorDataWithKey(xorData, key);
				System.out.println("Message received from user - " + decryptedData);
			}

		}
		catch (IOException e){
			e.printStackTrace();
		} catch (Exception e) {
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