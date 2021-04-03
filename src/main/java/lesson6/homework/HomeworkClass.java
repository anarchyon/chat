package lesson6.homework;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeworkClass {
    public static final int SPECIFIED_NUMBER = 4;

    public int[] getArrayAfterSpecifiedNumber(int[] array) throws RuntimeException {
        int index = -1;
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == SPECIFIED_NUMBER) {
                    index = i + 1;
                }
            }
        }
        if (index == -1) {
            throw new RuntimeException(String.format("Массив не содержит заданного числа: %s", SPECIFIED_NUMBER));
        } else if (index == array.length) {
            return new int[0];
        }
        int length = array.length - index;
        int[] resultArray = new int[length];
        int j = 0;
        for (int i = index; i < array.length; i++) {
            resultArray[j++] = array[i];
        }
        return resultArray;
    }

    public boolean isArrayConsistOnly(int[] array, int... args) {
        if (array == null || array.length == 0) return false;
        boolean condition1 = isArray1MembersContainsInArray2(array, args);
        boolean condition2 = isArray1MembersContainsInArray2(args, array);
        return condition1 && condition2;
    }

    private boolean isArray1MembersContainsInArray2 (int[] array1, int[] array2) {
        for (int n : array1) {
            boolean temp = false;
            for (int m : array2) {
                if (n == m) {
                    temp = true;
                    break;
                }
            }
            if (!temp) {
                return false;
            }
        }
        return true;
    }
}
