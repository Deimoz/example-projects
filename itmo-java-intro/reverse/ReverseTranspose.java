import java.io.*;
import java.util.Arrays;

public class ReverseTranspose {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int[][] array = new int[1000_000][10];
        int[] size = new int[1000_000];
        int maxLength = 0;
        while (in.hasNextLine()) {
            int count = 0;

            Scanner numbers = new Scanner(in.nextLine());
            while (numbers.hasNextInt()) {
                if (size[count] >= array[count].length) {
                    array[count] = Arrays.copyOf(array[count], (array[count].length * 3)/2 + 1);
                }
                array[count][size[count++]++] = numbers.nextInt();
            }
            if (count > maxLength) {
                maxLength = count;
            }
        }

        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < size[i]; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }
}