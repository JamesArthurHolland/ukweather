package ukweather.ukweather.model;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by jamie on 06/08/18.
 */

public class WeatherDay
{
    public LocalDate date;
    public ArrayList<WeatherCondition> conditions;

    public WeatherDay(LocalDate date, ArrayList<WeatherCondition> conditions) {
        this.date = date;
        this.conditions = conditions;
    }
}
