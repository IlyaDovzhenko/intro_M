
public class MainClass {
    public static void main(String[] args) {
        long num = 100;
        System.out.println("Hello");
        DBConnector.getConnection(num);
        System.out.println("Summa = " + DBConnector.MyHandler.count);
        long testSumma = 0;
        for(int i = 1; i <= num; i++) {
            testSumma = testSumma + i;
        }
        System.out.println("Test Summa = " + testSumma);
    }
}
