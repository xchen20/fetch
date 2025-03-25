package fetch;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointsCalculatorTest {

    private final PointsCalculator calculator = new PointsCalculator();

    @Test
    void testCalculatePointsWithSampleReceipt() {
        final Receipt receipt = Receipt.builder()
                .retailer("Target")
                .purchaseDate("2022-01-01")
                .purchaseTime("14:33")
                .total("35.00")
                .items(List.of(
                        Item.builder().shortDescription("Mountain Dew 12PK").price("6.49").build(), // length of 17` => not %3
                        Item.builder().shortDescription("Emils Cheese Pizza").price("12.25").build() // length of 18 =>  %3
                ))
                .build();

        int expectedPoints = 6     // "Target" -> 6 alphanumerics
                + 50               // round total
                + 25               // total divisible by 0.25
                + 5                // 2 items => 5 points
                + 3                // 12.25 * 0.2 = 2.45, round up to 3
                + 6                // odd day
                + 10;              // time bonus

        assertEquals(expectedPoints, calculator.calculatePoints(receipt));
    }

    @Test
    void testPointsWithItemDescriptionMultipleOf3() {
        final Receipt receipt = Receipt.builder()
                .retailer("Walmart")
                .purchaseDate("2022-02-02")
                .purchaseTime("13:00")
                .total("10.00")
                .items(List.of(
                        Item.builder().shortDescription("abc").price("10.00").build() // length of 3 => %3
                ))
                .build();

        int expectedPoints = 7 // "Walmart" -> 7 alphanumerics
                + 50           // round total
                + 25           // total divisible by 0.25
                + 2;           // 10 * 0.2 = 2

        assertEquals(expectedPoints, calculator.calculatePoints(receipt));
    }

    @Test
    void testNoItemsStillScoresRetailerPoints() {
        final Receipt receipt = Receipt.builder()
                .retailer("Amazon")
                .purchaseDate("2022-04-04")
                .purchaseTime("10:00")
                .total("1.00")
                .items(List.of())
                .build();

        int expectedPoints = 6     // "Amazon" -> 6 alphanumerics
                + 50               // round total
                + 25;              // total divisible by 0.25
        assertEquals(expectedPoints, calculator.calculatePoints(receipt));
    }

    @Test
    void testNonRoundTotalNotDivisibleBy25() {
        final Receipt receipt = Receipt.builder()
                .retailer("Costco")
                .purchaseDate("2022-04-06")
                .purchaseTime("13:00")
                .total("33.33")
                .items(List.of(
                        Item.builder().shortDescription("Something").price("1.00").build(), // length of 9 => %3
                        Item.builder().shortDescription("Else").price("2.00").build()
                ))
                .build();

        int expectedPoints = 6 // "Costco" -> 6 alphanumerics
                + 5           // 2 items => 5
                + 1;          // 1.00 * 0.2 = 0.2, round up to 1

        assertEquals(expectedPoints, calculator.calculatePoints(receipt));
    }

    @Test
    void testMultipleItemsWithMixedDescriptionLengths() {
        final Receipt receipt = Receipt.builder()
                .retailer("TestStore")
                .purchaseDate("2022-05-15")
                .purchaseTime("14:01")
                .total("20.00")
                .items(List.of(
                        Item.builder().shortDescription("abc").price("10.00").build(),    // length of 3 => %3
                        Item.builder().shortDescription("abcd").price("6.00").build(),    // length of 4 => not %3
                        Item.builder().shortDescription("abcdef").price("5.00").build()   // length of 6 => %3
                ))
                .build();

        int expectedPoints = 9   // "TestStore" -> 9 alphanumerics
                + 50             // round total
                + 25             // total divisible by 0.25
                + 5              // 3 items => 1 pair => 5 points
                + 2 + 1          // 10.00 * 0.2 = 2, 5.00 * 0.2 = 1
                + 6              // odd day
                + 10;            // time bonus

        assertEquals(expectedPoints, calculator.calculatePoints(receipt));
    }
}
