package Utils;


import icix.Modules.IcixTasks;

public class Main {
    public static void main(String[] args) throws Exception {
        IcixTasks.addProcessedRelatedRequestsInMap("Hello");
        IcixTasks.addProcessedRelatedRequestsInMap("Test");
        System.out.println(IcixTasks.getProcessedRelatedRequestsFromMap().toString());
        IcixTasks.addProcessedRelatedRequestsInMap("Test3");
        System.out.println(IcixTasks.getProcessedRelatedRequestsFromMap().toString());


    }

    public static String convertMillisToTime(long Millis) {
        String convert = String.format("Total Time Taken: %d hour(s), %d minute(s), and %d second(s)",
                Millis / (1000 * 60 * 60), (Millis % (1000 * 60 * 60)) / (1000 * 60), ((Millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        System.out.println(convert);
        return convert;
    }

}
