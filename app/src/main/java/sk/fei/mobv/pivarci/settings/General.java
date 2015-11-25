package sk.fei.mobv.pivarci.settings;

import sk.fei.mobv.pivarci.api.MyServerApi;
import sk.fei.mobv.pivarci.api.ServerApi;

public class General {
    public static final String ACCOUNT_TYPE = "sk.fei.mobv";
    public static final String AUTHTOKEN_TYPE = "MOBV";

    public static final String ARG_USER = "USER";
    public static final String PREFS = "shPrefs";

    public static final String DISTANCE_KEY = "distance";
    public static final String POI_TYPE_KEY = "poitype";
    public static final String CHOSEN_POI_ID_KEY = "chosen_poi_id";
    public static final String CHOSEN_POI_NAME_KEY = "chosen_poi_name";

    public static final ServerApi S_SERVER_API = new MyServerApi();
}
