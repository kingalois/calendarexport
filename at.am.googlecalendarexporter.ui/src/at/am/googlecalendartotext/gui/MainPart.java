package at.am.googlecalendartotext.gui;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import at.am.googlecalendar.GoogleCalendarWrapper;

public class MainPart {
	private static final String HH_MM = "HH.mm";
	private static final String DD_MM_YYYY = "EE'\t' dd.MM.yyyy";
	private final GoogleCalendarWrapper cal;

	public MainPart() {
		cal = new GoogleCalendarWrapper();
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		new CalendarExportComposite(parent, cal, SWT.NONE);
	}

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO Set the focus to control
	}

}
