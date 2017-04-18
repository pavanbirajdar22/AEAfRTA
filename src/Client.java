import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
	private static byte[] encryptedData;
	private static List<String> dataBlocks;

	public static List<String> splitIntoBlocks(String text, int size) {

		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}
		return ret;
	}

	public static void main(String args[]) {
		try {
			RSA rsa = new RSA();
			Scanner sc = new Scanner(System.in);

			System.out.print("Server IP - ");
			host = sc.nextLine();
			port = 25000;
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);

			// Get Public Key

			DataInputStream din = new DataInputStream(socket.getInputStream());
			int length = din.readInt();
			if (length > 0) {
				publicKey = new byte[length];
				din.readFully(publicKey, 0, publicKey.length);
			}

			// Generate session key and table

			key = KeyGen.generateKey();
			table = TableGen.getTable();
			TableGen.generateShiftingKeys();

			// Encryption of symmetric key using public serverKey

			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			byte[] encryptedKey = rsa.encrypt(key, RSA.convertBytesToPublicKey(publicKey));
			dOut.writeInt(encryptedKey.length);
			dOut.write(encryptedKey);
			System.out.println("Step 1: Encrypted key sent to the server");

			// Encryption of shifting keys

			byte[] encryptedShiftingKeys = rsa.encrypt(TableGen.getShiftingKeys(),
					RSA.convertBytesToPublicKey(publicKey));
			dOut.writeInt(encryptedShiftingKeys.length);
			dOut.write(encryptedShiftingKeys);
			System.out.println("Step 1: Encrypted Shifting keys sent to the server");

			// Table sent

			for (int i = 0; i < TableGen.matrix.length; i++)
				for (int j = 0; j < TableGen.matrix.length; j++)
					dOut.writeInt(TableGen.matrix[i][j]);

			dOut.flush();
			System.out.println("Step 1: Table sent");

			while (true) {

				// Get message from user

				System.out.print("Enter message to send - ");
				data = sc.nextLine();

				if (data.equalsIgnoreCase("exit")) {
					dOut.writeInt(-1);
					dOut.flush();
					System.out.println("Session over");
					break;
				}

				// Send encrypted Data

				long startTime = System.nanoTime();
				if (data.length() > 128) {

					byte[] encryptedBlocks = new byte[data.length()];
					int k = 0;

					// dataBlocks = new ArrayList<String>();
					dataBlocks = splitIntoBlocks(data, 128);

					for (String temp : dataBlocks) {
						xorData = XorDataNKey.XorDataWithKey(temp, key);
						encryptedData = XorDataNKey.XorDataWithKeyAtIndex(xorData, key, TableGen.getIndexes());
						for (int i = 0; i < encryptedData.length;) {
							encryptedBlocks[k++] = encryptedData[i++];
						}
					}

					long endTime = System.nanoTime();
					System.out.println("Took " + (endTime - startTime) + " ns");

					dOut.writeInt(encryptedBlocks.length);
					dOut.write(encryptedBlocks);
					dOut.flush();
				} else {

					xorData = XorDataNKey.XorDataWithKey(data, key);
					encryptedData = XorDataNKey.XorDataWithKeyAtIndex(xorData, key, TableGen.getIndexes());

					long endTime = System.nanoTime();
					System.out.println("Took " + (endTime - startTime) + " ns");

					dOut.writeInt(encryptedData.length);
					dOut.write(encryptedData);
					dOut.flush();

				}
				System.out.println("Encrypted data sent");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			// Closing the socket
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
