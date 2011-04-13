/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.location;

/**
 *  Enum-Klasse zur Speicherung der Regionen in Anlehnung an prinz.de
 * @author juliusollesch
 * @version 1.0
 */
public enum Regions {
    //Längen- und Breitengrade für die Prinz Regionen
    Berlin(52.516700744629,13.39999961853), Bremen(53.0833333, 8.8), Ruhrgebiet(51.4833333, 7.2166667), Köln(50.9333333, 6.95), Dresden(51.05, 13.75), Düsseldorf(51.2166667, 6.7666667), Frankfurt(50.1166667, 8.6833333), Hamburg(53.55, 10), Leipzig(51.3, 12.333333), München(48.15, 11.5833333), Stuttgart(48.7666667, 9.1833333);

    private double latitude;
    private double longitude;

    private Regions(double la, double lo){
        latitude = la;
        longitude = lo;
        
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }
}
