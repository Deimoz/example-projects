public class SumLong{

    public static void main(String[] args) {
        long sum = 0;
		for (int j = 0; j < args.length; j++) {
			boolean flag = false;
			int i = 0;
			int startIndex = 0;
            int endindex = 0;
			while (i < args[j].length()){
				if (!Character.isWhitespace(args[j].charAt(i)) && !flag) {
					startindex = i;
					endindex = i;
					flag = true;
				}
				if (((Character.isWhitespace(args[j].charAt(i))) || (i + 1 == args[j].length()))  && (flag))  {
					endindex = i;
					if ((i + 1 == args[j].length()) && (!Character.isWhitespace(args[j].charAt(i)))) {
						endindex++;
					}
					flag = false;
					if (endindex - startindex > 0) {
						sum += Long.parseLong(args[j].substring(startindex, endindex));
					}
				}
				i++;
			}
		}
        System.out.println(sum);
    }
}