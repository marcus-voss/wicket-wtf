/*
 * WicketExamplePage.java
 *
 * Created on 29. März 2011, 11:15
 */
package de.hwr.wdint;

import de.hwr.wdint.facebook.FacebookPanel;
import de.hwr.wdint.location.Location;
import de.hwr.wdint.location.LocationPanel;
import de.hwr.wdint.rss.RSSPanel;
import de.hwr.wdint.weather.WeatherPanel;
import java.util.ArrayList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.util.string.Strings;

/** 
 *
 * @author juliusollesch
 * @version 
 */
public class BasePage extends WebPage {
    //Ergänzung der BasePage Klasse um ein Location Objekt
    //Dieses ist als Singleton ausgelegt

    private Location userLocation;

    public void setUserLocation(String userInput) {
        this.userLocation = new Location(userInput);

//        String markupID = weatherPanel.getMarkupId();
//        this.remove(weatherPanel);
//        weatherPanel = new WeatherPanel("weatcherPanel");
//        weatherPanel.setMarkupId(markupID);
//        weatherPanel.setOutputMarkupId(true);
//        this.add(weatherPanel);
//
//
//        markupID = rssPanel.getMarkupId();
//        this.remove(rssPanel);
//        rssPanel = new RSSPanel("rssPanel");
//        rssPanel.setMarkupId(markupID);
//        rssPanel.setOutputMarkupId(true);
//        this.add(rssPanel);

        //WeatherPanel temp = new WeatherPanel("weatherPanel");
        //weatherPanel.replaceWith(temp);
        //weatherPanel = temp;
        
    }

    public Location getUserLocation() {
        if (userLocation != null) {
            return userLocation;
        } else {
            userLocation = new Location();
            return userLocation;
        }

    }
    private LocationPanel locationPanel;
    private RSSPanel rssPanel;
    private WeatherPanel weatherPanel;
    private FacebookPanel facebookPanel;

    /**
     * Constructor
     */
    public BasePage() {
        this(null);
    }

    /**
     * Construct.
     * @param model
     */
    public BasePage(IModel model) {

        super(model);
        //final String packageName = getClass().getPackage().getName();
        //add(new HeaderPanel("mainNavigation", Strings.afterLast(packageName, '.')));
        locationPanel = new LocationPanel("locationPanel");
        rssPanel = new RSSPanel("rssPanel");
        weatherPanel = new WeatherPanel("weatherPanel");
        facebookPanel = new FacebookPanel("facebookPanel");
        weatherPanel.setOutputMarkupId(true);



        add(locationPanel);
        add(rssPanel);
        add(weatherPanel);
        add(facebookPanel);
        add(new StyleSheetReference("stylesheet", BasePage.class, "style.css"));


        //HWR-Proxy Settings :-(
        //System.getProperties().put("http.proxyHost", "194.94.23.231");
        //System.getProperties().put("http.proxyPort", "80");
        //HWR-Proxy  Settings Ende
    }


    public void update(AjaxRequestTarget target){
        if (target!= null){
           weatherPanel.setLabelText("Update");
           target.addComponent(weatherPanel);

           RSSPanel temp = new RSSPanel("rssPanel");
           rssPanel.replaceWith(temp);
           rssPanel = temp;
           
           rssPanel.setLabelText("Update");
           target.addComponent(rssPanel);
        }
    }
}
