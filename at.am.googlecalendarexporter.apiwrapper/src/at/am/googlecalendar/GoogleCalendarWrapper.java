package at.am.googlecalendar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.am.common.logging.LogFactory;
import at.am.googlecalendar.connection.GoogleCalendarConnector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class GoogleCalendarWrapper {

	private Logger log = LogFactory.makeLogger();

	private Calendar calendar;

	public GoogleCalendarWrapper() {
		log.info("try to load calendar service");
		try {
			calendar = GoogleCalendarConnector.getCalendarService();
		} catch (IOException e) {
			log.log(Level.SEVERE, "cannot create GoogleCalendarWrapper", e);
		}
		log.info("calendar service successfully loaded");
	}

	public List<String> getAllCalendarNames() {
		if(calendar == null){
			log.log(Level.WARNING, "no calendar available, return empty list");
			return Collections.emptyList();
		}
		log.info("getAllCalendarNames");
		List<String> result = new ArrayList<String>();
		try {
			com.google.api.services.calendar.model.CalendarList list = calendar.calendarList().list().execute();
			List<CalendarListEntry> items = list.getItems();
			for (CalendarListEntry entry : items) {
				result.add(entry.getSummary());
		//		log.info("name: " + entry.getSummary());
			}

		} catch (IOException e) {
			log.log(Level.SEVERE, "cannot load all calendars", e);
		}
		return result;
	}

	public String getCalendarIdForSummary(String summary) {
		try {
			com.google.api.services.calendar.model.CalendarList list = calendar.calendarList().list().execute();
			List<CalendarListEntry> items = list.getItems();
			for (CalendarListEntry entry : items) {
				if (entry.getSummary().equals(summary)) {
					return entry.getId();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public List<GoogleCalendarEvent> getEvents(String summary, long from, long to) {
		List<GoogleCalendarEvent> result = new ArrayList<GoogleCalendarEvent>();
		try {
			Events events = calendar.events().list(getCalendarIdForSummary(summary)).setSingleEvents(true).setOrderBy("startTime").setTimeMin(new DateTime(from)).setTimeMax(new DateTime(to))
					.execute();
			for (Event event : events.getItems()) {
				GoogleCalendarEvent gEvent = new GoogleCalendarEvent(summary);
				gEvent.setName(event.getSummary());
				gEvent.setStartDate(event.getStart().getDateTime() == null ? event.getStart().getDate().getValue() : event.getStart().getDateTime().getValue());
				gEvent.setEndDate(event.getEnd().getDateTime() == null ? event.getEnd().getDate().getValue() : event.getEnd().getDateTime().getValue());
				gEvent.setPlace(event.getLocation());
				gEvent.setAllDayEvent(event.getStart().getDateTime() == null);
				result.add(gEvent);
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "cannot load all calendars", e);
		}
		log.info("getEvents");
		return result;
	}
}
