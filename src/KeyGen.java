import java.util.Random;

public class KeyGen {
	public static byte[] generateKey() {
		byte[] b = new byte[128];
		new Random().nextBytes(b);
		return b;
	}
	public static void main(String args[]) {
		generateKey();
	}
}