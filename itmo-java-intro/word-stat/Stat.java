import java.util.ArrayList;
import java.util.List;

public class Stat {
    private List<Integer> lines = new ArrayList<>();
    private List<Integer> numInLines = new ArrayList<>();

    public void addPosition(int line, int number) {
        lines.add(line);
        numInLines.add(number);
    }

    public int size() {
        return lines.size();
    }

    public String getPosition(int i) {
        return lines.get(i) + ":" + numInLines.get(i);
    }
}