/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hwr.wdint.location;

import de.hwr.wdint.BasePage;
import de.hwr.wdint.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author juliusollesch
 */
public final class LocationPanel extends Panel {

    //Variable für das Datenmodell des Labels des Pannels
    private String labelUserInputCityText = "";

    public String getLabelUserInputCityText() {
        return labelUserInputCityText;
    }

    public void setLabelUserInputCityText(String labelText) {
        this.labelUserInputCityText = labelText;
    }
    //Label des Pannels ist ein AjaxEditableLabel, so dass Benutzer ihren Aufenthaltsort überschreiben können
    AjaxEditableLabel userInputCity = new AjaxEditableLabel("userInputCity", new PropertyModel(this, "labelUserInputCityText")) {
        //Methode onSubmit wird überschrieben

        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            if (target != null) {
                super.onSubmit(target);
                if (labelUserInputCityText != null) {
                    if (!labelUserInputCityText.equalsIgnoreCase("")) {


                        System.out.println(labelUserInputCityText);

                        //Falls etwas eigegeben wurde, setze userLocation mit der Eingabe
                        ((BasePage) this.getPage()).setUserLocation(labelUserInputCityText);
                        ((BasePage) this.getPage()).update(target);
                    }

                    //zeige die Region des Benutzers im Label an
                    labelUserInputCityText = ((BasePage) this.getPage()).getUserLocation().getCity();
                    labelUserUrbanAreaText = ((HomePage) this.getPage()).getUserLocation().getUrbanArea();

                    //aktualisiere die Label in der HTML
                    target.addComponent(userInputCity);
                    target.addComponent(userUrbanArea);


                } else {
                    //Falls Text Null war setze den text des Labels wieder zurück auf die Region des Benutzers
                    labelUserInputCityText = ((BasePage) this.getPage()).getUserLocation().getCity();
                    labelUserUrbanAreaText = ((HomePage) this.getPage()).getUserLocation().getUrbanArea();
                    
                    //aktualisiere die Label in der HTML
                    target.addComponent(userInputCity);
                    target.addComponent(userUrbanArea);
                }
            }
        }

        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);

            setEscapeModelStrings(true);

        }
    };
    private String labelUserUrbanAreaText = "";

    public String getLabelUserUrbanArea() {
        return labelUserUrbanAreaText;
    }

    public void setLabelUserUrbanArea(String labelUserUrbanArea) {
        this.labelUserUrbanAreaText = labelUserUrbanArea;
    }
    Label userUrbanArea = new Label("userUrbanArea", new PropertyModel(this, "labelUserUrbanAreaText"));

    public LocationPanel(String id) {
        super(id);
        //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
        this.setOutputMarkupId(true);
        //Setze den Text des Labels mit der Region


    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        labelUserInputCityText = ((HomePage) this.getPage()).getUserLocation().getCity();
        labelUserUrbanAreaText = ((HomePage) this.getPage()).getUserLocation().getUrbanArea();
        add(userInputCity);
        add(userUrbanArea);
    }
}
