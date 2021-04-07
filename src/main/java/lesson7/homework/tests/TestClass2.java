package lesson7.homework.tests;

import lesson7.homework.annotation.AfterSuite;
import lesson7.homework.annotation.BeforeSuite;
import lesson7.homework.annotation.Test;

public class TestClass2 implements TestClass{
    @BeforeSuite
    public void testMethod1() {
        System.out.println("testMethod with priority = 5");
    }

    @Test(priority = 1)
    public void testMethod2() {
        System.out.println("testMethod with priority = 1");
    }

    @Test(priority = 7)
    public void testMethod3() {
        System.out.println("testMethod with priority = 7");
    }

    @Test(priority = 10)
    public void testMethod4() {
        System.out.println("testMethod with priority = 10");
    }

    @Test(priority = 3)
    public void testMethod5() {
        System.out.println("testMethod with priority = 3");
    }

    @BeforeSuite
    public void firstTest() {
        System.out.println("BeforeSuite test");
    }

    @AfterSuite
    public void lastTest() {
        System.out.println("AfterSuite test");
    }

}
