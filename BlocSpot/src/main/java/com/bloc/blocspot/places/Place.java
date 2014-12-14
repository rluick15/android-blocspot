package com.bloc.blocspot.places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Model class for Places data.
 *
 * @author Karn Shah
 * @Date   10/3/2013
 *
 */
public class Place {
    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;
    private JSONArray types;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public JSONArray getTypes() {
        return types;
    }

    public void setTypes(JSONArray types) {
        this.types = types;
    }

    static Place jsonToReferencePoint(JSONObject referencePoint) {
        try {
            Place result = new Place();

            JSONObject geometry = (JSONObject) referencePoint.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(referencePoint.getString("icon"));
            result.setName(referencePoint.getString("name"));
            result.setVicinity(referencePoint.getString("vicinity"));
            result.setId(referencePoint.getString("id"));
            result.setTypes(referencePoint.getJSONArray("types"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name
                + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

}