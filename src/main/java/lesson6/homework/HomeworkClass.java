package lesson6.homework;

public class HomeworkClass {
    public static final int SPECIFIED_NUMBER = 4;

    public int[] getArrayAfterSpecifiedNumber(int[] array) {
        int index = -1;
        if (array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == SPECIFIED_NUMBER) {
                    index = i + 1;
                }
            }
        }
        if (index == -1) {
            throw new RuntimeException(String.format("Массив не содержит заданного числа: %s", SPECIFIED_NUMBER));
        } else if (index == array.length) {
            return null;
        }
        int length = array.length - index;
        int[] resultArray = new int[length];
        int j = 0;
        for (int i = index; i < array.length; i++) {
            resultArray[j++] = array[i];
        }
        return resultArray;
    }
}
