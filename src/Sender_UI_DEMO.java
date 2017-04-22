import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Sender_UI_DEMO {

	private JFrame frame;
	private JTextField txtEnterMessage;
	private JTextField txtLocalhost;
	private JLabel lblDisconnected;
	private static Socket socket;
	private static int port;
	private static String host;
	public static byte[] key;
	public static byte[] xorData;
	public static String data;
	public static int[] indexes;
	public static byte[] publicKey;
	public static int[][] table;
	public static DataOutputStream dOut;
	public static DataInputStream din;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton btnConnect;
	private static List<String> dataBlocks;

	public static List<String> splitIntoBlocks(String text, int size) {

		List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			ret.add(text.substring(start, Math.min(text.length(), start + size)));
		}
		return ret;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sender_UI_DEMO window = new Sender_UI_DEMO();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Sender_UI_DEMO() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Sender UI");
		frame.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		frame.setBounds(500, 500, 800, 560);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		txtEnterMessage = new JTextField();
		txtEnterMessage.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		txtEnterMessage.setBounds(19, 472, 535, 39);
		frame.getContentPane().add(txtEnterMessage);
		txtEnterMessage.setColumns(10);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		btnNewButton.addActionListener(new ActionListener() {
			private byte[] encryptedData;

			public void actionPerformed(ActionEvent e) {
				try {
					data = txtEnterMessage.getText();

					if (data.equalsIgnoreCase("exit")) {
						dOut.writeInt(-1);
						dOut.flush();
						btnNewButton.setEnabled(false);
						btnConnect.setEnabled(true);
						txtEnterMessage.setText("");
						txtEnterMessage.setEnabled(false);
						lblDisconnected.setText("Disconnected");
						lblDisconnected.setForeground(Color.RED);
						textArea.setText("");
						JOptionPane.showMessageDialog(null, "Session Over");
						System.out.println("Session over");
					} else {
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
							encryptedBlocks = Base64.getEncoder().encode(encryptedBlocks);
							dOut.writeInt(encryptedBlocks.length);
							dOut.write(encryptedBlocks);
							dOut.flush();

							textArea.setText(textArea.getText() + "Sent : " + txtEnterMessage.getText()
									+ "\n\nEncrypted as : " + new String(encryptedBlocks) + "\n\n");
							txtEnterMessage.setText("");
						} else {

							xorData = XorDataNKey.XorDataWithKey(data, key);
							encryptedData = XorDataNKey.XorDataWithKeyAtIndex(xorData, key, TableGen.getIndexes());
							encryptedData = Base64.getEncoder().encode(encryptedData);

							dOut.writeInt(encryptedData.length);
							dOut.write(encryptedData);
							dOut.flush();

							textArea.setText(textArea.getText() + "Sent : " + txtEnterMessage.getText()
									+ "\nEncrypted as : " + new String(encryptedData) + "\n\n");
							txtEnterMessage.setText("");
						}

						System.out.println("Encrypted data sent");
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(566, 473, 210, 39);
		frame.getContentPane().add(btnNewButton);

		JLabel lblServerIp = new JLabel("Receiver IP :");
		lblServerIp.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblServerIp.setBounds(19, 16, 142, 39);
		frame.getContentPane().add(lblServerIp);

		txtLocalhost = new JTextField();
		txtLocalhost.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(173, 13, 381, 39);
		frame.getContentPane().add(txtLocalhost);
		txtLocalhost.setColumns(10);

		btnConnect = new JButton("Connect");
		btnConnect.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		btnConnect.setBounds(574, 14, 202, 38);
		frame.getContentPane().add(btnConnect);

		lblDisconnected = new JLabel("Disconnected");
		lblDisconnected.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblDisconnected.setForeground(Color.RED);
		lblDisconnected.setBounds(19, 67, 757, 39);
		frame.getContentPane().add(lblDisconnected);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(19, 110, 757, 350);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		scrollPane.setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setForeground(Color.BLACK);
		textArea.setText("");
		textArea.setBackground(Color.WHITE);

		btnConnect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					RSA rsa = new RSA();
					String shost = txtLocalhost.getText();

					host = shost;
					port = 25000;

					InetAddress address = InetAddress.getByName(host);
					socket = new Socket(address, port);

					// Get Public Key
					din = new DataInputStream(socket.getInputStream());
					int length = din.readInt();
					if (length > 0) {
						publicKey = new byte[length];
						din.readFully(publicKey, 0, publicKey.length);
					}
					System.out.println("Step 1: Public key received from receiver");
					textArea.append("Step 1: Public key received from receiver\n");

					// Generate session key and table
					key = KeyGen.generateKey();
					table = TableGen.getTable();
					TableGen.generateShiftingKeys();

					// Encryption of symmetric key using public serverKey
					dOut = new DataOutputStream(socket.getOutputStream());
					byte[] encryptedKey = rsa.encrypt(key, RSA.convertBytesToPublicKey(publicKey));
					dOut.writeInt(encryptedKey.length);
					dOut.write(encryptedKey);
					dOut.flush();
					System.out.println("Step 2: Encrypted key sent to the receiver");
					textArea.append("Step 2: Encrypted key sent to the receiver\n");

					// Encryption of shifting keys
					byte[] encryptedShiftingKeys = rsa.encrypt(TableGen.getShiftingKeys(),
							RSA.convertBytesToPublicKey(publicKey));
					dOut.writeInt(encryptedShiftingKeys.length);
					dOut.write(encryptedShiftingKeys);
					System.out.println("Step 3: Encrypted Shifting keys sent to the receiver");
					textArea.append("Step 3: Encrypted Shifting keys sent to the receiver\n");

					// Table sent
					for (int i = 0; i < TableGen.matrix.length; i++)
						for (int j = 0; j < TableGen.matrix.length; j++)
							dOut.writeInt(TableGen.matrix[i][j]);

					dOut.flush();
					System.out.println("Step 4: Table sent");
					textArea.append("Step 4: Table sent\n\n");

					lblDisconnected.setText("Connected");
					lblDisconnected.setForeground(Color.GREEN);
					btnConnect.setEnabled(false);
					txtEnterMessage.setEnabled(true);
					btnNewButton.setEnabled(true);
					txtEnterMessage.setText("");
					txtEnterMessage.setEnabled(true);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});

	}
}
