/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hwr.wdint.location;

import de.hwr.wdint.location.Regions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.StringAutoCompleteRenderer;
import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

/**
 *
 * @author juliusollesch
 */
public class LocationAutoCompleteBehavior extends AutoCompleteBehavior {

    @Override
    protected Iterator getChoices(String input) {
        try {
            List subList = new ArrayList();

            WebService.setUserName("wdint");

            //Bekomme eine Liste Städte
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            //Sprache ist Deutsch - wie ganz WDINT
            searchCriteria.setLanguage("de");

            //alles was mit der Eingabe des Benutzers anfängt
            searchCriteria.setNameStartsWith(input);

            //Hole maximal 10 Einträge , macht weniger Traffic und ist übersichtlicher
            searchCriteria.setMaxRows(10);
            //und in Deutschland ist
            searchCriteria.setCountryCode("DE");
            //und eine Stadt ist
            searchCriteria.setFeatureCode("PPL");

            ToponymSearchResult searchResult = WebService.search(searchCriteria);


            for (Toponym toponym : searchResult.getToponyms()) {
                String cityName = toponym.getName();
                //Zeige dem Benutzer maximal 5 Einträge an
                if(subList.size() == 5){
                    break;
                }
                //Manchmal werden doppelte Einträge geliefert - die müssen manuell aussortiert werden
                if (!subList.contains(cityName)) {
                    subList.add(cityName);
                }
            }
            return subList.iterator();
        } catch (Exception ex) {
            Logger.getLogger(LocationAutoCompleteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList().iterator();
        }
    }

    public LocationAutoCompleteBehavior() {
        super(new StringAutoCompleteRenderer());
    }
}
