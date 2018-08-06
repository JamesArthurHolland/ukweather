package ukweather.ukweather;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;

/**
 * Created by jamie on 04/08/18.
 */

public class GsonHelper
{
    public static final JsonDeserializer<LocalDateTime> LDT_DESERIALIZER = new JsonDeserializer<LocalDateTime>() {
        @Override
        public LocalDateTime deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
            try {

                // if provided as String - '2011-12-03T10:15:30+01:00[Europe/Paris]'
                if(jsonPrimitive.isString()){
                    return LocalDateTime.parse(jsonPrimitive.getAsString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
                }

                // if provided as Long
                if(jsonPrimitive.isNumber()){
                    return LocalDateTime.ofInstant(Instant.ofEpochSecond(jsonPrimitive.getAsLong()), ZoneId.systemDefault());
                }

            } catch(RuntimeException e){
                throw new JsonParseException("Unable to parse ZonedDateTime", e);
            }
            throw new JsonParseException("Unable to parse ZonedDateTime");
        }
    };
}
