import com.google.common.io.Resources;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import static io.restassured.RestAssured.given;
/*
     1- Get Token+
     2- CreateBooking+
     3- CheckCreationBooking+
     4- UpdateBooking+
     5- CheckUpdateBooking+
     6- DeleteBooking+
     7- CheckDelete+  */

public class ApiTest {
    @BeforeMethod
    public void beforeTest() throws IOException {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }
    @Test
    public void restfulBookerTest() throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long totalprice = timestamp.getTime();
        String bookingId = createBooking(totalprice);
        checkCreateBooking(bookingId);
        updateBooking(totalprice,bookingId);
        checkUpdateBooking(bookingId);
        deleteBooking(bookingId);
        checkDeleteBooking(bookingId);
    }
    public String getToken() throws IOException {
        URL file = Resources.getResource("token.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject( myJson );
        Response response =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .body(json.toString())
                        .when()
                        .post("/auth")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        String token = response.getBody().jsonPath().getString("token");
        System.out.println(token);
        return token;
    }
    public String createBooking(Long totalprice) throws IOException {
        URL file = Resources.getResource("bookingBody.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject( myJson );
        json.put("totalprice", totalprice);
        json.put("depositpaid", false);
        json.getJSONObject("bookingdates").put("checkin" , "2000-11-11");
        Response bodyresponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .body(json.toString())
                        .when()
                        .post("/booking")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        String bookingId = bodyresponse.getBody().jsonPath().getString("bookingid");
//        String bookingId = js.get("bookingid").toString();
        System.out.println("Booking Id:" + bookingId);
        return bookingId;
    }
    public void checkCreateBooking(String bookingId){
        Response checkUpdate =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .when()
                        .get("/booking/"+ bookingId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        checkUpdate.getBody().prettyPeek() ;
//        System.out.println("CREATEBOOKING: " + checkUpdate);
    }
    public void updateBooking(long totalprice, String bookingId ) throws IOException {
        URL file = Resources.getResource("bookingBody.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject( myJson );
        json.put("depositpaid", true);
        json.put("additionalneeds", "Sonaksamyemegi");
        Response bodyResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .accept("application/json")
                        .cookie("token" , getToken())
//                        .auth()
//                        .preemptive()
//                        .basic( "admin" , "password123")
                        .body(json.toString())
                        .when()
                        .put("/booking/" + bookingId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        bodyResponse.getBody().prettyPeek() ;
    }
    public void checkUpdateBooking(String bookingId ){
        Response checkUpdate =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .when()
                        .get("/booking/"+ bookingId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
    }
    public void deleteBooking(String bookingId) throws IOException {
        given()
                .contentType("application/json; charset=UTF-8")
                .cookie("token", getToken())
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);
    }
    public void checkDeleteBooking(String bookingId){
        given()
                .contentType("application/json; charset=UTF-8")
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(404);
    }
}
