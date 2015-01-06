package com.bloc.blocspot.utils;

/**
 * This class holds all the constants for the projects
 */
public class Constants {

    //SavedInstanceState Constants
    public static final String LIST_STATE = "listState";
    public static final String QUERY_TEXT = "queryText";
    public static final String FILTER_TEXT = "filterText";
    public static final String EDIT_NOTE_TEXT = "editNoteText";

    //Shared Preferences
    public static final String MAIN_PREFS ="mainPrefs";
    public static final String GEOFENCE_PREFS ="geofencePrefs";
    public static final String CATEGORY_ARRAY = "categoryArray";

    //Geofence Constants
    public static final String KEY_ID ="com.example.android.geofence.KEY_ID";
    public static final String KEY_LATITUDE ="com.example.android.geofence.KEY_LATITUDE";
    public static final String KEY_LONGITUDE ="com.example.android.geofence.KEY_LONGITUDE";
    public static final String KEY_RADIUS ="com.example.android.geofence.KEY_RADIUS";
    public static final String KEY_EXPIRATION_DURATION ="com.example.android.geofence.KEY_EXPIRATION_DURATION";
    public static final String KEY_TRANSITION_TYPE ="com.example.android.geofence.KEY_TRANSITION_TYPE";
    public static final String KEY_PREFIX ="com.example.android.geofence.KEY";
    public static final String APPTAG = "Geofence Detection";

    public static final long INVALID_LONG_VALUE = -999l;
    public static final float INVALID_FLOAT_VALUE = -999.0f;
    public static final int INVALID_INT_VALUE = -999;
    public static final float GEOFENCE_RADIUS = 160.934f;
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //API Key
    public static final String API_KEY = "AIzaSyCdMYv2IzTm331hPXmgfUJCvvZmw9C2ZxI";

    //Database Constants
        //Database
        public static final String DATABASE_NAME = "BlocSpot";
        public static final int DATABASE_VERSION = 14;
        //Tables
        public static final String TABLE_POI_NAME = "poiTable";
            //Table Columns
            public static final String TABLE_COLUMN_ID = "_id";
                //POI
                public static final String TABLE_COLUMN_POI_NAME = "name";
                public static final String TABLE_COLUMN_LATITUDE= "lat";
                public static final String TABLE_COLUMN_LONGITUDE= "lng";
                public static final String TABLE_COLUMN_CAT_NAME= "catName";
                public static final String TABLE_COLUMN_CAT_COLOR= "catColor";
                public static final String TABLE_COLUMN_NOTE= "note";
                public static final String TABLE_COLUMN_VISITED = "visited";
                public static final String TABLE_COLUMN_GEO_ID = "geoId";

    //JSON Search Constants
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    public static final String ALL_PLACE_TYPES = "&types=airport|amusement_park|aquarium|" +
            "art_gallery|atm|bakery|bank|bar|beauty_salon|bicycle_store|book_store|bowling_alley|" +
            "bus_station|cafe|campground|car_dealer|car_rental|car_repair|car_wash|casino|cemetery|" +
            "church|city_hall|clothing_store|convenience_store|courthouse|department_store|" +
            "electrician|electronics_store|embassy|establishment|fire_station|" +
            "florist|food|funeral_home|furniture_store|gas_station|" +
            "grocery_or_supermarket|gym|hair_care|hardware_store|health|hindu_temple|" +
            "home_goods_store|hospital|jewelry_store|laundry|library|" +
            "liquor_store|local_government_office|lodging|meal_delivery|meal_takeaway|" +
            "mosque|movie_rental|movie_theater|museum|night_club|park|" +
            "pet_store|pharmacy|place_of_worship|police|post_office|" +
            "restaurant|rv_park|school|shoe_store|" +
            "shopping_mall|spa|stadium|storage|store|subway_station|synagogue|" +
            "train_station|university|zoo";
    public static final String LOCATION = "&location=";
    public static final String KEYWORD = "&keyword=";
    public static final String SENSOR_AND_KEY = "&sensor=false&key=";
    public static final String RANK_BY_DISTANCE = "&rankby=distance";

    //Colors
    public static final String CYAN = "cyan";
    public static final String BLUE = "blue";
    public static final String GREEN = "green";
    public static final String MAGENTA = "magenta";
    public static final String ORANGE = "orange";
    public static final String RED = "red";
    public static final String ROSE = "rose";
    public static final String VIOLET = "violet";
    public static final String YELLOW = "yellow";

    //Misc
    public static final String EMPTY_STRING = "";
    public static final String COMMA = ",";
    public static final String CATEGORY_UNCATEGORIZED = "Uncategorized";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String INTENT_TYPE_TEXT_PLAIN = "text/plain";
    


}
