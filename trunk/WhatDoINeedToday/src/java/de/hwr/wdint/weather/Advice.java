package de.hwr.wdint.weather;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Advice
 * Objekt, dass den Ratschlag für den User erstellt
 * @author Marcus Voss
 */
public class Advice implements Serializable
{
//Konstante, die für die Wetterkonditionen einen Ratschlag zurückgibt
private final static HashMap<String, String> ADVICE_TABLE = adviceTable();

//Enthält den Ratschlag
private String advice;

    /**
     * Konstruktor
     * @param location muss übergeben werden
     */
    public Advice(String conditions)
    {
        //Holt mithilfe der Konstante ADVIC_TABLE den Ratschlag für die aktuellen Wetterkonditionen
        if (ADVICE_TABLE.get(conditions) != null)
        {
            advice = ADVICE_TABLE.get(conditions);
        }
        else
        {
            //Falls etwas schief läuft
            advice = "Lass dich überraschen!";
        }
        
    }

    /**
     * Getter für advice
     * @return
     */
    public String getAdvice()
    {
        return advice;
    }

    /**
     * initiert die Konstante ADVICE_TABLE
     * @return
     */
    private static HashMap<String, String> adviceTable()
    {
        HashMap<String, String> adviceTable = new HashMap();

        adviceTable.put("Chance of Flurries", "Iss lieber gut Frühstück, es wird vielleicht windig!");
        adviceTable.put("Chance of Rain", "Nimm lieber einen Regenschirm mit, es könnte regnen!");
        adviceTable.put("Chance of Freezing Rain", "Nimm lieber einen Regenschirm und einen extra Pullover mit!");
        adviceTable.put("Chance of Sleet", "Bleib lieber drinn, es könnte ekelig werden!");
        adviceTable.put("Chance of Snow", "Nimm ein paar Handschuhe mit und, du könntest vielleicht nen Schneemann bauen!");
        adviceTable.put("Chance of Thunderstorms", "Lass sicherheitshalber den Drachen zu hause!");
        adviceTable.put("Chance of a Thunderstorm", "Lass sicherheitshalber den Drachen zu hause!");
        adviceTable.put("Clear", "Dich kann heute nichts betrüben!");
        adviceTable.put("Cloudy", "Naja, du machst einfach trotzdem das Best draus!");
        adviceTable.put("Flurries", "Iss gut Frühstück, es wird windig!");
        adviceTable.put("Fog", "Eine starke Taschenlampe kann hilfreich sein!");
        adviceTable.put("Haze", "Nimm am besten einen Helm mit!");
        adviceTable.put("Mostly Cloudy", "Lass dich davon nicht runterkriegen!");
        adviceTable.put("Mostly Sunny", "Das wird sicher ein schöner Tag!");
        adviceTable.put("Partly Cloudy", "Das wird überwiegend ein schöner Tag!");
        adviceTable.put("Partly Sunny", "Alles supi heute!");
        adviceTable.put("Freezing Rain", "Wenn es sein muss, dann zieh dich warm an und nimm einen Regenschirm!");
        adviceTable.put("Rain", "Nimm einen Regenschirm mit!");
        adviceTable.put("Sleet", "Bleib einfach zu hause!");
        adviceTable.put("Snow", "Ein paar Handschuhe zum Schneemann bauen, ein Schlitten oder Ski und du wirst heute glücklich!");
        adviceTable.put("Sunny", "Don't worry, be happy!");
        adviceTable.put("Thunderstorms", "Drachen steigen wird heute jedenfalls nix!");
        adviceTable.put("Thunderstorm", "Drachen steigen wird heute jedenfalls nix!");
        adviceTable.put("Unknown", "Lass dich überraschen!");
        adviceTable.put("Overcast", "Lass dich davon einfach nicht runterkriegen!");
        adviceTable.put("Scattered Clouds", "Die paar Wolken werden dich nicht runterkriegen!");

        return adviceTable;
    }
}
