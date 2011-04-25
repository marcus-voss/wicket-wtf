/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.weather;
import de.hwr.wdint.*;
import de.hwr.wdint.location.Location;
import java.net.URL;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * WeatherPanel
 * Panel, das den Ratschlag enthält
 * @author Marcus Voss
 */
public final class WeatherPanel extends Panel
{
    private Location userLocation;
    private Advice advice;

    //Enthält die Wetterkonditionen
    private String conditions;
    private String iconUrl;

    private String sunsetTime;
    private String sunriseTime;

    /**
     * Main
     * @param id
     */
    public WeatherPanel(String id)
    {
        super (id);

        setOutputMarkupId(true);

        add(title);
        add(new StaticImage("icon", new PropertyModel(this, "iconUrl")));

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        try
        {
            //Freift auf die userLocation in der BasePage zu
            userLocation = ((BasePage)this.getPage()).getUserLocation();
                        
            //Erstellt eine Instanz von WeatherInfo und holt die aktuellen Wetterkonditionen
            WeatherInfo weatherInfo = new WeatherInfo(userLocation);

            //advice
            advice = new Advice(weatherInfo.getConditions());
            labelAdviceText = advice.getAdvice();
            add(labelAdvice);

            //conditions
            labelConditionText = weatherInfo.getConditions();
            add(labelCondition);

            //High
            labelHighText = weatherInfo.getHigh();
            add(labelHigh);

            //Low
            labelLowText = weatherInfo.getLow();
            add(labelLow);

            //add(new Label("advice", advice.getAdvice()));
            
            labelTitleText = "Wettervorhersage für " + userLocation.getCity();
            
            add(title);

           iconUrl = weatherInfo.getIcon();
           
           sunriseTime = weatherInfo.getSunrise();
           sunsetTime = weatherInfo.getSunset();

           ((BasePage)this.getPage()).getUserLocation().setSunsetTime(sunsetTime);
           ((BasePage)this.getPage()).getUserLocation().setSunriseTime(sunriseTime);

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
    private String labelLowText = "";
    private String labelHighText = "";
    private String labelConditionText = "";
    
    public String getLabelAdviceText() {
        return labelAdviceText;
    }

    public void setLabelAdviceText(String labelText) {
        this.labelAdviceText = labelText;
    }

    Label labelAdvice = new Label("advice", new PropertyModel(this,"labelAdviceText"))
    {
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }

    };

    Label labelHigh = new Label("low", new PropertyModel(this, "labelLowText"))
    {
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }

    };

    Label labelLow = new Label("high", new PropertyModel(this,"labelHighText"))
    {
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }

    };

    Label labelCondition = new Label("condition", new PropertyModel(this,"labelConditionText"))
    {
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }

    };

    public String getSunriseTime() {
        return sunriseTime;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

   



}
