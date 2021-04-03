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

    @CsvSource({
            "new int[]{0,1,2,3,4,5,6,7}, new int[]{5,6,7}",
            "new int[]{44,4,1,2,4,2,2,1,4,5}, new int[]{5}",
            "new int[]{4,4,4,4}, null",
            "null, null"
    })
    @ParameterizedTest
    void getArrayAfterSpecifiedNumber1(int[] source, int[] result) {
        Assertions.assertArrayEquals(result, homeworkClass.getArrayAfterSpecifiedNumber(source));
    }

    @Test
    void getArrayAfterSpecifiedNumber2() {
        Assertions.assertThrows(RuntimeException.class,
                () -> homeworkClass.getArrayAfterSpecifiedNumber(new int[]{0, 1, 2, 3, 5}));
    }
}