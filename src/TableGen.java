import java.util.Random;

public class TableGen {

	public static int[] shiftingKeys = null;
	public static int[][] matrix = new int[16][16];

	public static void setMatrix(int[][] array) {
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 16; j++)
				matrix[i][j] = array[i][j];
	}

	public static void columnShiftRight(int count) {
		while (count > 0) {
			for (int i = 0; i < 16; i++) {
				int m = 16;
				int temp = matrix[i][m - 1];
				for (int k = m - 1; k >= 1; k--) {
					matrix[i][k] = matrix[i][k - 1];
				}
				matrix[i][0] = temp;
			}
			count--;
		}
	}

	public static void rowShiftDown(int count) {

		while (count > 0) {
			for (int i = 0; i < 16; i++) {
				int m = 16;
				int temp = matrix[m - 1][i];
				for (int k = m - 1; k >= 1; k--) {
					matrix[k][i] = matrix[k - 1][i];
				}
				matrix[0][i] = temp;
			}
			count--;
		}
	}

	public static int[][] getTable() {

		Random r = new Random();
		int Low = 0;
		int High = 10;

		// Generate Random Table.
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {

				int Result = r.nextInt(High - Low) + Low;
				matrix[i][j] = Result;
			}
		}
		return matrix;
	}

	public static void setShiftingKeys(String key) {
		shiftingKeys = new int[key.length()];
		for (int i = 0; i < key.length(); i++) {
			shiftingKeys[i] = key.charAt(i) - '0';
		}
	}

	public static void generateShiftingKeys() {
		Random r = new Random();
		int low = 0;
		int high = 10;
		shiftingKeys = new int[6];
		for (int i = 0; i < 6; i++) {
			shiftingKeys[i] = r.nextInt(high - low) + low;
		}
	}

	public static byte[] getShiftingKeys() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < shiftingKeys.length; i++)
			sb.append(shiftingKeys[i]);
		return sb.toString().getBytes();
	}

	// Getting indexes
	public static int[] getIndexes() {

		for (int i = 0; i < shiftingKeys.length; i++) {
			if (i % 2 == 0)
				columnShiftRight(shiftingKeys[i]);
			else
				rowShiftDown(shiftingKeys[i]);
		}

		int indexes[] = new int[128];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j += 2) {
				indexes[k] = matrix[i][j] * 10 + matrix[i][j + 1];
				k++;
			}
		}
		return indexes;
	}

	public static void main(String[] args) {
		generateShiftingKeys();
		System.out.println(new String(getShiftingKeys()));
		setShiftingKeys(new String(getShiftingKeys()));

		for (int i = 0; i < shiftingKeys.length; i++)
			System.out.print(shiftingKeys[i]);
		// System.out.println(getIndexes(getTable()));
	}
}
