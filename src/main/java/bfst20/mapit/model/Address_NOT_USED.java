package bfst20.mapit.model;

import java.util.regex.*;

public class Address_NOT_USED {
    public final String street, housenumber, houseletter, floor, side, postcode, city;

    private Address_NOT_USED(String _street, String _housenumber, String _houseletter, String _floor, String _side, String _postcode, String _city) {
        street = _street;
        housenumber = _housenumber;
        houseletter = _houseletter;
        floor = _floor;
        side = _side;
        postcode = _postcode;
        city = _city;
    }

    public String toString() {
        return street + " " + housenumber + housenumber + ", " + floor + " " + side + "\n" + postcode + " " + city;
    }

    private static String irrRegularRoads = "10. Februar Vej|10. Juli Vej|2. Tværvej|3. Tværvej|4. Maj Stræde|5. Junivej|6.Julivej|7.Tangvej|8.Tangvej";
    private static String sides = "th|tv|mf|TH|TV|MF|tH|tV|mF|Th|Tv|Mf|til højre|til venstre|midt for";

    static String regex = "^(?i)(?<street>" + irrRegularRoads + "|[0-9a-zæøå\\. ]*?){0,1}( |,){0,}(?<housenumber>[0-9]{1,3})?(?<houseletter>[a-zæøå]{0,1})*?(( |,){0,}(?<floor>[0-9]+)( |,)*(?<side>" + sides + "))?( |,)*?(?<postcode>[0-9]{4})?( |,)*?(?<city>[a-zæøå \\.]{2,}){0,1};$";
    static Pattern pattern = Pattern.compile(regex);

    public static Address_NOT_USED parse(String input) {
        var matcher = pattern.matcher(input +";");
        if (matcher.matches()) {
            return new Builder()
                    .street(matcher.group("street"))
                    .houseletter(matcher.group("houseletter"))
                    .housenumber(matcher.group("housenumber"))
                    .floor(matcher.group("floor"))
                    .side(matcher.group("side"))
                    .postcode(matcher.group("postcode"))
                    .city(matcher.group("city"))
                    .build();
        } else {
            throw new IllegalArgumentException("Cannot parse: " + input);
        }
    }

    public static class Builder {
        private String street, housenumber, houseletter, floor, side, postcode, city;

        public Builder street(String _street) {
            street = _street;
            if(street != null){
                street = (_street).trim();
            }
            return this;
        }

        public Builder housenumber(String _housenumber) {
            housenumber = _housenumber;
            if(housenumber != null){
                housenumber = (_housenumber).trim();
            }
            return this;
        }

        public Builder houseletter(String _houseletter) {
            houseletter = _houseletter;
            if(houseletter != null){
                houseletter = (_houseletter).trim();
            }
            return this;
        }

        public Builder floor(String _floor) {
            floor = _floor;
            if(floor != null){
                floor = (_floor).trim();
            }
            return this;
        }

        public Builder side(String _side) {
            side = _side;
            if(side != null){
                side = (_side).trim();
            }
            return this;
        }

        public Builder postcode(String _postcode) {
            postcode = _postcode;
            if(postcode != null){
                postcode = (_postcode).trim();
            }
            return this;
        }

        public Builder city(String _city) {
            city = _city;
            if(city != null){
                city = (_city).trim();
            }
            return this;
        }

        public Address_NOT_USED build() {
            return new Address_NOT_USED(street, housenumber, houseletter, floor, side, postcode, city);
        }
    }

    public static String toString(Address_NOT_USED parsed){
        String output ="";
        if(parsed.street != null){
            output += parsed.street + " ";
        }

        if(parsed.housenumber != null){
            output += parsed.housenumber;
        }

        if(parsed.houseletter != null){
            output += parsed.houseletter;
        }

        if(parsed.floor != null){
            output += ", " + parsed.floor + " ";
        }

        if(parsed.side != null){
            output += parsed.side;
        }

        if(parsed.postcode != null){
            output += " " + parsed.postcode + " ";
        }

        if(parsed.city != null){
            output += parsed.city;
        }

        output += ".\n\n";
        return output;
    }
}