package ch.makery.book.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

object DateUtil {
  val DATE_PATTERN = "dd-MM-yyyy"
  val DATE_FORMATTER =  DateTimeFormatter.ofPattern(DATE_PATTERN)
  
  implicit class DateFormater (val date : LocalDate){

     /**
     * Returns the given date as a well formatted String. The above defined 
     * {@link DateUtil#DATE_PATTERN} is used.
     * 
     * @param date the date to be returned as a string
     * @return formatted string
     */
     def asString : String = {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }
  }
  implicit class StringFormater (val data : String) {
        /**
     * Converts a String in the format of the defined {@link DateUtil#DATE_PATTERN} 
     * to a {@link LocalDate} object.
     * 
     * Returns null if the String could not be converted.
     * 
     * @param dateString the date as String
     * @return the date object or null if it could not be converted
     */
    def parseLocalDate : LocalDate = {
        try {
          LocalDate.parse(data, DATE_FORMATTER)
        } catch {
          case  e: DateTimeParseException => null
        }
    }
    def isValid : Boolean = {
      data.parseLocalDate != null
    }
  }
}