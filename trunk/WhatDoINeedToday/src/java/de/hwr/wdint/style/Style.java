/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.style;

import de.hwr.wdint.weather.WeatherPanel;
import java.sql.Time;
import java.util.Calendar;
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

        //Test: add(title);

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

            //now -> derzeitige Uhrzeit und Datum
            Calendar now = Calendar.getInstance();

            //today -> derzeitiger Tag, aber 0 Uhr
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

            //korrigiere Unterschied Sommer- / Winterzeit
            if(today.get(Calendar.DST_OFFSET) != 0) {
                today.setTimeInMillis(today.getTimeInMillis() + today.get(Calendar.DST_OFFSET));
            }

            //Wandle die Zeiten in Millisekunden um

            Time sunrise = new Time(Time.valueOf(sunriseTime+":00").getTime() + today.getTimeInMillis());
            Time sunset = new Time(Time.valueOf(sunsetTime+":00").getTime() + today.getTimeInMillis());

            //Vergleiche jetzigen Zeitpunkt, ob er zwischen sunrise und sunset liegt
            if(sunrise.before(now.getTime()) && sunset.after(now.getTime()))
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
