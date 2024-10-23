package org.aulich.utilities;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * The JsonTypeConverter helps to convert data from SQL date format into a
 * format that the Google Charts Datatable expects.
 */
public class JsonTypeConverter {
    public static String getDate(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        return "Date(" + calendar.get(Calendar.YEAR) + ", "
                + calendar.get(Calendar.MONTH)
                + ", " + calendar.get(Calendar.DAY_OF_MONTH)
                + ")";
    }
    public static String getDateTime(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        return "Date(" + calendar.get(Calendar.YEAR) + ", "
                + calendar.get(Calendar.MONTH)
                + ", " + calendar.get(Calendar.DAY_OF_MONTH)
                + ", " + calendar.get(Calendar.HOUR)
                + ", " + calendar.get(Calendar.MINUTE)
                + ")";
    }

}
