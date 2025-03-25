package fetch;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller that exposes REST endpoints for processing receipts and retrieving points.
 * <p>
 * Secured as anonymous to allow public access without authentication.
 */
@Controller("/receipts")
@Secured(SecurityRule.IS_ANONYMOUS)
public class ReceiptController {

    private final PointsCalculator calculator;

    /**
     * Thread-safe in-memory map to store receipt IDs and their associated points.
     */
    private final Map<String, Integer> receiptPoints = new ConcurrentHashMap<>();

    /**
     * Constructor injection of the PointsCalculator.
     *
     * @param calculator the calculator responsible for computing receipt points
     */
    @Inject
    public ReceiptController(final PointsCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Endpoint to process a receipt and calculate reward points.
     * <p>
     * Stores the result and returns a unique ID for later retrieval.
     *
     * @param receipt the submitted receipt (validated against constraints)
     * @return HTTP 200 with receipt ID if successful, or HTTP 400 if input is invalid
     */
    @Post("/process")
    public HttpResponse<Map<String, String>> processReceipt(@Body @Valid final Receipt receipt) {
        try {
            int points = calculator.calculatePoints(receipt);
            final String id = UUID.randomUUID().toString();
            receiptPoints.put(id, points);
            return HttpResponse.ok(Collections.singletonMap("id", id));
        } catch (Exception e) {
            // Handle unexpected parsing or validation issues
            return HttpResponse.badRequest(Collections.singletonMap("message", "The receipt is invalid. Please verify input."));
        }
    }

    /**
     * Endpoint to retrieve points for a previously processed receipt by ID.
     *
     * @param id the UUID string identifying a previously processed receipt
     * @return HTTP 200 with points if found, or HTTP 404 if ID does not exist
     */
    @Get("/{id}/points")
    public HttpResponse<Map<String, ?>> getPoints(@PathVariable final String id) {
        final Integer points = receiptPoints.get(id);
        if (Objects.isNull(points)) {
            return HttpResponse.notFound(Collections.singletonMap("message", "No receipt found for that ID."));
        }
        return HttpResponse.ok(Collections.singletonMap("points", points));
    }
}
