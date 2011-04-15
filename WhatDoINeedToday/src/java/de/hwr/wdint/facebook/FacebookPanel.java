/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author robert
 */
public final class FacebookPanel extends Panel {

	/*
	 * reeeeaaaally nice lightweight REST client for facebook:
	 *
	 * http://www.restfb.com
	 */

	/**
	 * What Do I Need Today Facebook App Details
	 */
	private static String APP_ID = "113587772056243";
	private static String API_KEY = "e6b72373a0883c54eee13f120a2f671a";
	private static String APP_SECRET = "59760c84a9500b10b21f53c28b61338e";

	/**needs to be a container because the login button is a child element*/
	private WebMarkupContainer fbLoginButton;

	/**container for all information div*/
	private WebMarkupContainer fbInformation;

	/**
	 * label for welcome text and its text
	 */
	private Label labelWelcome;
	private String labelWelcomeText;

	/**
	 * constructor
	 * @param id
	 */
    public FacebookPanel(String id) {

        super (id);

		//add the login button
		fbLoginButton = new WebMarkupContainer("fbLoginButton");
		add(fbLoginButton);

		//the fb information container
		fbInformation = new WebMarkupContainer("fbInformation");
		add(fbInformation);

		labelWelcomeText = "Hallo ...";
		labelWelcome = new Label("welcomeLabel", new PropertyModel(this, "labelWelcomeText"));
		labelWelcome.setOutputMarkupId(true);
		fbInformation.add(labelWelcome);
/*
		System.getProperties().put("http.proxyHost", "194.94.23.231");
		System.getProperties().put("http.proxyPort", "80");
*/
    }

	@Override
	protected void onInitialize() {

		super.onInitialize();

		//we are able to create this component here only because it needs to be already added to the panel
		//(i.e. the constructor must have completed) because it needs to be able to respond to an id
		//(see the wicketAjaxGet call in the .html file)
		final AbstractDefaultAjaxBehavior behavior = new AbstractDefaultAjaxBehavior() {

			@Override
			protected void respond(final AjaxRequestTarget target) {

				//retrieve the access token as a parameter
				String access_token = RequestCycle.get().getRequest().getParameter("fb_access_token");

				//set / reset UI depending on the value
				if(!access_token.equals("null")) {

					setupFBInformation(access_token, target);

				} else {

					resetFBInformation(target);

				}

			}
			
		};
		
		add(behavior);
		
	}

	/**
	 * gets all kinds of information from facebook (birthdays etc)
	 * and displays them in the facebookpanel
	 * @param access_token
	 */
	private void setupFBInformation(String access_token, AjaxRequestTarget target) {

		//create the client
		FacebookClient fbClient = new DefaultFacebookClient(access_token);

		//get basic user info
		User user = fbClient.fetchObject("me", User.class);

		//create a "welcome" line
		labelWelcomeText = "Hallo " + user.getFirstName() + "!";
		target.addComponent(labelWelcome);

		//TODO get events, birthdays, ...

	}

	/**
	 * resets all the info labels
	 * @param target
	 */
	private void resetFBInformation(AjaxRequestTarget target) {

		labelWelcomeText = "Hallo ...";
		target.addComponent(labelWelcome);

	}

}
