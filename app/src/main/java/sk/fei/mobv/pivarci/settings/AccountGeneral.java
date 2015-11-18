package sk.fei.mobv.pivarci.settings;

import sk.fei.mobv.pivarci.api.MyServerApi;
import sk.fei.mobv.pivarci.api.ServerApi;

public class AccountGeneral {
    public static final String ACCOUNT_TYPE = "sk.fei.mobv";
    public static final String AUTHTOKEN_TYPE = "MOBV";

    public static final String ARG_USER = "USER";
    public static final String PREFS = "shPrefs";

    public static final ServerApi S_SERVER_API = new MyServerApi();
}