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
import java.net.InetSocketAddress;
import java.net.Proxy;
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


        //Set <<system-wide>> Proxy Settings :-(
		Proxy proxy = getProxy();
		if(proxy != null) {

			System.getProperties().put("http.proxyHost", ((InetSocketAddress) proxy.address()).getHostName());
			System.getProperties().put("http.proxyPort", ((InetSocketAddress) proxy.address()).getPort());

		}
        //Proxy Settings Ende
    }

	/**
	 * gets the proxy settings for HWR
	 * @return
	 */
	public Proxy getProxy() {

		Proxy proxy = null;
		//HWR Proxy - beim Deployen nächste Zeile auskommentieren
		//proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("194.94.23.231", 80));
		return proxy;

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
