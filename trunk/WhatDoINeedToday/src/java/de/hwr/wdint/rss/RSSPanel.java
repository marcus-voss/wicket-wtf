package de.hwr.wdint.rss;

import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserException;
import com.sun.cnpi.rss.parser.RssParserFactory;
import de.hwr.wdint.HeaderPanel;
import de.hwr.wdint.Application;
import de.hwr.wdint.BasePage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;

/**
 * Panel zur Anzeige diverser RSS Feeds
 * @version 1.0
 * @author juliusollesch
 */
public final class RSSPanel extends Panel {

    final WebMarkupContainer panelPlaceholder = new WebMarkupContainer("panelPlaceholder"){
      {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }
    };

    //Variable für das Datenmodell des Labels des Pannels
    private String labelTitleText = "Willst Du heute Abend weggehen?";

    public String getLabelText() {
        return labelTitleText;
    }

    public void setLabelText(String labelText) {
        this.labelTitleText = labelText;
    }
    Label title = new Label("labelTitle", new PropertyModel(this, "labelTitleText")) {

        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }
    };
    /*
     * Data Container umschliesst die Tabelle/Dataview, dies ist nötig,
     * damit die Tabelle via ajax aktualisiert werden kann. DataView an sich
     * ist nicht ajax-fähig...
     */
    final WebMarkupContainer dataContainer = new WebMarkupContainer("dataContainer") {

        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }
    };
    //Array-List als Grundlage des Datenmodells des Data View
    private ArrayList list = new ArrayList();

    public void setList(ArrayList list) {
        this.list = list;
    }
    /*
     * DataView zur Füllung der Tabelle mit Einträgen, es werden maximal fünf
     * Einträge gleichzeitig angezeigt.
     */
    DataView dataView = new DataView("simple", new ListDataProvider(list), 5) {
        /*
         * Hier wird festgelegt, wie die Tabelle zu füllen ist
         * id und description finden sich in der HTML wieder
         */

        public void populateItem(final org.apache.wicket.markup.repeater.Item item) {
            //RSSEntry ist eine kleine Hilfsklasse zur Speicherung der RSS-Einträge der Tabelle
            final RSSEntry entry = (RSSEntry) item.getModelObject();
            //füge Label bzw. MultilineLabel hinzu, bei der Description wird HTML-Code erlaubt
            item.add(new Label("id", entry.getTitle().toString()));
            item.add(new MultiLineLabel("description", entry.getDescription().toString()).setEscapeModelStrings(false));
        }

        {
            //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
            setOutputMarkupId(true);
        }
    };
    //Navigation Controller zur Navigation in der DataView
    AjaxPagingNavigator pager = new AjaxPagingNavigator("navigator", dataView) {
        //Code kommt so "out of the box"

        @Override
        protected void onAjaxEvent(AjaxRequestTarget target) {
            target.addComponent(dataContainer);
        }
    };

    /*
     * Konstruktor für das Panel
     */
    public RSSPanel(String id) {
        super(id);
        //Wichtig damit ajax funktioniert! So wird die ID des Elements in die HTML übergeben
        this.setOutputMarkupId(true);



        try {



            /*
             * Füge einen neuen AjaxLink hinzu - Weggehen! soll die aktuellen
             * Events aus dem Prinz RSS Feed der Region des Nutzers in der
             * DataView anzeigen
             */
            panelPlaceholder.add(new IndicatingAjaxFallbackLink("link1") {
                //OnClick wird überschrieben um die Logik zu implementieren

                @Override
                public void onClick(AjaxRequestTarget target) {

                    // Ereignis behandeln
                    //title.setDefaultModelObject("clicked");
                    // target ist null:    Regulärer Request, Seite wird neu geladen
                    // target ist gesetzt: Ajax-Request, Komponenten können aktualisiert werden
                    if (target != null) {
                        // Titel Label aktualisieren
                        labelTitleText = "Events in der Region " + ((BasePage) this.getPage()).getUserLocation().getUrbanArea();

                        target.addComponent(title);
                        //DataView mit den Events füllen und aktualisieren
                        updateDataViewWithEvents(target);

                        panelPlaceholder.add(new SimpleAttributeModifier("class", "rssPanelParty"));

                        target.addComponent(panelPlaceholder);

                    }   
                }
            });

            
            /*
             * Füge einen zweiten AjaxLink hinzu um das Fernsehprgramm zu laden
             */
            panelPlaceholder.add(new IndicatingAjaxFallbackLink("link2") {
                //OnClick wird überschrieben um die Logik zu implementieren

                @Override
                public void onClick(AjaxRequestTarget target) {
                    // Ereignis behandeln
                    //title.setDefaultModelObject("clicked");
                    // target ist null:    Regulärer Request, Seite wird neu geladen
                    // target ist gesetzt: Ajax-Request, Komponenten können aktualisiert werden
                    if (target != null) {
                        // Titel Label aktualisieren
                        labelTitleText = "Fernsehprogramm für heute Abend";
                        target.addComponent(title);
                        try {
                            //Data View aktualisieren
                            list.clear();
                            list.addAll(readRSS(makeTVURL())); //aktualisert list mit den Einträgen des Fernsehprogramms
                            System.out.println("Liste mit: " + list.size());
                            //dataView neu rendern
                            target.addComponent(dataContainer);
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(RSSPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        panelPlaceholder.add(new SimpleAttributeModifier("class", "rssPanelTV"));

                        target.addComponent(panelPlaceholder);

                    }
                }
            });

            //Schließlich: Kompenenten hinzufügen
            panelPlaceholder.add(title);
            dataContainer.add(dataView);
            dataContainer.add(pager);
            panelPlaceholder.add(dataContainer);

            add(panelPlaceholder);
            
            
            

        } catch (Exception ex) {
            Logger.getLogger(HeaderPanel.class.getName()).log(Level.SEVERE, null, ex);
            add(new Label("title", ex.toString()));
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        labelTitleText = "Willst Du heute Abend weggehen?";
        
    }


    /*
     * Aktualisierung des DataView mit Events
     */
    private void updateDataViewWithEvents(AjaxRequestTarget target) {

        try {
            //list muss komplett geleert und neubefüllt werden, damit ajax funktioniert
            list.clear();
            list.addAll(readRSS(makePrinzKonzertURL())); //aktualisert list
            list.addAll(readRSS(makePrinzPartyURL())); //aktualisert list
            list.addAll(readRSS(makePrinzKulturURL())); //aktualisert list
            //DataContainer mit ajax neu rendern - das aktualisiert auch DataView
            target.addComponent(dataContainer);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RSSPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
     * Erstellt die URL zum Prinz Konzert Feed in der Region des Nutzers
     */

    private URL makePrinzKonzertURL() throws MalformedURLException {
        String area = ((BasePage) this.getPage()).getUserLocation().getUrbanArea();
        area = area.replaceAll("ä", "ae");
        area = area.replaceAll("ü", "ue");
        area = area.replaceAll("ö", "oe");
        area = area.replaceAll("ß", "ss");
        area = area.toLowerCase();

        System.out.println("Area: " + area);
        return new URL("http://" + area + ".prinz.de/rss/konzert");

    }
    /*
     * Erstellt die URL zum Prinz Party Feed in der Region des Nutzers
     */

    private URL makePrinzPartyURL() throws MalformedURLException {
        String area = ((BasePage) this.getPage()).getUserLocation().getUrbanArea();
        area = area.replaceAll("ä", "ae");
        area = area.replaceAll("ü", "ue");
        area = area.replaceAll("ö", "oe");
        area = area.replaceAll("ß", "ss");
        area = area.toLowerCase();

        System.out.println("Area: " + area);
        return new URL("http://" + area + ".prinz.de/rss/party");

    }
    /*
     * Erstellt die URL zum Prinz Kultur Feed in der Region des Nutzers
     */

    private URL makePrinzKulturURL() throws MalformedURLException {
        String area = ((BasePage) this.getPage()).getUserLocation().getUrbanArea();
        area = area.replaceAll("ä", "ae");
        area = area.replaceAll("ü", "ue");
        area = area.replaceAll("ö", "oe");
        area = area.replaceAll("ß", "ss");
        area = area.toLowerCase();

        System.out.println("Area: " + area);
        return new URL("http://" + area + ".prinz.de/rss/kultur");

    }
    /*
     * Erstellt die URL zum TVProgramm24 Feed
     */

    private URL makeTVURL() throws MalformedURLException {
        return new URL("http://www.tvprogramm24.com/rss.xml");
    }

    /*
     * Generische Methode zum Einlesen eines RSS Feeds in eine ArrayList mit RSSEntry Objekten
     */
    private ArrayList readRSS(URL feedURL) {
        String tag = "READRSS";
        String returnCode = "";


        /*
         *
         */
        ArrayList myList = new ArrayList();
        try {

            //Erzeuge Parser
            RssParser parser = RssParserFactory.createDefault();

            //Parse URL
            URL url = feedURL;
            Rss rss = parser.parse(url);

            if (rss.getChannel() == null) {
                returnCode = "Kein Channel gefunden";

            }
            //Nehme alle RSS Einträge
            Collection items = rss.getChannel().getItems();
            //Wenn es welche gibt dann füge diese zur ArrayList hinzu
            if (items != null && !items.isEmpty()) {
                for (Iterator i = items.iterator(); i.hasNext();) {
                    Item item = (Item) i.next();
                    String itemTitle = item.getTitle().toString();
                    System.out.println("Original: " + item.getDescription().toString());
                    String itemDescription = item.getDescription().toString().replaceAll("(\r\n|\r|\n|\n\r)", "");//.replaceAll("<b[^<]+?>", "");
                    System.out.println("RegExed: " + itemDescription);
                    String itemLink = item.getLink().toString();
                    //Erzeuge neues RRSEntry Objekt und packe es in die Liste
                    RSSEntry entry = new RSSEntry(itemTitle, itemDescription, itemLink);
                    myList.add(entry);
                }
            }


            //return returnCode;

        } catch (RssParserException e) {
            // TODO Auto-generated catch block
            returnCode = "RSSParserException";
            //return returnCode;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            returnCode = "MalformedURLException";
            //return returnCode;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            returnCode = "IOException";
            //return returnCode;
        } finally {
            return myList;
        }
    }
}
