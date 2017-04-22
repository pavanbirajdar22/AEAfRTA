import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Receiver_UI_DEMO extends JFrame {
	private static final long serialVersionUID = 1L;
	JTextArea txtadisplay;
	private static Socket socket;
	private static ServerSocket receiverSocket;
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

	@SuppressWarnings("deprecation")
	public Receiver_UI_DEMO() {
		super("Receiver");
		Container c = getContentPane();
		txtadisplay = new JTextArea();
		txtadisplay.setLineWrap(true);
		txtadisplay.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtadisplay.setEditable(false);

		c.add(new JScrollPane(txtadisplay), BorderLayout.CENTER);

		setSize(800, 560);
		show();
	}

	public void runReceiver() {
		try {

			RSA rsa = new RSA();
			byte[] publicKey = rsa.getPublicKey();
			// System.out.println(publicKey);

			receiverSocket = new ServerSocket(port);

			System.out.println("Receiver Online");
			txtadisplay.setText(txtadisplay.getText() + "Receiver Online\n\n");

			socket = receiverSocket.accept();

			// Send Public Key
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			dOut.writeInt(publicKey.length);
			dOut.write(publicKey);
			dOut.flush();
			System.out.println("Step 1: Public Key sent");
			txtadisplay.setText(txtadisplay.getText() + "Step 1: Public Key sent\n");

			// Get encrypted key
			DataInputStream din = new DataInputStream(socket.getInputStream());
			int length = din.readInt();
			byte[] encryptedKey = null;
			if (length > 0) {
				encryptedKey = new byte[length];
				din.readFully(encryptedKey, 0, encryptedKey.length);
			}
			System.out.println("Step 2: Key received");
			txtadisplay.setText(txtadisplay.getText() + "Step 2: Key received\n");

			// Get shifting values
			length = din.readInt();
			byte[] encryptedShiftingKeys = null;
			if (length > 0) {
				encryptedShiftingKeys = new byte[length];
				din.readFully(encryptedShiftingKeys, 0, encryptedShiftingKeys.length);
			}

			TableGen.setShiftingKeys(new String(rsa.decrypt(encryptedShiftingKeys)));
			System.out.println("Step 3: ShiftingKeys received");
			txtadisplay.setText(txtadisplay.getText() + "Step 3: ShiftingKeys received\n");

			// Get Table
			int[][] matrix = new int[16][16];
			for (int i = 0; i < 16; i++)
				for (int j = 0; j < 16; j++)
					matrix[i][j] = din.readInt();

			System.out.println("Table received");
			txtadisplay.setText(txtadisplay.getText() + "Step 4: Table received\n\n");
			TableGen.setMatrix(matrix);

			// Decrypt session key
			key = rsa.decrypt(encryptedKey);

			while (true) {

				System.out.println();
				encryptedData = null;
				encryptedDataBlocks = null;
				length = din.readInt();

				if (length > 128) {
					encryptedDataBlocks = new byte[length];
					din.readFully(encryptedDataBlocks, 0, encryptedDataBlocks.length);
					txtadisplay.setText(txtadisplay.getText() + "Data received from user - "
							+ new String(encryptedDataBlocks) + "\n");
					encryptedDataBlocks = Base64.getDecoder().decode(encryptedDataBlocks);

					List<byte[]> blocks = new ArrayList<byte[]>();
					blocks = splitIntoBlocks(encryptedDataBlocks, 128);
					String decryptedBlocks = new String();

					for (byte[] block : blocks) {
						xorData = XorDataNKey.XorDataWithKeyAtIndex(block, key, TableGen.getIndexes());
						decryptedData = XorDataNKey.XorDataWithKey(xorData, key);
						decryptedBlocks += decryptedData;
					}

					System.out.println("Message received from user - " + decryptedBlocks);
					txtadisplay.setText(txtadisplay.getText() + "\nDecrypted data - " + decryptedBlocks + "\n\n");

				} else if (length > 0) {
					encryptedData = new byte[length];
					din.readFully(encryptedData, 0, encryptedData.length);
					txtadisplay.setText(
							txtadisplay.getText() + "Data received from user - " + new String(encryptedData) + "\n");
					encryptedData = Base64.getDecoder().decode(encryptedData);

					xorData = XorDataNKey.XorDataWithKeyAtIndex(encryptedData, key, TableGen.getIndexes());
					decryptedData = XorDataNKey.XorDataWithKey(xorData, key);

					System.out.println("Message received from user - " + decryptedData);
					txtadisplay.setText(txtadisplay.getText() + "Decrypted data - " + decryptedData + "\n\n");
				} else {
					System.out.println("Session over");
					txtadisplay.setText(txtadisplay.getText() + "Session over\n\n");
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				receiverSocket.close();
			} catch (Exception e) {
			}
		}

	}

	public static void main(String args[]) {
		Receiver_UI_DEMO ser = new Receiver_UI_DEMO();

		ser.addWindowListener(new WindowAdapter() {
			@SuppressWarnings("unused")
			public void WindowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		ser.runReceiver();
	}

}
