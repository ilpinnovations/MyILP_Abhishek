package com.ilp.ilpschedule.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {
	String PREF_FILE_NAME = "com.tcs.preffile";
	String URL_BASE = "http://theinspirer.in/ilpscheduleapp/";
	String URL_BADGES = URL_BASE + "points_json.php?";
	String URL_NOTIFICATION = URL_BASE + "notify_json.php";
	String URL_SCHEDULE = URL_BASE + "schedulelist_json.php";
	String URL_CONTACTS = URL_BASE + "getEmergencyContacts.php?";
	String URL_FEEDBACK_SUBMIT = URL_BASE + "feedback_json.php";
	String URL_FEEDBACK_SUMMARY = URL_BASE + "faculty_json.php?";
	String URL_REGISTER = URL_BASE + "register.php";
	String URL_LOGIN = URL_BASE + "login.php";
	String URL_GOOGLE_MAP_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

	String QUESTIONMARK = "?";
	String AND = "&";
	String SPACE = " ";
	String EQUALS = "=";
	String CHARSET = "UTF-8";
	String GOOGLE_SENDER_ID = "1039038689673";
	String GOOGLE_MAP_API_KEY = "AIzaSyBefyHSIxtoiDf3GAN7z0WkI9nxTna12wA";

	SimpleDateFormat paramsDateFormat = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);

	public interface PREF_KEYS {
		String EMP_NAME = "key_name";
		String EMP_ID = "key_empid";
		String EMP_LOCATION = "key_location";
		String EMP_BATCH = "key_batch";
		String EMP_EMAIL = "key_email";
		String EMP_LG = "key_lg";
		String IS_LOGIN = "is_login";
		String GCM_REG_ID = "gcm_reg_id";
		String BADGE_POINTS = "my_points";
	}

	public interface USER_TABLE_ATTRIB{
		String EMP_ID = "emp_id";
		String EMAIL = "emp_email";
		String NAME = "emp_name";
		String LOCATION = "emp_loc";
		String BATCH = "emp_batch";
	}

	public interface GRAB_A_SEAT{
		String BASE_URL = "http://myilp.org/grabaseat/native/";

		public interface CHECK_ASSOCIATE{
			String URL = BASE_URL + "check_associate.php";
			String PARAM_EMP_ID = "emp_id";
			String KEY_RESULT = "result";
			String VALUE_INVALID = "Invalid";
		}

		public interface GET_TIMINGS{
			String URL = BASE_URL + "get_timings.php";
			String PARAM_TYPE = "type";
			String PARAM_ACCOMMODATION = "accommodation";
			String PARAM_LAP = "lap";
			String PARAM_LG = "lg";

			String KEY_RESULT = "result";
			String VALUE_INVALID = "Invalid";
			String VALUE_NOT_SET = "Not Set";
		}

		public interface CHECK_BOOKING{
			String URL = BASE_URL + "check_booking.php";
			String PARAM_TYPE = "type";
			String PARAM_EMP_ID = "emp_id";

			String KEY_RESULT = "result";
			String VALUE_NOT_BOOKED = "Not Booked";
		}

		public interface PREFERENCES{
			String NAME = "grab_a_seat";

			public interface CHECK_ASSOCIATE{
				String KEY_LG = "lg";
				String KEY_ACCOMMODATION = "accommodation";
				String KEY_LAP = "lap";
			}
		}
	}

	public interface EMP_ERRORS {
		public interface EMAIL {
			int BLANK = 1;
			int INVALID = 2;
		}

		public interface NAME {
			int BLANK = 3;
			int INVALID = 4;
		}

		public interface BATCH {
			int BLANK = 5;
			int INVALID = 6;
		}

		public interface LOCATION {
			int BLANK = 7;
			int INVALID = 8;
		}

		public interface EMP_ID {
			int BLANK = 9;
			int INVALID = 10;
		}

		public interface EMP_LG {
			int BLANK = 11;
			int INVALID = 12;
		}

		int NO_ERROR = 0;
	}

	public interface DRAWER_ITEM_TYPE {
		int HEADER = 1;
		int OPTION = 2;
	}

	public interface NETWORK_PARAMS {
		public interface SCHEDULE {
			String URL = URL_SCHEDULE + QUESTIONMARK;
			String BATCH = "batch";
			String DATE = "date";
		}

		public interface CONTACT {
			String URL = URL_CONTACTS + QUESTIONMARK;
			String ILP = "ilp";
		}

		public interface BADGE {
			String URL = URL_BADGES + QUESTIONMARK;
			String EMPID = "empid";
			String BATCH = "batch";
		}

		public interface FEEDBACK_SUMMARY {
			String FACULTY = "faculty";
			String COURSE = "course";
			String SLOT = "slot";
			String DATE = "date";
		}

		public interface FEEDBACK_SUBMIT {
			String FACULTY = "faculty";
			String COURSE = "course";
			String COMMENT = "comment";
			String RATE = "rate";
			String EMP_ID = "empid";
			String EMP_NAME = "empname";
			String EMP_LOC = "emploc";
			String EMP_BATCH = "empbatch";
			String SLOT = "slot";
			String DATE = "date";
		}

		public interface MAP {
			String LOCATION = "location";
			String KEY = "key";
			String RADIUS = "radius";
			String TYPES = "types";
			String SENSOR = "sensor";
		}

		public interface REGISTER {
			String IMEI = "imei";
			String NAME = "name";
			String EMIAL = "email";
			String EMP_ID = "id";
			String LOCATION = "loc";
			String BATCH = "batch";
		}
	}

	public interface BUNDLE_KEYS {
		public interface FEEDBACK_FRAGMENT {
			String FACULTY = "faculty";
			String COURSE = "course";
			String SLOT_ID = "slot_id";
			String IS_FACULTY = "is_faculty";
		}
	}

	public interface JSON_KEYS {

	}

	public interface LOCATIONS {
		/**
		 * types of locations
		 */
		public interface TYPE {
			String ILP = "ILP";
			String HOSTEL = "Hostel";
		}

		public interface TRIVANDRUM {
			String LOC_NAME = "Trivandrum";
		}

		public interface CHENNAI {
			String LOC_NAME = "Chennai";
		}

		public interface GUWAHATI {
			String LOC_NAME = "Guwahati";
		}

		public interface HYDERABAD {
			String LOC_NAME = "Hyderabad";
		}

		public interface AHMEDABAD {
			String LOC_NAME = "Ahmedabad";
		}
	}

	public interface ACTION_TYPES{
		String WEATHER_INTENT = "weather_intent";
		String INPUT_UNKNOWN = "input.unknown";
	}

	public interface SCHEDULE_INTENT{
		String DATE_KEY = "date";
	}

	public interface WEATHER_API{
		String BASE_URL = "http://api.openweathermap.org/";
		String API_END_POINT = BASE_URL + "data/2.5/weather";

		String TAG_WEATHER = "weather";
		String TAG_WEATHER_ID = "id";
		String TAG_WEATHER_MAIN = "main";
		String TAG_MAIN = "main";
		String TAG_MAIN_TEMP = "temp";
		String TAG_MAIN_PRESSURE = "pressure";
		String TAG_MAIN_HUMIDITY = "humidity";
		String TAG_MAIN_TEMP_MIN = "temp_min";
		String TAG_MAIN_TEMP_MAX = "temp_max";
		String TAG_WIND = "wind";
		String TAG_WIND_SPEED = "speed";
		String TAG_SYS = "sys";
		String TAG_SYS_SUNRISE = "sunrise";
		String TAG_SYS_SUNSET = "sunset";
		String TAG_NAME = "name";

	}

	public interface SEARCH_API{
		String BASE_URL = "https://www.googleapis.com/customsearch/v1/";

		String TAG_ITEMS = "items";
		String TAG_ITEMS_TITLE = "title";
		String TAG_ITEMS_LINK = "link";
		String TAG_ITEMS_SNIPPET = "snippet";
		String TAG_ITEMS_PAGEMAP = "pagemap";
		String TAG_ITEMS_CSE_THUMBNAIL = "cse_thumbnail";
		String TAG_ITEMS_SRC = "src";
	}
}
