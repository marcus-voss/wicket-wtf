/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hwr.wdint.location;

import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author juliusollesch
 * @version 1.0
 * Objekt zur Speicherung des Breiten- & Längengrads, der Stadt und Region des Benutzers
 * 
 */
public class Location implements Serializable {
    /*
     *city ist die Stadt des Benutzers
     */

    private static final long serialVersionUID = 1L;
    String city;
    /*
     * @deprecated Wird derzeit nicht benötigt
     * postalCode ist die PLZ, wird derzeit nicht benutzt
     */
    String postalCode;
    /*
     * @see Regions
     * urbanArea speichert die Region des Benutzers
     */
    String urbanArea;
    /*
     * latitude speichert den Längengrad, String da alle WebServices ebenfalls Strings benutzen
     */
    String latitude;
    /*
     * longitude speichert den Breitengrad, String da alle WebServices ebenfalls Strings benutzen
     */
    String longitude;
    /*
     * sunsetTime und sunriseTime speichern die aktuellen Sonnenunter und aufgangszeiten
     */
    String sunsetTime;
    String sunriseTime;

    public String getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(String sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(String sunsetTime) {
        this.sunsetTime = sunsetTime;
    }
    /*
     * yahooAppID ist der Schlüssel der von yahoo für die Nutzung des Geolocation Services benötigt wird
     */
    final String yahooAppID = "PCvCMCfV34EK39cA9nSF8xTLKsi_b_iYNcPOvjVVRf20M_OOxfevRC0duEU_7kvwwXBq_EJN3klw844mtFAHiNqFJC9F2Yo-";

    /*
     * googleID ist der Schlüssel der von Google für die Nutzung des Geolocation Services benötigt wird
     */
    final String googleID = "ABQIAAAAkQnUeWkpWX2ghpH_HBNKThRNpo9uqOK-a7B_JqEqDo20wX8tIRRUoWgo4zGl7z27---7vloAp8XY8g";

    public String getLatitude() {
        return latitude;
    }

    private void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    private void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPostalCode() {
        return postalCode;
    }

    private void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getUrbanArea() {
        return urbanArea;
    }

    /*
     * @see urbanArea
     * @see Regions
     * @param city
     * Setter für urbanArea, geht die Regions durch und stellt die aktuell naheliegendste fest.
     */
    private void setUrbanArea() {


        double minDistance = Double.MAX_VALUE;
        String regionName = "";

        //Gehe durch die Regionen und bestimme die Nahegelegenste - Annahme: kreisförmige Region
        for (Regions r : Regions.values()) {
            double lat1 = r.getLatitude();
            double lon1 = r.getLongitude();

            double lat2 = Double.parseDouble(this.getLatitude());
            double lon2 = Double.parseDouble(this.getLongitude());

            double d = calculateDistance(lat1, lat2, lon1, lon2);
            if (d <= minDistance) {
                minDistance = d;
                regionName = r.name();
            }
        }
        String msg = "minDistance: " + minDistance + " Region: " + regionName;

        System.out.println(msg);
        this.urbanArea = regionName;
    }

    public String getCity() {
        return city;
    }

    private void setCity(String userInput) {
        this.city = userInput;
    }

    /*
     * @param userInput
     * Konstruktor für Location mit Eingaben des Benutzers
     *
     */
    public Location(String userInput) {
        //Yahoo war mal erste Wahl
        //getLocationInfoByUserInputUsingYahoo(userInput);

        //Ergebnisse von Google sind besser...
        getLocationInfoByUserInputUsingGoogle(userInput);
    }

    /*
     * Konstruktor für Location ohne Eingaben des Benutzers
     * Stellt die IP Adresse des Benutzers fest und versucht aus dieser den Aufenthaltsort zu erkennen.
     */
    public Location() {
        /*
        //Aktuellen WebRequest bekommen
        //IP Adresse des Benutzers feststellen
         */
        final ServletWebRequest req = (ServletWebRequest) RequestCycle.get().getRequest();


        /*
         * Versuche zunächst IP durch evt. Proxy herauszukriegen
         * Funktioniert bei Anfragen über Standard-Internetverbindungen
         * Bei UMTS Verbindungen sind die IPs vertauscht... Da dies aber nicht
         * erkennbar ist, fehlt derzeit ein Bugfix.
         */
        String remoteAddr = req.getHttpServletRequest().getHeader("X-FORWARDED-FOR");

        System.out.println("X-Forwarded-For: " + remoteAddr);
        System.out.println("Remote Address Header: " + req.getHttpServletRequest().getRemoteAddr());



        if (remoteAddr == null) {
            //falls kein Proxy da ist, nehme die Standard Host Adresse
            remoteAddr = req.getHttpServletRequest().getRemoteAddr();
        } else {
            //x-forwarded-for enthält unter Umständen mehrere IP Adressen kommasepariert

            int lastIndex = remoteAddr.lastIndexOf(",");
            //wir wollen nur den letzten
            if (lastIndex != -1) {
                remoteAddr = remoteAddr.substring(lastIndex + 1, remoteAddr.length());

                remoteAddr = remoteAddr.replaceAll(" ", "");
            }
        }


        System.out.println("IP-Adresse: " + remoteAddr);

        //Geolokalisierung mittels IP Adresse
        getLocationInfoByIP(remoteAddr);
    }
    /*
     * Berechnung der Entfernung zwischen zwei Längen- und Breitengraden
     */

    private double calculateDistance(double lat1, double lat2, double lon1, double lon2) {
        int R = 6371; // Radius der Erde in km

        //Berechnung nach Lamberts Formel: http://en.wikipedia.org/wiki/Geographical_distance#Pythagorean_formula_with_parallel_meridians

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    /*
     * Methode zur Ermittlung des Aufenthaltsorts nach Benutzereingabe mit Yahoo
     */
    private void getLocationInfoByUserInputUsingGoogle(String userInput) {
        try {
            Logger.getLogger(Location.class.getName()).log(Level.INFO, null, userInput);



            //Zeichen umwandeln, die Google nicht verwerten kann
            userInput = userInput.replaceAll("ä", "ae");
            userInput = userInput.replaceAll("ü", "ue");
            userInput = userInput.replaceAll("ö", "oe");
            userInput = userInput.replaceAll("ß", "ss");
            userInput = userInput.replaceAll(" ", "+");
            userInput = userInput.toLowerCase();

            System.out.println(userInput);
            // URL zu Google Geolocation API erstellen
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/xml?address=" + userInput + "&sensor=false&key=" + googleID);
            URLConnection urlConnection = url.openConnection();
            //Setzen der Request Header ist notwendig, damit Google uns auf Deutsch antwortet
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_7; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.205 Safari/534.16");
            urlConnection.setRequestProperty("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4");



            System.out.println("Google APi Url: " + url.toString());
            //SAX Builder verwenden, DocBuilder kommt mit Googles XML Format nicht klar
            SAXBuilder builder = new SAXBuilder();
            org.jdom.Document doc = builder.build(urlConnection.getInputStream());

            Element root = doc.getRootElement();
            System.out.println("Status response: " + root.getChildText("status"));
            if (root.getChildText("status").equalsIgnoreCase("OK")) {
                Element result = root.getChild("result");
                Element address = result.getChild("address_component");
                Element geometry = result.getChild("geometry");
                Element location = geometry.getChild("location");
                System.out.println("City response: " + address.getChildText("long_name"));
                this.setCity(address.getChildText("long_name"));
                System.out.println("Latitude response: " + location.getChildText("lat"));
                this.setLatitude(location.getChildText("lat"));
                System.out.println("Longitude response: " + location.getChildText("lng"));
                this.setLongitude(location.getChildText("lng"));
                //Aufrufen des Setters von urbanArea zur Bestimmung der naheliegensten Region
                this.setUrbanArea();
            } else {
                throw new Exception("Google Maps API Fehler");
            }




        } catch (Exception e) {
            //Im Fehlerfall alles Null setzen
            this.setCity("... Fehler");
            this.setLatitude("0");
            this.setLongitude("0");
            this.setUrbanArea();

            Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, e);


        }
    }

    /*
     * Methode zur Ermittlung des Aufenthaltsorts nach Benutzereingabe mit Yahoo
     */
    private void getLocationInfoByUserInputUsingYahoo(String userInput) {
        try {
            Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, userInput);

            System.out.println(userInput);

            //Zeichen umwandeln, die Yahoo nicht verwerten kann
            userInput = userInput.replaceAll("ä", "ae");
            userInput = userInput.replaceAll("ü", "ue");
            userInput = userInput.replaceAll("ö", "oe");
            userInput = userInput.replaceAll("ß", "ss");
            userInput = userInput.toLowerCase();

            System.out.println(userInput);
            // URL zu Yahoo Geolocation API erstellen
            URL url = new URL("http://local.yahooapis.com/MapsService/V1/geocode?appid=" + yahooAppID + "&location=" + userInput);

            // Document Builder initialisieren und mit Inhalt des XML von yahoo füttern

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(url.openStream());

            //Normalisieren
            doc.getDocumentElement().normalize();

            //City Attribut aus der XML lesen
            NodeList cityNodes = doc.getElementsByTagName("City");
            if (cityNodes.getLength() > 0) {
                //Als city setzen
                this.setCity(cityNodes.item(0).getTextContent());
            } else {
                this.setCity("Not found");
            }

            //Längengrad Attribut lesen
            NodeList latitudeNodes = doc.getElementsByTagName("Latitude");
            if (latitudeNodes.getLength() > 0) {
                //und setzen
                this.setLatitude(latitudeNodes.item(0).getTextContent());
            } else {
                //im Zweifel Null setzen
                this.setLatitude("0");
            }

            //Breitengrad Attribut lesen
            NodeList longitudeNodes = doc.getElementsByTagName("Longitude");
            if (longitudeNodes.getLength() > 0) {
                //und setzen
                this.setLongitude(longitudeNodes.item(0).getTextContent());
            } else {
                //im Zweifel Null setzen
                this.setLongitude("0");
            }

            //Aufrufen des Setters von urbanArea zur Bestimmung der naheliegensten Region
            this.setUrbanArea();


        } catch (Exception e) {
            //Im Fehlerfall alles Null setzen
            this.setCity("... Fehler");
            this.setLatitude("0");
            this.setLongitude("0");
            this.setUrbanArea();

            Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, e);


        }
    }
    /*
     * Methode zum Ermitteln des Aufenthaltsorts nach IP Adresse
     */

    private void getLocationInfoByIP(String theIP) {
        System.out.println("Originating IP Address = " + theIP);

        //Falls localhost zugreift, muss die IP Adresse auf einen leeren String gestezt werden, da der WebService andernfalls spinnt
        if (theIP.equalsIgnoreCase("0:0:0:0:0:0:0:1") || theIP.equalsIgnoreCase("0:0:0:0:0:0:0:1%0") || theIP.equalsIgnoreCase("127.0.0.1")) {
            theIP = "";
            String msg = "Changed IP to null String";
            System.out.println(msg);
            Logger.getLogger(Location.class.getName()).log(Level.INFO, null, msg);

        }


        try {

            // URL zu GeoLocation API erstellen
            URL url = new URL("http://www.geoplugin.net/xml.gp?ip=" + theIP);

            System.out.println("Erzeugte URL: " + url.toString());

            // Document Builder initialisieren und mit Inhalt des XML von GeoPlugin füttern

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder db = dbf.newDocumentBuilder();


            Document doc = db.parse(url.openStream());

            //Normalisieren
            doc.getDocumentElement().normalize();



            //Längengrad einlesen
            NodeList latitudeNodes = doc.getElementsByTagName("geoplugin_latitude");
            if (latitudeNodes.getLength() > 0) {
                this.setLatitude(latitudeNodes.item(0).getTextContent());
            } else {
                this.setLatitude("0");
            }

            //Breitengrad einlesen
            NodeList longitudeNodes = doc.getElementsByTagName("geoplugin_longitude");
            if (longitudeNodes.getLength() > 0) {
                this.setLongitude(longitudeNodes.item(0).getTextContent());
            } else {
                this.setLongitude("0");
            }

            //Aufrufen des Setters von urbanArea zur Bestimmung der naheliegensten Region
            this.setUrbanArea();

            //City Attribut einlesen
            NodeList cityNodes = doc.getElementsByTagName("geoplugin_city");
            if (cityNodes.getLength() > 0) {
                String geopluginCity = cityNodes.item(0).getTextContent();
                //Falls keine Stadt geliefert wird (passiert bei UMTS Zugriffen)
                if (geopluginCity.equalsIgnoreCase("")) {
                    //setze Stadt gleich der ermittelten Region
                    this.setCity(this.getUrbanArea());
                } else {
                    this.setCity(geopluginCity);
                }
            } else {
                this.setCity("Not found");
            }


        } catch (Exception e) {
            //Im Fehlerfall alles auf Null
            this.setCity("... Fehler - IP - Adresse: " + theIP);
            this.setLatitude("0");
            this.setLongitude("0");
            this.setUrbanArea();

            Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, e);

        }

    }
}
