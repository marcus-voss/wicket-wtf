/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hwr.wdint.location;


import de.hwr.wdint.location.Regions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.StringAutoCompleteRenderer;

/**
 *
 * @author juliusollesch
 */
public class LocationAutoCompleteBehavior extends AutoCompleteBehavior {

    @Override
    protected Iterator getChoices(String input) {
        List subList = new ArrayList();
        for (Regions r : Regions.values()){
            if(r.name().toLowerCase().startsWith(input.toLowerCase())){
                subList.add(r);
            }
        }
        return subList.iterator();
    }

   

    public LocationAutoCompleteBehavior(){
       super(new StringAutoCompleteRenderer());
    }



}
