/*
 * Application.java
 *
 * Created on 29. März 2011, 11:15
 */
package de.hwr.wdint;

import org.apache.wicket.protocol.http.WebApplication;

/** 
 *
 * @author juliusollesch
 * @version 
 */
public class Application extends WebApplication {



    @Override
    protected void init() {
        super.init();
        this.getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        this.getRequestCycleSettings().setResponseRequestEncoding("UTF-8"); 
    }


    public Application() {
        
    }

    public Class getHomePage() {
        return HomePage.class;
    }

    
}
