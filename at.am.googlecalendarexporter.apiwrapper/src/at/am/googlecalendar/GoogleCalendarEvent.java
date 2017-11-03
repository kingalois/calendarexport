package at.am.googlecalendar;

public class GoogleCalendarEvent {

	private String calendarName;
	private String name;
	private long startDate;
	private long endDate;
	private String place;
	private boolean isAllDayEvent;
	
	public GoogleCalendarEvent(String calendarName){
		this.calendarName = calendarName;
	}

	public boolean isAllDayEvent() {
		return isAllDayEvent;
	}

	public void setAllDayEvent(boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}
	
	public String getCalendarName(){
		return this.calendarName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

}
