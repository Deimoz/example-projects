import java.io.IOException;
import java.util.Arrays;

public class ReverseTranspose {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int[][] array = new int[1000_000][];
        int[][] res = new int[1000_000][];
        int[] allNumbers = new int[1000_000];
        int numOflines = 0;
        int maxLength = 0;
        while (in.hasNextLine()) {
            int count = 0;

            Scanner numbers = new Scanner(in.nextLine());
            while (numbers.hasNextInt()) {
                allNumbers[count] = numbers.nextInt();
                res[count] = Arrays.copyOf(res[count], count + 1);
                res[count][res[count++].length - 1] = numOflines;
            }
            if (count > maxLength) {
                maxLength = count;
            }
            array[numOflines++] = Arrays.copyOf(allNumbers, count);
        }
        
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < res[i].length; j++) {
                System.out.print(array[j][i] + " ");
            }
            System.out.println();
        }
        
        
    }
}