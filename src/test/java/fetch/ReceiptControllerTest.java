package fetch;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class ReceiptControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testProcessAndGetPoints() {
        final Receipt receipt = Receipt.builder()
                .retailer("Costco")
                .purchaseDate("2022-12-31")
                .purchaseTime("13:45")
                .total("25.00")
                .items(List.of(
                        Item.builder().shortDescription("Frozen Pizza").price("12.50").build(),
                        Item.builder().shortDescription("Ice Cream").price("5.00").build()
                ))
                .build();

        final HttpRequest<Receipt> request = HttpRequest.POST("/receipts/process", receipt);
        final HttpResponse<Map<String, Object>> response = client.toBlocking()
                .exchange(request, Argument.mapOf(String.class, Object.class));
        assertEquals(200, response.getStatus().getCode());
        assertTrue(response.body().containsKey("id"));

        final String id = (String) response.body().get("id");
        final HttpRequest<?> getRequest = HttpRequest.GET("/receipts/" + id + "/points");
        final HttpResponse<Map> getResponse = client.toBlocking().exchange(getRequest, Map.class);

        assertEquals(200, getResponse.getStatus().getCode());
        assertTrue(getResponse.body().containsKey("points"));
    }

    @Test
    void testInvalidReceiptReturnsBadRequest() {
        final Receipt badReceipt = new Receipt();

        final HttpRequest<Receipt> request = HttpRequest.POST("/receipts/process", badReceipt);

        final HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().retrieve(request)
        );

        assertEquals(400, exception.getStatus().getCode());
        final String body = exception.getResponse().getBody(String.class).orElse("");
        assertTrue(body.contains("The receipt is invalid"));
    }
}
