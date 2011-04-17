/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.facebook;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Event;
import com.restfb.types.User;
import com.restfb.types.Venue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;

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

	/**container for the data view*/
	private WebMarkupContainer fbInformation;
	
	/**displays the data*/
	private DataView fbDataView;

	/**list contains all our data*/
	private ArrayList<String> fbDataList;

	/**
	 * constructor
	 * @param id
	 */
    public FacebookPanel(String id) {

        super (id);

		//title of the panel
		add(new Label("labelTitle", "Dein Facebook"));

		//add the login button
		fbLoginButton = new WebMarkupContainer("fbLoginButton");
		add(fbLoginButton);

		//the fb information container
		fbInformation = new WebMarkupContainer("fbInformation");
		fbInformation.setOutputMarkupId(true);
		add(fbInformation);

		//the actual information
		fbDataList = new ArrayList<String>();

		//this will populate the list
		fbDataView = new DataView("fbEntry", new ListDataProvider(fbDataList)) {

			@Override
			protected void populateItem(Item item) {

				item.add(new Label("fbEntryContent", (String) item.getModelObject()));

			}
			
		};

		fbInformation.add(fbDataView);
		
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
		fbDataList.add("Hallo " + user.getFirstName() + "!");

		//process all birthdays
		Connection<User> friends = fbClient.fetchConnection("me/friends", User.class, Parameter.with("fields", "birthday"));
		processBirthdays(friends);
		while(friends.hasNext()) {

			friends = fbClient.fetchConnectionPage(friends.getNextPageUrl(), User.class);
			processBirthdays(friends);

		}

		//process all events
		Connection<Event> events = fbClient.fetchConnection("me/events", Event.class);
		processEvents(events);
		while(events.hasNext()) {

			events = fbClient.fetchConnectionPage(events.getNextPageUrl(), Event.class);
			processEvents(events);

		}

		target.addComponent(fbInformation);

	}

	/**
	 * adds birthdays to the list
	 * @param friends
	 */
	private void processBirthdays(Connection<User> friends) {

		List<User> friendList = friends.getData();
		for(User friend : friendList) {

			Date birthday = friend.getBirthdayAsDate();
			if(birthday != null && isToday(birthday)) {

				fbDataList.add(friend.getFirstName() + " hat heute Geburtstag - besorge ein Geschenk!");

			}

		}

	}

	/**
	 * adds events to the list
	 * @param events
	 */
	private void processEvents(Connection<Event> events) {

		List<Event> eventList = events.getData();
		for(Event event : eventList) {

			//list includes past and future events
			Date eventDate = event.getStartTime();
			if(isToday(event.getStartTime())) {

				//check location
				String eventLine = event.getName();
				String eventLocation = event.getLocation();
				if(eventLocation == null) {

					//maybe an address?
					Venue venue = event.getVenue();
					if(venue == null) {

						//no location, no venue, maybe description
						eventLine += " (" + (event.getDescription() != null ? event.getDescription() : "weder Ort noch Beschreibung vorhanden") + ")";

					} else {

						//no location but a venue
						eventLocation = venue.getCity() != null ? (venue.getCity() + " ") : "";
						eventLocation += venue.getStreet() != null ? venue.getStreet() : "";

					}

				} else {

					//location is set
					eventLine += " in " + eventLocation;

				}

				//we only need the time because it is today
				eventLine += " um " + new SimpleDateFormat("HH:mm").format(eventDate) + " Uhr";

				fbDataList.add(eventLine);

			}

		}

	}

	/**
	 * resets all content
	 * @param target
	 */
	private void resetFBInformation(AjaxRequestTarget target) {

		fbDataList.clear();
		target.addComponent(fbInformation);

	}

	/**
	 * obviously i was lazy ...
	 * http://www.computing.net/answers/programming/comparing-a-date-object-with-today/17209.html
	 * @param date
	 * @return
	 */
	private static boolean isToday(Date date) {

		Calendar today = Calendar.getInstance();
		today.setTime(new Date());

		Calendar otherday = Calendar.getInstance();
		otherday.setTime(date);

		return	otherday.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
				otherday.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
				otherday.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);

	}

}
