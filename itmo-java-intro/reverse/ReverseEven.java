import java.io.IOException;
import java.util.Arrays;

public class ReverseEven {
	
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int[][] array = new int[1000_000][];
		int[] allNumbers = new int[1000_000];
        int numOflines = 0;
        while (in.hasNextLine()) {
			int count = 0;
            
			Scanner numbers = new Scanner(in.nextLine());
			while (numbers.hasNextInt()) {
				int a = numbers.nextInt();
				if (a % 2 == 0) {
					allNumbers[count] = a;
					count++;
				}
            }

            array[numOflines++] = Arrays.copyOf(allNumbers, count);
        }
		
        for (int i = numOflines - 1; i >= 0; i--){
            for (int j = array[i].length - 1; j >= 0; j--) {
                System.out.print(array[i][j]);
                if (j > 0) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}