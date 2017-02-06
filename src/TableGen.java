import java.util.Random;

public class TableGen {

	public static int[][] columnShiftRight(int count, int[][] array) {
		
		while(count > 0) {
			for(int i = 0; i < 16; i++){
				int m = 16;
				int temp = array[i][m - 1];
				for (int k = m - 1; k >= 1; k--) {
					array[i][k] = array[i][k - 1];
				}
				array[i][0] = temp;
			}
			count--;
		}
		return array;
	}
	
	public static int[][] rowShiftDown(int count, int[][] array) {
		
		while(count > 0) {
			for(int i = 0; i < 16; i++){
				int m = 16;
				int temp = array[m - 1][i];
				for (int k = m - 1; k >= 1; k--) {
					array[k][i] = array[k - 1][i];
				}
				array[0][i] = temp;
			}
			count--;
		}
		return array;
	}
	

	public static int[][] getTable() {
		int matrix[][] = new int[16][16];

		Random r = new Random();
		int Low = 0;
		int High = 10;

		//Generate Random Table.
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {

				int Result = r.nextInt(High - Low) + Low;
				matrix[i][j] = Result;
			}
		}
		return matrix;
	}
	
	
	//Getting indexes
	public static int[] getIndexes(int matrix[][]){
		
		Random r = new Random();
		int Low = 0;
		int High = 10;
		
		int Result = r.nextInt(High - Low) + Low;
		matrix = rowShiftDown(Result, matrix);
		
		Result = r.nextInt(High - Low) + Low;
		matrix = columnShiftRight(Result, matrix);
		
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
}
