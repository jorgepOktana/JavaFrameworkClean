package Utils;


public class Main {
    public static void main(String[] args) throws Exception {

    }

    public static String convertMillisToTime(long Millis) {
        String convert = String.format("Total Time Taken: %d hour(s), %d minute(s), and %d second(s)",
                Millis / (1000 * 60 * 60), (Millis % (1000 * 60 * 60)) / (1000 * 60), ((Millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        System.out.println(convert);
        return convert;
    }

}
