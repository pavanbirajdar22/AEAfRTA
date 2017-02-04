import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
			data = "Hey Server Hey Server";
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
			
			/*
			//Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			String sendMessage = key + "\n";
			bw.write(sendMessage);
			bw.flush();
			*/
			
			/*
			//Get the return message from the server
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String message = br.readLine();
			System.out.println("Response received from the server : " +message);
			*/
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
