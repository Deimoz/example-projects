import java.util.ArrayList;
import java.util.List;

public class BinarySearch {

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        List<Integer> array = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            array.add(Integer.parseInt(args[i]));
        }
        System.out.println(binarySearch(array, x));
    }

    public static int binarySearch(List<Integer> array, int x) {
        int l = 0, r = array.size();
        while (r - l > 1) {
            int mid = (r + l) / 2;
            if (array.get(mid) > x) {
                l = mid;
            } else {
                r = mid;
            }
        }
        return r;
    }
}