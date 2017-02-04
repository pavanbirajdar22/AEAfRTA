import java.util.Random;

public class KeyGen {
	public static String generateKey(String[] args) {
		Random r = new Random();
		int Low = 0;
		int High = 2;
		for(int i=0;i<1028;i++){
			int Result = r.nextInt(High-Low) + Low;
			System.out.print(Result);
		}
	}
}
