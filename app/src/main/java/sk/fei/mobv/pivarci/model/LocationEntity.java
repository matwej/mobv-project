package sk.fei.mobv.pivarci.model;

import java.util.List;
import java.util.Map;

public class LocationEntity {
    private String version;
    private String generator;
    private Map<String, String> osm3s;
    private List<LocationItem> elements;

    public LocationEntity() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public Map<String, String> getOsm3s() {
        return osm3s;
    }

    public void setOsm3s(Map<String, String> osm3s) {
        this.osm3s = osm3s;
    }

    public List<LocationItem> getElements() {
        return elements;
    }

    public void setElements(List<LocationItem> elements) {
        this.elements = elements;
    }
}