package ukweather.ukweather;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by jamie on 03/08/18.
 */

public class CityDeserializer implements JsonDeserializer<City>
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
