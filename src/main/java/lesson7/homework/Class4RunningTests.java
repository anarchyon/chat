package lesson7.homework;

import lesson7.homework.annotation.AfterSuite;
import lesson7.homework.annotation.BeforeSuite;
import lesson7.homework.annotation.Test;
import lesson7.homework.tests.TestClass;
import lesson7.homework.tests.TestClass1;
import lesson7.homework.tests.TestClass2;
import lesson7.homework.tests.TestClass3;

import java.lang.reflect.Method;
import java.util.*;

public class Class4RunningTests {
    public static final int BEFORE_SUITE_PRIORITY = 11;
    public static final int AFTER_SUITE_PRIORITY = 0;
    public static final int MINIMAL_PRIORITY_FOR_TEST_METHOD = 1;
    public static final int MAXIMAL_PRIORITY_FOR_TEST_METHOD = 10;

    public static void main(String[] args) {
        try {
            start(TestClass1.class);
            start(TestClass3.class);
            start(TestClass2.class);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void start(Class<? extends TestClass> testClass) {
        System.out.println("Run tests form class: " + testClass.getName());
        Method[] methods = testClass.getDeclaredMethods();
        Map<Integer, List<Method>> testMethods = new TreeMap<>(Comparator.reverseOrder());
        prepareMapOfMethods(methods, testMethods);
        if (testMethods.size() == 0) {
            System.out.println("This class haven't test methods");
            return;
        }
        checkMap(testMethods);
        runTests(testClass, testMethods);
    }

    private static void checkMap(Map<Integer, List<Method>> testMethods) {
        if (testMethods.get(BEFORE_SUITE_PRIORITY).size() > 1 || testMethods.get(AFTER_SUITE_PRIORITY).size() > 1) {
            throw new RuntimeException("BeforeSuite and AfterSuite tests must be in a single instance");
        }
    }

    private static void prepareMapOfMethods(Method[] methods, Map<Integer, List<Method>> testMethods) {
        for (Method method : methods) {
            if (method.getDeclaredAnnotations().length == 0) continue;
            Test annotation = method.getAnnotation(Test.class);
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                putMethodInMap(testMethods, BEFORE_SUITE_PRIORITY, method);
            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                putMethodInMap(testMethods, AFTER_SUITE_PRIORITY, method);
            } else if (annotation != null) {
                int priority = annotation.priority();
                if (priority < MINIMAL_PRIORITY_FOR_TEST_METHOD) {
                    priority = MINIMAL_PRIORITY_FOR_TEST_METHOD;
                } else if (priority > MAXIMAL_PRIORITY_FOR_TEST_METHOD) {
                    priority = MAXIMAL_PRIORITY_FOR_TEST_METHOD;
                }
                putMethodInMap(testMethods, priority, method);
            }
        }
    }

    private static void runTests(Class<? extends TestClass> testClass, Map<Integer, List<Method>> testMethods) {
        try {
            TestClass test = testClass.newInstance();
            for (List<Method> methods : testMethods.values()) {
                for (Method method : methods) {
                    method.invoke(test, (Object[]) null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void putMethodInMap(Map<Integer, List<Method>> map, Integer key, Method value) {
        map.compute(key, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            v.add(value);
            return v;
        });
    }

}
