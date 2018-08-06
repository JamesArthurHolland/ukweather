package ukweather.ukweather;


import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.internal.PointDouble;
import com.google.gson.annotations.SerializedName;

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
}
