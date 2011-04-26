/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.style;

import de.hwr.wdint.weather.WeatherPanel;
import java.sql.Time;
import java.util.Date;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.model.PropertyModel;


/**
 *
 * @author dirk
 */
public class Style extends Panel{

    private String sunriseTime = "";
    private String sunsetTime = "";
    private WeatherPanel weatherPanel;
    /**
     * Main
     * @param id
     */
    public Style(String id, WeatherPanel wP)
    {
        super (id);
        this.weatherPanel = wP;
        
        setOutputMarkupId(true);

        //add(title);

    }
    /*
     * Testpanel-Inhalt
     */
    /*
    private String labelContentText = "";
    Label title = new Label("labelTitle", "StylePanel"){
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }

    };
    Label content = new Label("labelContent", new PropertyModel(this,"labelContentText"))
    {
        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }
    };
*/
    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        try
        {
            sunriseTime = weatherPanel.getSunriseTime();
            sunsetTime = weatherPanel.getSunsetTime();
            //labelContentText = sunriseTime;

            Time now = new Time(new Date().getTime());

            Time todayMidnight = new Time(now.getTime() - Time.valueOf(now.toString()).getTime());

            //korrigiere Unterschied Sommer- / Winterzeit
/*            if(! (todayMidnight.toString().equalsIgnoreCase("00:00:00")) )
                todayMidnight.setTime(todayMidnight.getTime() - Time.valueOf("01:00:00").getTime());

 */

            //Wandle die Zeiten in Millisekunden um
            Time sunrise = new Time(Time.valueOf(sunriseTime+":00").getTime() + todayMidnight.getTime());
            Time sunset = new Time(Time.valueOf(sunsetTime+":00").getTime() + todayMidnight.getTime());

            //Vergleiche jetzigen Zeitpunkt, ob er zwischen sunrise und sunset liegt
            if(sunrise.before(now) && sunset.after(now))
                add(new StyleSheetReference("stylesheet", Style.class, "style_light.css"));
            else
                add(new StyleSheetReference("stylesheet", Style.class, "style.css"));

            //add(content);

        
        }
        catch (Exception ex)
        {
            
        }
    }
}
