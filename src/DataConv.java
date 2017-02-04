
public class DataConv {
	public static String dataConv(String data) {
		String s = data;
		byte[] bytes = s.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes){
			int val = b;
			for (int i = 0; i < 8; i++)
			{
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		System.out.println("'" + s + "' to binary: " + binary);
		return binary.toString();
	}
}
