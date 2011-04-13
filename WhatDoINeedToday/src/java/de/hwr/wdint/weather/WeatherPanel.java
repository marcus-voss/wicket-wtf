/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.weather;
import de.hwr.wdint.*;
import de.hwr.wdint.location.Location;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.model.PropertyModel;

/**
 * WeatherPanel
 * Panel, das den Ratschlag enthält
 * @author Marcus Voss
 */
public final class WeatherPanel extends Panel
{
    private Location userLocation;
    private Advice advice;
    

    /**
     * Main
     * @param id
     */
    public WeatherPanel(String id)
    {
        super (id);

        setOutputMarkupId(true);

        add(title);

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        try
        {
            //Freift auf die userLocation in der BasePage zu
            userLocation = ((BasePage)this.getPage()).getUserLocation();
            //Erstellt die Instanz von Advice für den Ratschlag für den User und fügt diesem dem Label "advice" zu
            advice = new Advice(userLocation);
            
            labelAdviceText = advice.getAdvice();
            add(labelAdvice);
            //add(new Label("advice", advice.getAdvice()));
            
            labelTitleText = "Wettervorhersage für " + userLocation.getCity();
            
            add(title);

        }
        catch (Exception ex)
        {
            Logger.getLogger(HeaderPanel.class.getName()).log(Level.SEVERE, null, ex);
            labelAdviceText = ex.toString();
            add(labelAdvice);
        }
    }

    //Variable für das Datenmodell des Labels des Pannels
    private String labelTitleText = "Wettervorhersage";

    public String getLabelText() {
        return labelTitleText;
    }

    public void setLabelText(String labelText) {
        this.labelTitleText = labelText;
    }

    Label title = new Label("labelTitle", new PropertyModel(this,"labelTitleText")){
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }

    };
    
    //Variable für das Datenmodell des Labels des Pannels
    private String labelAdviceText = "";

    public String getLabelAdviceText() {
        return labelAdviceText;
    }

    public void setLabelAdviceText(String labelText) {
        this.labelAdviceText = labelText;
    }

    Label labelAdvice = new Label("advice", new PropertyModel(this,"labelAdviceText")){
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }

    };


}
