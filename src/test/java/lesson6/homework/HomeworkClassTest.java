package lesson6.homework;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class HomeworkClassTest {
    HomeworkClass homeworkClass;

    @BeforeEach
    void setUp() {
        homeworkClass = new HomeworkClass();
    }

    @Test
    void getArrayAfterSpecifiedNumber1() {
        Assertions.assertThrows(RuntimeException.class,
                () -> homeworkClass.getArrayAfterSpecifiedNumber(new int[]{0, 1, 2, 3, 5}));
    }

    @Test
    void getArrayAfterSpecifiedNumber2() {
        Assertions.assertThrows(RuntimeException.class,
                () -> homeworkClass.getArrayAfterSpecifiedNumber(new int[0]));
    }

    @Test
    void getArrayAfterSpecifiedNumber3() {
        Assertions.assertThrows(RuntimeException.class,
                () -> homeworkClass.getArrayAfterSpecifiedNumber(null));
    }

    @Test
    void getArrayAfterSpecifiedNumber4() {
        Assertions.assertArrayEquals(new int[]{3}, homeworkClass.getArrayAfterSpecifiedNumber(new int[]{0, 1, 4, 4, 4, 3}));
    }

    @Test
    void getArrayAfterSpecifiedNumber5() {
        Assertions.assertArrayEquals(new int[]{1, 2, 3}, homeworkClass.getArrayAfterSpecifiedNumber(new int[]{1, 2, 3, 40, 0, 2, 4, 1, 2, 3}));
    }

    @Test
    void getArrayAfterSpecifiedNumber6() {
        Assertions.assertArrayEquals(new int[]{1, 2}, homeworkClass.getArrayAfterSpecifiedNumber(new int[]{-4, -3, 2, 5, 7, 9, 0, 4, 1, 2}));
    }

    @Test
    void getArrayAfterSpecifiedNumber7() {
        Assertions.assertArrayEquals(new int[0], homeworkClass.getArrayAfterSpecifiedNumber(new int[]{4, 4, 4, 4}));
    }

    @Test
    void isArrayConsistOnly1() {
        Assertions.assertFalse(homeworkClass.isArrayConsistOnly(new int[0], 1, 4));
    }

    @Test
    void isArrayConsistOnly2() {
        Assertions.assertFalse(homeworkClass.isArrayConsistOnly(null, 1, 4));
    }

    @Test
    void isArrayConsistOnly3() {
        Assertions.assertTrue(homeworkClass.isArrayConsistOnly(new int[]{1, 1, 1, 4, 4, 4}, 1, 4));
    }

    @Test
    void isArrayConsistOnly4() {
        Assertions.assertFalse(homeworkClass.isArrayConsistOnly(new int[]{1, 1, 1, 1}, 1, 4));
    }

    @Test
    void isArrayConsistOnly5() {
        Assertions.assertTrue(homeworkClass.isArrayConsistOnly(new int[]{1, 1, 1, 1, 1}, 1));
    }

    @Test
    void isArrayConsistOnly6() {
        Assertions.assertFalse(homeworkClass.isArrayConsistOnly(new int[]{1, 2, 3, 1, 2, 3, 4}, 1, 2, 3, 4, 5));
    }

    @Test
    void isArrayConsistOnly7() {
        Assertions.assertTrue(homeworkClass.isArrayConsistOnly(new int[]{1, 2, 3, 4, 1, 2, 3, 4, 4, 4, 4}, 1, 2, 3, 4));
    }

    @Test
    void isArrayConsistOnly8() {
        Assertions.assertFalse(homeworkClass.isArrayConsistOnly(new int[]{4, 5}, 6, 7));
    }
}