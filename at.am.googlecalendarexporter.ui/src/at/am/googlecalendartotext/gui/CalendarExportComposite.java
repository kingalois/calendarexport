package at.am.googlecalendartotext.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import at.am.common.logging.LogFactory;
import at.am.googlecalendar.GoogleCalendarEvent;
import at.am.googlecalendar.GoogleCalendarWrapper;
import at.am.googlecalendar.template.XMLCalendarTemplate;

public class CalendarExportComposite extends Composite {
	
	private static final String XML_TEMPLATE = "standardvorlage.xml";

	private static final Logger log = LogFactory.makeLogger();

	private final GoogleCalendarWrapper cal;

	public CalendarExportComposite(Composite parent, GoogleCalendarWrapper calendar, int style) {
		super(parent, style);
		this.cal = calendar;
		createContent(style);

	}

	public void createContent(int style) {
		GridLayout gridLayout = new GridLayout(2, false);
		this.setLayout(gridLayout);
		Label l = new Label(this, SWT.NONE);
		l.setText("1. Bitte den zu exportierenden Kalender auswählen:");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		l.setLayoutData(data);

		final List listOfCalendarCategories = new List(this, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		for (String item : cal.getAllCalendarNames()) {
			listOfCalendarCategories.add(item);
		}
		GridData listGridData = new GridData(GridData.FILL_HORIZONTAL);
		listGridData.heightHint = 100;
		listGridData.horizontalSpan = 2;
		listOfCalendarCategories.setLayoutData(listGridData);
		
		l = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		l.setLayoutData(data);
		
		l = new Label(this, SWT.NONE);
		l.setText("2. Start- und Enddatum wählen:");
		l.setLayoutData(data);
		
		

		new Label(this, SWT.NONE).setText("Startdatum:");
		final DateTime startDate = new DateTime(this, SWT.DATE | SWT.SHORT);
		new Label(this, SWT.NONE).setText("Enddatum:");
		final DateTime endDate = new DateTime(this, SWT.DATE | SWT.SHORT);
		Button button = new Button(this, SWT.PUSH);
		button.setText("Datensätze von Kalender laden");
		GridData buttonGridData = new GridData(GridData.FILL_HORIZONTAL);
		buttonGridData.heightHint = 20;
		buttonGridData.horizontalSpan = 2;
		button.setLayoutData(buttonGridData);
		
		l = new Label(this, SWT.NONE);
		l.setText("Hinweis: die Vorlage für den resultierenden Text liegt unter: " + new File(XML_TEMPLATE).getAbsolutePath());
		l.setLayoutData(data);
		
		
		final Text resultText = new Text(this, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		GridData griddata = new GridData(GridData.FILL_BOTH);
		griddata.horizontalSpan = 2;
		resultText.setLayoutData(griddata);

		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar calStart = Calendar.getInstance();
				calStart.set(startDate.getYear(), startDate.getMonth(), startDate.getDay());
				Calendar calEnd = Calendar.getInstance();
				calEnd.set(endDate.getYear(), endDate.getMonth(), endDate.getDay());
				if(listOfCalendarCategories.getSelectionCount()==0){
					resultText.setText("kein Kalender ausgew�hlt");
					return;
				}
				fillData(resultText, listOfCalendarCategories.getSelection()[0], calStart.getTimeInMillis(), calEnd.getTimeInMillis());
			}

		});

		Button exportButton = new Button(this, SWT.PUSH);
		exportButton.setText("Exportieren...");
		GridData exportButtonGridData = new GridData();
		exportButtonGridData.heightHint = 20;
		exportButtonGridData.widthHint = 100;
		exportButtonGridData.horizontalSpan = 2;
		exportButton.setLayoutData(buttonGridData);

		exportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog saveDialog = new FileDialog(getShell(), SWT.SAVE);
				saveDialog.setText("Speichere Daten...");
				String[] filterExt = { "*.txt", "*.*" };
				saveDialog.setFilterExtensions(filterExt);
				String fileName = saveDialog.open();
				if (fileName == null) {
					return;
				}

				FileWriter writer;
				try {
					writer = new FileWriter(fileName);
					writer.write(resultText.getText());
					writer.close();
				} catch (IOException e1) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
					messageBox.setMessage("Datei konnte nicht gespeichert werden: " + e1.getMessage());
					messageBox.setText("Fehler");
					messageBox.open();
					return;
				}

			}
		});

	}

	private void fillData(Text data, String summary, long from, long to) {
		java.util.List<GoogleCalendarEvent> events = cal.getEvents(summary, from, to);
		StringBuilder builder = new StringBuilder();
		FileInputStream in = null;
		try {
			in = new FileInputStream(XML_TEMPLATE);
			XMLCalendarTemplate temp = new XMLCalendarTemplate(in, events);
			builder.append(temp.parseTemplate());
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "cannot find template", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.log(Level.SEVERE, "cannot close template stream", e);
				}
			}

		}
		data.setText(builder.toString());

	}
}
