package org.szh.util;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateBinder extends PropertyEditorSupport {

	private SimpleDateFormat format;

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Date date = null;
		try {
			date = format.parse(text);
		} catch (ParseException e) {
		}
		setValue(date);
	}

	public DateBinder(String format) {
		this.format = new SimpleDateFormat(format);
	}

}
