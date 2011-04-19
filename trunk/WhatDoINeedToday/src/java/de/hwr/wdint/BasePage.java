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

        /*
         * Initialisierung der Panels auf der BasePage
         * und aktivieren.
         */

        locationPanel = new LocationPanel("locationPanel");
        rssPanel = new RSSPanel("rssPanel");
        weatherPanel = new WeatherPanel("weatherPanel");
        facebookPanel = new FacebookPanel("facebookPanel");
        



        add(locationPanel);
        add(rssPanel);
        add(weatherPanel);
        add(facebookPanel);

        /*
         ToDo: Stylesheet Tageszeit-abhängig ändern oder modifizieren
         *
         *
         */
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



        /*
         * Aktualisiert die BasePage mit AJAX
         */
    public void update(AjaxRequestTarget target){
        //Falls AJAX Request
        if (target!= null){

           /*
            * Weather Panel überschreibt onBeforeRender
            * und initialisiert die Werte sofort neu wenn es
            * nochmal hinzugefügt wird
            */
           weatherPanel.setLabelText("Update");
           target.addComponent(weatherPanel);

           /*
            * RSSPanel muss neu initialisiert werden
            * Benutzer soll wieder die Auswahl haben ob Weggehen oder Zuhause bleiben
            * Da wir im RSS Panel die Auswahl nicht Zwischenspeichern sondern über Links
            * dynamisch Inhalte hereinladen ist dies die beste Lösung.
            *
            * Unschön ist, das Wicket nicht erkennt wenn Komponenten mit replaceWith ausgetauscht werden
            * Dies ist ähnlich dem Verhalten, dass eine Liste erst geleert werden muss
            * damit das Wicket Datenmodell die Änderung mitbekommt
            */
           RSSPanel tempRSS = new RSSPanel("rssPanel");
           rssPanel.replaceWith(tempRSS);
           rssPanel = tempRSS;
           target.addComponent(rssPanel);

        }//Falls normaler Request
        else{
            //Seite neuladen
            this.setResponsePage(this.getPage());
        }
    }
}
