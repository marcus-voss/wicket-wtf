/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hwr.wdint.facebook;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.WebRequestor;
import com.restfb.types.Event;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.User;
import com.restfb.types.Venue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

		//do not add this line - the container has a static id
		//use the static id to update the component
		//this id must be a regular html id and NOT A WICKET:ID
		//setOutputMarkupId for this component must not be set to true!
		//fbInformation.setOutputMarkupId(true);
		add(fbInformation);

		//the actual information
		fbDataList = new ArrayList<String>();

		//this will populate the list
		fbDataView = new DataView("fbEntry", new ListDataProvider(fbDataList)) {

			@Override
			protected void populateItem(Item item) {

				//allow arbitrary html ... I know
				item.add(new Label("fbEntryContent", (String) item.getModelObject()).setEscapeModelStrings(false));

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

					if(target != null) {

						setupFBInformation(access_token, target);

					}

				} else {

					if(target != null) {

						resetFBInformation(target);

					}

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

		//clear list for new presentation
		fbDataList.clear();

		//use this for HWR proxy
		WebRequestor webRequestor = new DefaultWebRequestor() {

			@Override
			protected HttpURLConnection openConnection(URL url) throws IOException {

				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("194.94.23.231", 80));
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(proxy);
				return urlConnection;

			}

		};

		//create the client ...

		//... with HWR proxy settings
		FacebookClient fbClient = new DefaultFacebookClient(access_token, webRequestor, new DefaultJsonMapper());

		//... without proxy
		//FacebookClient fbClient = new DefaultFacebookClient(access_token);

		//get basic user info
		User user = fbClient.fetchObject("me", User.class);
		fbDataList.add(	"Hallo <a href='https://www.facebook.com/profile.php?id=" + user.getId() +
						"' target='_blank'>" + user.getFirstName() + "</a>!");

		//process all birthdays
		Connection<User> friends = fbClient.fetchConnection("me/friends", User.class, Parameter.with("fields", "id, name, birthday, location"));
		processBirthdays(friends);

		//get number of friends in current city
		int friendCount = processFriends(friends, user.getLocation().getId());
		while(friends.hasNext()) {

			friends = fbClient.fetchConnectionPage(friends.getNextPageUrl(), User.class);
			processBirthdays(friends);
			friendCount += processFriends(friends, user.getLocation().getId());

		}

		//process all events
		Connection<Event> events = fbClient.fetchConnection("me/events", Event.class);
		processEvents(events);
		while(events.hasNext()) {

			events = fbClient.fetchConnectionPage(events.getNextPageUrl(), Event.class);
			processEvents(events);

		}

		//display friend count
		fbDataList.add(friendCount + " Freunde in " + user.getLocation().getName());

		//use the static id to update the component
		//this id must be a regular html id and NOT A WICKET:ID
		//setOutputMarkupId for this component must not be set to true!
		target.addComponent(fbInformation, "fbInformation");

	}

	/**
	 * adds birthdays to the list
	 * @param friends
	 */
	private void processBirthdays(Connection<User> friends) {

		List<User> friendList = friends.getData();
		for(User friend : friendList) {

			//friend.getBirthdayAsDate() does not seem to work very well ...
			String bdString = friend.getBirthday();

			//some friends choose to not display their birthday at all
			if(bdString != null) {

				try {

					//only month and day are important
					Date birthday = new SimpleDateFormat("MM/dd").parse(bdString);
					if(isToday(birthday, false)) {

						//display the name as link
						fbDataList.add(	"<a href='https://www.facebook.com/profile.php?id=" + friend.getId() + "'" +
										" target='_blank'>" + friend.getName() + "</a>" +
										" hat heute Geburtstag - besorge ein Geschenk!");

					}

				} catch (ParseException ex) {

					System.out.println("Error parsing date '" + bdString + "' (" + ex.getMessage() + ")");

				}

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
			if(isToday(event.getStartTime(), true)) {

				//build link to event
				String eventLine =	"<a href='https://www.facebook.com/event.php?eid=" + event.getId() + "'" +
									" target='_blank'>" + event.getName() + "</a>";
				
				//check location
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
	 * returns number of friends in the location
	 * @param friends
	 * @param locationId
	 * @return
	 */
	private int processFriends(Connection<User> friends, String locationId) {

		int friendCount = 0;
		List<User> friendList = friends.getData();
		for(User friend : friendList) {

			NamedFacebookType location = friend.getLocation();
			if(location != null && location.getId().equals(locationId)) {

				++friendCount;

			}

		}

		return friendCount;

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
	 * obviously I was lazy ...
	 * http://www.computing.net/answers/programming/comparing-a-date-object-with-today/17209.html
	 * @param date
	 * @param compareYear
	 * @return
	 */
	private static boolean isToday(Date date, boolean compareYear) {

		Calendar today = Calendar.getInstance();
		today.setTime(new Date());

		Calendar otherday = Calendar.getInstance();
		otherday.setTime(date);

		//e.g. in birthdays we do not want to compare the year
		return	(compareYear ? (otherday.get(Calendar.YEAR) == today.get(Calendar.YEAR)) : true) &&
				otherday.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
				otherday.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);

	}

}
