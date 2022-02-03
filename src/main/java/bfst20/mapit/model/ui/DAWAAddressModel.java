package bfst20.mapit.model.ui;

import java.util.List;

import bfst20.mapit.model.AutoComplete;
import bfst20.mapit.model.drawables.points.POI;
import bfst20.mapit.model.io.IO;
import impl.org.controlsfx.autocompletion.SuggestionProvider;

public class DAWAAddressModel {
    List<AutoComplete> addresses;
    SuggestionProvider<AutoComplete> addressesSuggestion;

    public DAWAAddressModel(IO io) {
        // load addresses
        this.addresses = io.loadAddresses();
        this.addressesSuggestion = SuggestionProvider.create(this.addresses);

    }

    public List<AutoComplete> getAddresses() {
        return addresses;
    }

    public SuggestionProvider<AutoComplete> getAddressesSuggestion() {
        return addressesSuggestion;
    }

    public void setAddressesSuggestion(SuggestionProvider<AutoComplete> addressesSuggestion) {
        this.addressesSuggestion = addressesSuggestion;
    }

    public void updataDAWAList(POI poi) {
        this.addresses.remove(poi);
        this.addressesSuggestion.clearSuggestions();
        this.addressesSuggestion.addPossibleSuggestions(this.addresses);
    }

    public void addPOItoAutoFillList(POI poi) {
        this.addresses.add(poi);
        this.addressesSuggestion.clearSuggestions();
        this.addressesSuggestion.addPossibleSuggestions(this.addresses);
    }

}