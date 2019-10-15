import static org.junit.Assert.*;
import org.junit.*;

public class HappyTest {

    @Ignore
    @Test
    public void happyStep_shouldReturnOne_ifCalledWithOne() {
        assertEquals(1, Happy.happyStep(1));
    }

    @Ignore
    @Test
    public void happyStep_shouldReturnFour_ifCalledWithTwo() {
        assertEquals(4, Happy.happyStep(2));
    }

    @Ignore
    @Test
    public void happyStep_shouldReturnNine_ifCalledWithThree() {
        assertEquals(9, Happy.happyStep(3));
    }

    @Ignore
    @Test
    public void happyStep_shouldReturnFive_ifCalledWithTwelve() {
        assertEquals(5, Happy.happyStep(12));
    }

    @Ignore
    @Test
    public void happyStep_shouldReturnOne_ifCalledWithTen() {
        assertEquals(1, Happy.happyStep(10));
    }

    @Ignore
    @Test
    public void happyStep_shouldReturnTwentyOne_ifCalledWithFourHundredTwentyOne() {
        assertEquals(21, Happy.happyStep(421));
    }

    @Ignore
    @Test
    public void happyStep_shouldReturnNintyOne_ifCalledWith123456() {
        assertEquals(91, Happy.happyStep(123456));
    }

    @Ignore
    @Test
    public void oneIsHappy() {
        assertEquals(true, Happy.isHappy(1));
    }

    @Ignore
    @Test
    public void fourIsNotHappy() {
        assertEquals(false, Happy.isHappy(4));
    }

    @Ignore
    @Test
    public void fiveIsNotHappy() {
        assertEquals(false, Happy.isHappy(5));
    }

    @Ignore
    @Test
    public void sevenIsHappy() {
        assertEquals(true, Happy.isHappy(7));
    }

    @Ignore
    @Test
    public void nineIsNotHappy() {
        assertEquals(false, Happy.isHappy(9));
    }

    @Ignore
    @Test
    public void tenIsHappy() {
        assertEquals(true, Happy.isHappy(10));
    }

    @Ignore
    @Test
    public void elevenIsHappy() {
        assertEquals(false, Happy.isHappy(11));
    }

    @Test
    public void digits_shouldReturnASingletonArray_ifCalledWithASingleDigit() {
        assertArrayEquals(new int[] { 4 }, Happy.digits(4));
    }

    @Test
    public void digits_shouldReturnAnArrayOfSizeTwo_ifCalledWithATwoDigitNumber() {
        assertArrayEquals(new int[] { 2, 4 }, Happy.digits(42));
    }

    @Test
    public void digits_shouldReturnAnArrayOfSizeFour_ifCalledWithAFourDigitNumber() {
        assertArrayEquals(new int[] { 1, 1, 7, 4 }, Happy.digits(4711));
    }

}