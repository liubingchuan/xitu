package com.xitu.app.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTest {
	public static void main(String[] args) {
		System.err.println(getTwoDaysDay("2017-8-27", "2017-9-15"));
	}

	// 取一段时间的每一天
	public static List<String> getTwoDaysDay(String dateStart, String dateEnd) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> dateList = new ArrayList<String>();
		try {
			Date dateOne = sdf.parse(dateStart);
			Date dateTwo = sdf.parse(dateEnd);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateTwo);

			dateList.add(dateEnd);
			while (calendar.getTime().after(dateOne)) { // 倒序时间,顺序after改before其他相应的改动。
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				dateList.add(sdf.format(calendar.getTime()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateList;
	}
}
