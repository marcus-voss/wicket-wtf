/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hwr.wdint.location;

import java.io.Serializable;
import java.net.Proxy;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
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
     * yahooAppID ist der Schlüssel der von yahoo für dei Nutzung des Geolocation Services benötigt wird
     */
    final String yahooAppID = "PCvCMCfV34EK39cA9nSF8xTLKsi_b_iYNcPOvjVVRf20M_OOxfevRC0duEU_7kvwwXBq_EJN3klw844mtFAHiNqFJC9F2Yo-";

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
        System.out.println("minDistance: " + minDistance + " Region: " + regionName);
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
        userInput = userInput.replace(" ", "%20");
        getLocationInfoByUserInput(userInput);

    }

    /*
     * Konstruktor für Location ohne Eingaben des Benutzers
     * Stellt die IP Adresse des Benutzers fest und versucht aus dieser den Aufenthaltsort zu erkennen.
     */
    public Location() {
        //Aktuellen WebRequest bekommen
        WebRequest wr = (WebRequest) RequestCycle.get().getRequest();
        //IP Adresse des Benutzers feststellen
        String originatingIPAddress = wr.getHttpServletRequest().getRemoteHost();
        
        //Methode aufrufen zur Geolokalisierung des Benutzers mittels IP
        getLocationInfoByIP(originatingIPAddress);


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
     * Methode zur Ermittlung des Aufenthaltsorts nach Benutzereingabe
     */
    private void getLocationInfoByUserInput(String userInput) {
        try {
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
            this.setCity("...Fehler");
            this.setLatitude("0");
            this.setLongitude("0");
            this.setUrbanArea();
            e.printStackTrace();

        }
    }
    /*
     * Methode zum Ermitteln des Aufenthaltsorts nach IP Adresse
     */

    private void getLocationInfoByIP(String theIP) {
        System.out.println("Originating IP Address = " + theIP);
        //Falls localhost zugreift, muss die IP Adresse auf einen leeren String gestezt werden, da der WebService andernfalls spinnt
        if(theIP.equalsIgnoreCase("0:0:0:0:0:0:0:1%0") || theIP.equalsIgnoreCase("127.0.0.1")){
            theIP = "";
            System.out.println("Changed IP to null String");
        }


        try {

            // URL zu GeoLocation API erstellen
            URL url = new URL("http://www.geoplugin.net/xml.gp?ip=" + theIP);

            System.out.println("Erzeugte URL: " + url.toString());

            // Document Builder initialisieren und mit Inhalt des XML von GeoPlugin füttern

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            System.out.println("URL Stream: " + url.openConnection(Proxy.NO_PROXY));

            Document doc = db.parse(url.openStream());

            //Normalisieren
            doc.getDocumentElement().normalize();


            //City Attribut einlesen
            NodeList cityNodes = doc.getElementsByTagName("geoplugin_city");
            if (cityNodes.getLength() > 0) {
                this.setCity(cityNodes.item(0).getTextContent());
            } else {
                this.setCity("Not found");
            }
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


        } catch (Exception e) {
            //Im Fehlerfall alles auf Null
            this.setCity("... Fehler");
            this.setLatitude("0");
            this.setLongitude("0");
            this.setUrbanArea();
            e.printStackTrace();
        }

    }
}
