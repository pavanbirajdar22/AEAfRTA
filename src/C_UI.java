import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class C_UI {

	private JFrame frame;
	private JTextField txtEnterMessage;
	private JTextField textField;
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					C_UI window = new C_UI();
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
	public C_UI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Client UI");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		txtEnterMessage = new JTextField();
		txtEnterMessage.setText("Enter Message to send");
		txtEnterMessage.setBounds(6, 243, 320, 29);
		frame.getContentPane().add(txtEnterMessage);
		txtEnterMessage.setColumns(10);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					data=txtEnterMessage.getText();

					if(data.equalsIgnoreCase("exit")){
						dOut.writeInt(-1);
						dOut.flush();
						btnNewButton.setEnabled(false);
						txtEnterMessage.setText("");
						txtEnterMessage.setEnabled(false);
						lblDisconnected.setText("Disconnected");
						lblDisconnected.setForeground(Color.RED);
						JOptionPane.showMessageDialog(null,"Session Over");
						System.out.println("Session over");
					}
					else{
						// Send indexes
						indexes = TableGen.getIndexes(table);
						dOut.writeInt(indexes.length);
						for (int ee : indexes) dOut.writeInt(ee);
						dOut.flush();
						System.out.println("Step 2: Indexes sent to the server");


						//Send encrypted Data
						xorData = XorDataNKey.XorDataWithKey(data,key);
						xorData = XorDataNKey.XorDataWithKeyAtIndex(xorData,key,indexes);
						dOut.writeInt(xorData.length); // write length of the message
						dOut.write(xorData);
						dOut.flush();

						textArea.setText(textArea.getText()
								+ "Sent : " + txtEnterMessage.getText() 
								+ "\nEncrypted as : " + new String(Base64.getEncoder().encode(xorData)) + "\n\n");
						txtEnterMessage.setText("");

						System.out.println("Encrypted data sent");
					}
				}
				catch (Exception exception){
					exception.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(338, 244, 112, 29);
		frame.getContentPane().add(btnNewButton);

		JLabel lblServerIp = new JLabel("Server IP :");
		lblServerIp.setBounds(6, 16, 106, 23);
		frame.getContentPane().add(lblServerIp);

		textField = new JTextField();
		textField.setBounds(124, 13, 320, 28);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(6, 51, 438, 29);
		frame.getContentPane().add(btnConnect);

		lblDisconnected = new JLabel("Disconnected");
		lblDisconnected.setForeground(Color.RED);
		lblDisconnected.setBounds(6, 92, 438, 16);
		frame.getContentPane().add(lblDisconnected);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 120, 438, 111);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		scrollPane.setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setForeground(Color.BLACK);
		textArea.setText("");
		textArea.setBackground(Color.WHITE);

		btnConnect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try
				{	
					RSA rsa = new RSA();
					String shost = textField.getText();			

					host = shost;
					port = 25000;

					InetAddress address = InetAddress.getByName(host);
					socket = new Socket(address, port);

					//Get Public Key
					din = new DataInputStream(socket.getInputStream());
					int length = din.readInt();                 
					if(length > 0) {
						publicKey = new byte[length];
						din.readFully(publicKey, 0, publicKey.length);
					}

					// Generate session key and table
					key = KeyGen.generateKey();
					table = TableGen.getTable();

					//Encryption of symmetric key using public serverKey
					dOut = new DataOutputStream(socket.getOutputStream());
					byte[] encryptedKey = rsa.encrypt(key, RSA.convertBytesToPublicKey(publicKey));
					dOut.writeInt(encryptedKey.length);
					dOut.write(encryptedKey);
					dOut.flush();
					System.out.println("Step 1: Encrypted key sent to the server");
					lblDisconnected.setText("Connected");
					lblDisconnected.setForeground(Color.GREEN);
					txtEnterMessage.setEnabled(true);
					btnNewButton.setEnabled(true);
				}
				catch (Exception exception){
					exception.printStackTrace();
				}
			}
		});

	}
}
