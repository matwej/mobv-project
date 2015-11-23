package sk.fei.mobv.pivarci.api;

import java.util.List;

import sk.fei.mobv.pivarci.model.LocationItem;

public interface OverpassInt {

    void onBackgroundTaskCompleted(List<LocationItem> items);

}
