/*
 * HeaderPanel.java
 *
 * Created on 29. März 2011, 11:15
 */
 
package de.hwr.wdint;


import com.sun.cnpi.rss.elements.*;
import com.sun.cnpi.rss.parser.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/** 
 *
 * @author juliusollesch
 * @version 
 */

public class HeaderPanel extends Panel {

    /**
     * Construct.
     * @param componentName name of the component
     * @param exampleTitle title of the example
     */

    public HeaderPanel(String componentName, String exampleTitle)
    {
        super(componentName);

       
        add(new Label("exampleTitle", "RSS Panel"));
        
            

        
    }

}
