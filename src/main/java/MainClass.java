
public class MainClass {
    public static void main(String[] args) {
        int num = 0;
        try {
            num = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }
        DBConnector.getConnection(num);
        System.out.println("Summa = " + DBConnector.MyHandler.count);
    }
}
