package lesson4.homework;

//1. Создать три потока, каждый из которых выводит определенную
// букву (A, B и C) 5 раз (порядок – ABСABСABС). Используйте wait/notify/notifyAll.
public class Test1 {
    private final Object monitor = new Object();

    public static void main(String[] args) {
        LetterPrinter letterPrinter = new LetterPrinter(5, 'A', 'B', 'C', 'D', 'E', 'F', '3', 'z');
        for (int i = 0; i < letterPrinter.getArgs().size(); i++) {
            int currentIndex = i;
            char currentLetter = letterPrinter.getArgs().get(i);
            new Thread(() -> letterPrinter.printLetter(currentIndex, currentLetter)).start();
        }
    }
}
