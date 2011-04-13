/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.rss;

import java.io.Serializable;

/**
 *
 * @author juliusollesch
 */
public class RSSEntry implements Serializable{

    public String getURL() {
        return URL;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
    private final String title;
    private final String description;
    private final String URL;

    public RSSEntry(String title, String desc, String url){
        this.title = title;
        this.description = desc;
        this.URL = url;
    }
}
