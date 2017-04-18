import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

	private static Socket socket;
	private static ServerSocket serverSocket;
	private static int port = 25000;
	private static byte[] key;
	private static byte[] xorData;
	private static byte[] encryptedData;
	private static String decryptedData;
	private static byte[] encryptedDataBlocks;

	public static List<byte[]> splitIntoBlocks(byte[] source, int chunksize) {

		List<byte[]> result = new ArrayList<byte[]>();
		int start = 0;
		while (start < source.length) {
			int end = Math.min(source.length, start + chunksize);
			result.add(Arrays.copyOfRange(source, start, end));
			start += chunksize;
		}

		return result;
	}

	public static void main(String[] args) {

		try {

			RSA rsa = new RSA();
			byte[] publicKey = rsa.getPublicKey();
			// System.out.println(publicKey);

			serverSocket = new ServerSocket(port);

			System.out.println("Server Online");

			socket = serverSocket.accept();

			// Send Public Key
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			dOut.writeInt(publicKey.length);
			dOut.write(publicKey);
			dOut.flush();
			System.out.println("Step 1: Public Key sent");

			// Get encrypted key
			DataInputStream din = new DataInputStream(socket.getInputStream());
			int length = din.readInt();
			byte[] encryptedKey = null;
			if (length > 0) {
				encryptedKey = new byte[length];
				din.readFully(encryptedKey, 0, encryptedKey.length);
			}
			System.out.println("Step 2: Key received");

			length = din.readInt();
			byte[] encryptedShiftingKeys = null;
			if (length > 0) {
				encryptedShiftingKeys = new byte[length];
				din.readFully(encryptedShiftingKeys, 0, encryptedShiftingKeys.length);
			}

			TableGen.setShiftingKeys(new String(rsa.decrypt(encryptedShiftingKeys)));

			System.out.println("Step 2: ShiftingKeys received");

			int[][] matrix = new int[16][16];
			for (int i = 0; i < 16; i++)
				for (int j = 0; j < 16; j++)
					matrix[i][j] = din.readInt();

			System.out.println("Table received");
			TableGen.setMatrix(matrix);
			// Decrypt session key
			key = rsa.decrypt(encryptedKey);

			while (true) {

				// Get indexes

				/*
				 * int indexes[] = null;
				 * 
				 * length = din.readInt(); if(length > 0) { indexes = new
				 * int[length]; for (int i = 0; i < indexes.length; ++i) {
				 * indexes[i] = din.readInt(); } } else{
				 * System.out.println("Session over"); break; }
				 * 
				 * System.out.println();
				 */
				// Get encrypted data

				encryptedData = null;
				encryptedDataBlocks = null;
				length = din.readInt();

				if (length > 128) {
					encryptedDataBlocks = new byte[length];
					din.readFully(encryptedDataBlocks, 0, encryptedDataBlocks.length);
					List<byte[]> blocks = new ArrayList<byte[]>();
					blocks = splitIntoBlocks(encryptedDataBlocks, 128);
					String decryptedBlocks = new String();
					System.out.println("Message received from user - ");

					for (byte[] block : blocks) {
						xorData = XorDataNKey.XorDataWithKeyAtIndex(block, key, TableGen.getIndexes());
						decryptedData = XorDataNKey.XorDataWithKey(xorData, key);
						decryptedBlocks += decryptedData;
					}
					System.out.println(decryptedBlocks);
				} else {
					encryptedData = new byte[length];
					din.readFully(encryptedData, 0, encryptedData.length);
					xorData = XorDataNKey.XorDataWithKeyAtIndex(encryptedData, key, TableGen.getIndexes());
					decryptedData = XorDataNKey.XorDataWithKey(xorData, key);
					System.out.println("Message received from user - " + decryptedData);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}
}