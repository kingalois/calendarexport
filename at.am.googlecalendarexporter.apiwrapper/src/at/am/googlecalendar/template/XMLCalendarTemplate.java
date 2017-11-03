package at.am.googlecalendar.template;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import at.am.common.logging.LogFactory;
import at.am.googlecalendar.GoogleCalendarEvent;

public class XMLCalendarTemplate {

	private static final Logger log = LogFactory.makeLogger();

	private final InputStream template;
	List<GoogleCalendarEvent> events;

	public XMLCalendarTemplate(InputStream template, List<GoogleCalendarEvent> events) {
		this.template = template;
		this.events = events;
	}

	public String parseTemplate() {
		StringBuilder builder = new StringBuilder();
		boolean allEvents = false;

		try {
			XMLMemento memento = XMLMemento.createReadRoot(new InputStreamReader(template, "UTF-8"));
			IMemento titleMemento = memento.getChild("titel");
			if(titleMemento.getChild("kalender") != null && events.size() > 0){
				builder.append(events.get(0).getCalendarName());
			}
			builder.append(titleMemento.getTextData());
			builder.append(memento.getChild("beschreibung").getTextData());
			IMemento evs = memento.getChild("termine");
			if (evs != null) {
				allEvents = true;
			}
			IMemento[] eventDetails = evs.getChild("termin").getChildren();
			for (GoogleCalendarEvent event : events) {
				for (IMemento mem : eventDetails) {
					switch (mem.getType()) {
					case "datum":
						builder.append(getDateString(event.getStartDate(), mem.getString("format")));
						builder.append(mem.getTextData());
						break;
					case "startzeit":
						if (event.isAllDayEvent()) {
							builder.append("ganztägig");
						} else {
							builder.append(getDateString(event.getStartDate(), mem.getString("format")));

						}
						builder.append(mem.getTextData());
						break;
					case "endzeit":
						if (!event.isAllDayEvent()) {
							builder.append(getDateString(event.getEndDate(), mem.getString("format")));

						}
						builder.append(mem.getTextData());
						break;
					case "name":
						builder.append(event.getName());
						builder.append(mem.getTextData());
						break;

					default:
						break;
					}
				}
				if (!allEvents) {
					break;
				}
			}

			builder.append(memento.getChild("ende").getTextData());

		} catch (WorkbenchException | UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "cannot parse XML template", e);
		}

		return builder.toString();

	}

	private String getDateString(long date, String format) {
		SimpleDateFormat sDF = new SimpleDateFormat(format);
		return sDF.format(new Date(date));
	}
}
