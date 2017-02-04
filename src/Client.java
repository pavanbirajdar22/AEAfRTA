import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	private static Socket socket;	
	public static byte[] key;
	public static byte[] xorData;
	public static String data;
	public static int[] indexes;

	public static void main(String args[])
	{
		try
		{	
			data = "Hey there! I am using AEAfRTA!";
			key = KeyGen.generateKey();
			xorData = XorDataNKey.XorDataWithKey(data,key);
			indexes = TableGen.getTable();
			xorData = XorDataNKey.XorDataWithKeyAtIndex(xorData, key,indexes);

			String host = "192.168.1.4";
			int port = 25000;
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);

			//Table
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			dOut.writeInt(indexes.length);
			for (int e : indexes) dOut.writeInt(e);
			dOut.flush();
			System.out.println("Indexes sent to the server");

			//Key
			dOut.writeInt(key.length); // write length of the message
			dOut.write(key);
			dOut.flush();
			System.out.println("Key sent to the server");

			//Data
			dOut.writeInt(xorData.length); // write length of the message
			dOut.write(xorData);
			dOut.flush();
			System.out.println("Encrypted data sent");

		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		finally
		{
			//Closing the socket
			try
			{
				socket.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
