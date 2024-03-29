package de.hwr.wdint.weather;

import de.hwr.wdint.location.Location;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Objekt, dass die relevanten Wetterinformationen von Wunderground.com holt
 * @author Marcus Voss
 */
public class WeatherInfo
{
    //Konstante, die den Stamm der URL für die WUnderground-API enthält
    private static final String WUNDERGROUND_URL_PARENT = "http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query=";

    //String, der die komplette URL inklusive der Location-Parameter enthält
    private String wundergroundURL;

    //relevante Wetterinformationen
    private String high;
    private String low;
    private String conditions;
    private String icon;
    private String sunset;
    private String sunrise;

    private String sunsetMinute;
    private String sunsetHour;
    private String sunriseMinute;
    private String sunriseHour;

    /**
     * Konstruktor
     * @param location
     */
    public WeatherInfo(Location location)
    {
       //erstellt die URL mit den relevanten Location-Parametern
       wundergroundURL = WUNDERGROUND_URL_PARENT + location.getLatitude() + "," + location.getLongitude().toString();

       //ruft Methode auf, die Wetterdaten extrahiert
       this.generateWeatherInfo(wundergroundURL);

    }
    
    /**
     * Methode, die die Wetterdaten extrahiert
     * @param wundergroundURL
     */
    private void generateWeatherInfo(String wundergroundURL)
    {
        try
        {
        //XML abrufen und in ein Document doc parsen
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        URL url = new URL(wundergroundURL);
        db = dbf.newDocumentBuilder();
        Document doc = db.parse(url.openStream());

        //Normalisieren
        doc.getDocumentElement().normalize();

        //Eine NodeList aus erstellen, die <simpleforecast> und dessen Kinder enthält
        NodeList simpleforecastNodes = doc.getElementsByTagName("simpleforecast");
        Element simpleforecastElement = (Element)simpleforecastNodes.item(0);

        //Eine NodeList erstellen, die alle <forecastday> enthält und das erste Element mit seinen Kindern als forecast für den aktuellen Tag weiter nutzen
        NodeList forecastdayNodes = simpleforecastElement.getElementsByTagName("forecastday");
        Element todaysForecastdayElement = (Element)forecastdayNodes.item(0);
        
        //High in Grad Celsius extrahieren
        NodeList highNodes = todaysForecastdayElement.getElementsByTagName("high");
        Element celsiusHighElement = (Element)highNodes.item(0);
        NodeList celsiusHighNodes = celsiusHighElement.getElementsByTagName("celsius");
        NodeList celsiusHighNodesChild = celsiusHighNodes.item(0).getChildNodes();
        high = celsiusHighNodesChild.item(0).getNodeValue().trim();
        
        //Low in Grad Celsius extrahieren
        NodeList lowNodes = todaysForecastdayElement.getElementsByTagName("low");
        Element celsiusLowElement = (Element)lowNodes.item(0);
        NodeList celsiusLowNodes = celsiusLowElement.getElementsByTagName("celsius");
        NodeList celsiusLowNodesChild = celsiusLowNodes.item(0).getChildNodes();
        low = celsiusLowNodesChild.item(0).getNodeValue().trim();
        
        //Conditions extrahieren
        NodeList conditionsNodes = todaysForecastdayElement.getElementsByTagName("conditions");
        NodeList conditionsNodesChild = conditionsNodes.item(0).getChildNodes();
        conditions = conditionsNodesChild.item(0).getNodeValue().trim();

        //Icons Contemporary URL extrahieren
        NodeList iconNodes = todaysForecastdayElement.getElementsByTagName("icon_set");
        Element iconContemporaryElement = (Element)iconNodes.item(7);
        NodeList iconContemporaryNodes = iconContemporaryElement.getElementsByTagName("icon_url");
        NodeList iconContemporaryNodesChild = iconContemporaryNodes.item(0).getChildNodes();
        icon = iconContemporaryNodesChild.item(0).getNodeValue().trim();

        //Sunset
        NodeList sunsetNodes = doc.getElementsByTagName("sunset");
        Element sunsetElement = (Element)sunsetNodes.item(0);

        NodeList sunsetHourNodes = sunsetElement.getElementsByTagName("hour");
        NodeList sunsetHourNodesChild = sunsetHourNodes.item(0).getChildNodes();
        sunsetHour = sunsetHourNodesChild.item(0).getNodeValue().trim();

        NodeList sunsetMinuteNodes = sunsetElement.getElementsByTagName("minute");
        NodeList sunsetMinuteNodesChild = sunsetMinuteNodes.item(0).getChildNodes();
        sunsetMinute = sunsetMinuteNodesChild.item(0).getNodeValue().trim();

        sunset = sunsetHour + ":" + sunsetMinute;

        //Sunrise
        NodeList sunriseNodes = doc.getElementsByTagName("sunrise");
        Element sunriseElement = (Element)sunriseNodes.item(0);

        NodeList sunriseHourNodes = sunriseElement.getElementsByTagName("hour");
        NodeList sunriseHourNodesChild = sunriseHourNodes.item(0).getChildNodes();
        sunriseHour = sunriseHourNodesChild.item(0).getNodeValue().trim();

        NodeList sunriseMinuteNodes = sunriseElement.getElementsByTagName("minute");
        NodeList sunriseMinuteNodesChild = sunriseMinuteNodes.item(0).getChildNodes();
        sunriseMinute = sunriseMinuteNodesChild.item(0).getNodeValue().trim();

        sunrise = sunriseHour + ":" + sunriseMinute;

        }
        catch (SAXException ex)
        {
            Logger.getLogger(WeatherInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(WeatherInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ParserConfigurationException ex)
        {
            Logger.getLogger(WeatherInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Getter für conditions
     * @return
     */
    public String getConditions()
    {
        return conditions;
    }

    /**
     * Getter für high
     * @return
     */
    public String getHigh()
    {
        return high;
    }

    /**
     * Getter für icon url
     * @return
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Getter für low
     * @return
     */
    public String getLow()
    {
        return low;
    }

    /**
     * Getter für sunrise
     * @return
     */
    public String getSunrise() {
        return sunrise;
    }

    /**
     * Getter für sunset
     * @return
     */
    public String getSunset() {
        return sunset;
    }


}
