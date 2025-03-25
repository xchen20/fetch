package fetch;

import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Singleton class responsible for calculating reward points based on
 * a variety of rules applied to receipt data.
 */
@Singleton
@Log4j2
public class PointsCalculator {

    /**
     * Calculates reward points for a given receipt based on the following rules:
     * <ul>
     *     <li>1 point for every alphanumeric character in the retailer name</li>
     *     <li>50 points if the total is a round dollar amount (ends in .00)</li>
     *     <li>25 points if the total is a multiple of 0.25</li>
     *     <li>5 points for every two items on the receipt</li>
     *     <li>For items with a short description length divisible by 3,
     *         add 20% of the item price (rounded up) to the points</li>
     *     <li>6 points if the purchase day is an odd-numbered day</li>
     *     <li>10 points if the purchase time is between 14:00 and 15:59 inclusive</li>
     * </ul>
     *
     * @param receipt the receipt containing purchase and item data
     * @return the total points earned
     */
    public int calculatePoints(final Receipt receipt) {
        int points = 0;

        // Rule 1: Alphanumeric characters in retailer name
        int rule1Points = receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();
        points += rule1Points;
        log.debug("Rule 1: Retailer '{}', added {} points", receipt.getRetailer(), rule1Points);

        // Rule 2: Round dollar total (ends with .00)
        if (receipt.getTotal().endsWith(".00")) {
            points += 50;
            log.debug("Rule 2: Total '{}' is round dollar, added 50 points", receipt.getTotal());
        }

        // Rule 3: Total is a multiple of 0.25
        final double total = Double.parseDouble(receipt.getTotal());
        if (total % 0.25 == 0) {
            points += 25;
            log.debug("Rule 3: Total '{}' is divisible by 0.25, added 25 points", total);
        }

        // Rule 4: 5 points for every 2 items
        int rule4Points = (receipt.getItems().size() / 2) * 5;
        points += rule4Points;
        log.debug("Rule 4: {} items => {} points", receipt.getItems().size(), rule4Points);

        // Rule 5: 20% of item price if description length is multiple of 3
        for (final Item item : receipt.getItems()) {
            final String desc = item.getShortDescription().trim();
            if (desc.length() % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                int itemPoints = (int) Math.ceil(price * 0.2);
                points += itemPoints;
                log.debug("Rule 5: Item '{}' (${}) → description length % 3 == 0, added {} points", desc, price, itemPoints);
            }
        }

        // Rule 6: 6 points if purchase day is odd
        final LocalDate date = LocalDate.parse(receipt.getPurchaseDate());
        if (date.getDayOfMonth() % 2 == 1) {
            points += 6;
            log.debug("Rule 6: Purchase date '{}' is an odd day, added 6 points", date);
        }

        // Rule 7: 10 points if time is between 14:00 and 15:59
        final LocalTime time = LocalTime.parse(receipt.getPurchaseTime());
        if (time.isAfter(LocalTime.of(14, 0).minusSeconds(1)) && time.isBefore(LocalTime.of(16, 0))) {
            points += 10;
            log.debug("Rule 7: Purchase time '{}' is between 14:00 and 15:59, added 10 points", time);
        }

        log.debug("Total points for receipt '{}': {}", receipt, points);
        return points;
    }
}
