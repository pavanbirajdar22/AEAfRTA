import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static Socket socket;
	private static int port;
	private static String host;
	public static byte[] key;
	public static byte[] xorData;
	public static String data;
	public static int[] indexes;
	public static byte[] publicKey;
	public static int[][] table;
	
	public static void main(String args[]) {
		try
		{	
			RSA rsa = new RSA();
			Scanner sc = new Scanner(System.in);			

			System.out.print("Server IP - ");
			host = sc.nextLine();
			port = 25000;
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);

			//Get Public Key
			
			DataInputStream din = new DataInputStream(socket.getInputStream());
			int length = din.readInt();                 
			if(length > 0) {
				publicKey = new byte[length];
				din.readFully(publicKey, 0, publicKey.length);
			}
			
			// Generate session key and table
			
			key = KeyGen.generateKey();
			table = TableGen.getTable();
			
			//Encryption of symmetric key using public serverKey
			
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			byte[] encryptedKey = rsa.encrypt(key, RSA.convertBytesToPublicKey(publicKey));
			dOut.writeInt(encryptedKey.length);
			dOut.write(encryptedKey);
			dOut.flush();
			System.out.println("Step 1: Encrypted key sent to the server");

			System.out.println();
			
			while(true){
				
				// Get message from user
				
				System.out.print("Enter message to send - ");
				data=sc.nextLine();
				
				if(data.equalsIgnoreCase("exit")){
					dOut.writeInt(-1);
					dOut.flush();
					System.out.println("Session over");
					break;
				}
				
				// Send indexes
				
				indexes = TableGen.getIndexes(table);
				dOut.writeInt(indexes.length);
				for (int e : indexes) dOut.writeInt(e);
				dOut.flush();
				System.out.println("Step 2: Indexes sent to the server");

				
				//Send encrypted Data
				
				xorData = XorDataNKey.XorDataWithKey(data,key);
				xorData = XorDataNKey.XorDataWithKeyAtIndex(xorData,key,indexes);
				dOut.writeInt(xorData.length); // write length of the message
				dOut.write(xorData);
				dOut.flush();
				System.out.println("Encrypted data sent");
			
			}
		}
		catch (Exception exception){
			exception.printStackTrace();
		}
		finally{
			//Closing the socket
			try{
				socket.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
