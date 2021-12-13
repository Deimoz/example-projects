public class Sum{

    public static void main(String[] args) {
        int sum = 0;
        int i;
        StringBuilder sub = new StringBuilder();
		for (int j = 0; j < args.length; j++) {
			i = 0;
				while (i < args[j].length()) {
					while (((args[j].charAt(i) >= '0') && (args[j].charAt(i) <= '9')) || (args[j].charAt(i) == '-')) {
						sub.append(Character.toString(args[j].charAt(i)));
				if (i + 1 < args[j].length()) {
						i++;
				} else {
				break;
				};
					}
					if (sub.length() > 0) {
						sum += Integer.parseInt(sub.toString());
						sub = new StringBuilder();
					}
					i++;
				}
		}
        System.out.println(sum);
    }
}