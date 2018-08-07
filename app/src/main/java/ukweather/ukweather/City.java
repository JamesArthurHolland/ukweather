package ukweather.ukweather;


import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.internal.PointDouble;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

/**
 * Created by jamie on 03/08/18.
 */

public class City
{
    public int id;
    public String country;
    public String name;
    public PointDouble coordinates;

    public City(int id, String country, String name, float lat, float lon) {
        this.id = id;
        this.country = country;
        this.name = name;
        this.coordinates = PointDouble.create(lat, lon);
    }

    @Override
    public String toString() {
        return name;
    }

    public static class CityDeserializer implements JsonDeserializer<City>
    {
        @Override
        public City deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {

            JsonObject jobject = json.getAsJsonObject();
            JsonObject coordiantes = jobject.get("coord").getAsJsonObject();

            return new City(
                jobject.get("id").getAsInt(),
                jobject.get("country").getAsString(),
                jobject.get("name").getAsString(),
                coordiantes.get("lat").getAsFloat(),
                coordiantes.get("lon").getAsFloat()
            );
        }
    }
}
