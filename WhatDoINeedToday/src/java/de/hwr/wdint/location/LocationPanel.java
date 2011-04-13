/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.location;
import de.hwr.wdint.BasePage;
import de.hwr.wdint.HomePage;
import java.util.Locale;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author juliusollesch
 */
public final class LocationPanel extends Panel {

    //Variable für das Datenmodell des Labels des Pannels
    private String labelTitleText = "";

    public String getLabelText() {
        return labelTitleText;
    }

    public void setLabelText(String labelText) {
        this.labelTitleText = labelText;
    }

   

    //Label des Pannels ist ein AjaxEditableLabel, so dass Benutzer ihren Aufenthaltsort überschreiben können
    AjaxEditableLabel title = new AjaxEditableLabel("title", new PropertyModel(this, "labelTitleText")) {
        //Methode onSubmit wird überschrieben
        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            if (target != null) {
                super.onSubmit(target);
                if (labelTitleText != null) {
                    if (!labelTitleText.equalsIgnoreCase("")) {
                        
                        
                        
                        System.out.println(labelTitleText);

                        //Falls etwas eigegeben wurde, setze userLocation mit der Eingabe
                        ((BasePage)this.getPage()).setUserLocation(labelTitleText);
                        ((BasePage)this.getPage()).update(target);
                    }

                    //zeige die Region des Benutzers im Label an
                    labelTitleText = ((BasePage)this.getPage()).getUserLocation().getUrbanArea();
                    //aktualisiere das Label in der HTML
                    target.addComponent(title);
                    //Rufe Methode zum aktualisieren der Tabelle mit Events auf
                    //Der Benutzer bekommt so nach Eingabe eine aktualisierte Ansicht
                    //updateDataViewWithEvents(target);

                } else {
                    //Falls Text Null war setze den text des Labels wieder zurück auf die Region des Benutzers
                    labelTitleText = ((BasePage)this.getPage()).getUserLocation().getUrbanArea();
                }
            }
        }

        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);

            setEscapeModelStrings(true);
            
        }
    };



    public LocationPanel(String id) {
        super (id);
        //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
        this.setOutputMarkupId(true);
        //Setze den Text des Labels mit der Region
  

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        labelTitleText = ((HomePage)this.getPage()).getUserLocation().getUrbanArea();
        add(title);
    }

    
}
