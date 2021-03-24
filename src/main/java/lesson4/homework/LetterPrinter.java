package lesson4.homework;

import java.util.ArrayList;
import java.util.Collections;

//1. Создать три потока, каждый из которых выводит определенную
// букву (A, B и C) 5 раз (порядок – ABСABСABС). Используйте wait/notify/notifyAll.
public class LetterPrinter {
    private final Object monitor = new Object();
    private final int count;
    private final ArrayList<Character> args;
    private volatile Character currentLetter;

    public LetterPrinter(int count, Character... characters) {
        this.count = count;
        args = new ArrayList<>();
        Collections.addAll(args, characters);
        currentLetter = args.get(0);
    }

    public ArrayList<Character> getArgs() {
        return args;
    }

    public void printLetter(int indexForThread, char letterForThread) {
        synchronized (monitor) {
            try {
                for (int i = 0; i < count; i++) {
                    while (currentLetter != letterForThread) {
                        monitor.wait();
                    }
                    System.out.print(letterForThread);
                    int currentIndex = (indexForThread + 1) % args.size();
                    currentLetter = args.get(currentIndex);
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
