package bfst20.mapit;

import bfst20.mapit.model.Address_NOT_USED;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressTest {

        @DisplayName("Simple Address Test")
        @Test
        void simpleAddressTest() {
                Address_NOT_USED testAddress = Address_NOT_USED.parse("Nordens Plads 12B, 2000 Frederiksberg");
                assertEquals("Nordens Plads", testAddress.street); //street
                assertEquals("12", testAddress.housenumber); //housenumber
                assertEquals("B", testAddress.houseletter); //houseletter
                assertEquals("2000", testAddress.postcode); //postcode
                assertEquals("Frederiksberg", testAddress.city); //city
        }

        @DisplayName("Complete Address Test")
        @Test
        void completeAddressTest() {
                Address_NOT_USED testAddress = Address_NOT_USED.parse("Nordens Plads 12B, 4 th 2000 Frederiksberg");
                assertEquals("Nordens Plads", testAddress.street); //street
                assertEquals("12", testAddress.housenumber); //housenumber
                assertEquals("B", testAddress.houseletter); //houseletter
                assertEquals("4", testAddress.floor); //floor
                assertEquals("th", testAddress.side); //side
                assertEquals("2000", testAddress.postcode); //postcode
                assertEquals("Frederiksberg", testAddress.city); //city
        }

        @DisplayName("Only City Address Test")
        @Test
        void onlyCityAddressTest() {
                Address_NOT_USED testAddress = Address_NOT_USED.parse("Frederiksberg");
                assertEquals("Frederiksberg", testAddress.city); //city
        }

        @DisplayName("Only Postcode and City Address Test")
        @Test
        void onlyPostcodeAndCityAddressTest() {
                Address_NOT_USED testAddress = Address_NOT_USED.parse("2000 Frederiksberg");
                assertEquals("2000", testAddress.postcode); //postcode
                assertEquals("Frederiksberg", testAddress.city); //city
        }

        @DisplayName("Odd Road Address Test")
        @Test
        void oddRoadAddressTest() {
                Address_NOT_USED testAddress = Address_NOT_USED.parse("10. Februar Vej 12, 2000 Frederiksberg");
                assertEquals("10. Februar Vej", testAddress.street); //street
                assertEquals("12", testAddress.housenumber); //housenumber
                assertEquals("2000", testAddress.postcode); //postcode
                assertEquals("Frederiksberg", testAddress.city); //city
        }

}
