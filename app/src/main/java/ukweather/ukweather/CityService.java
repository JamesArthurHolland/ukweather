package ukweather.ukweather;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.internal.PointDouble;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by jamie on 06/08/18.
 */
@Singleton
public class CityService
{
    private Context mContext;
    public ArrayList<City> mCities;
    private RTree<String, Point> mRtree;
    private HashMap<String, City> mCityNameLookupById;

    @Inject
    public CityService(Context context)
    {
        mContext = context;
        parseCitiesFromAssetsFile();
        mRtree = RTree.create();
        mCityNameLookupById = new HashMap<>();
        createRTreeAndDictionaryFromCities();
    }

    public City getNearestCity(PointDouble currentLocation)
    {
        List<Entry<String, Point>> list = mRtree.nearest(currentLocation, Integer.MAX_VALUE, 1).toList().toBlocking().single();
        int size = list.size();
        int id = Integer.parseInt(list.get(0).value());
        if (size > 0) {
            return mCityNameLookupById.get("" + id);
        }
        return null;
    }

    private void parseCitiesFromAssetsFile() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(City.class, new City.CityDeserializer());
        Gson gson = gsonBuilder.create();

        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream inputStream = assetManager.open("ukcities.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            mCities = gson.fromJson(bufferedReader, new TypeToken<ArrayList<City>>()
            {
            }.getType()); // line 6
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createRTreeAndDictionaryFromCities() {
        for (City city : mCities) {
            int id = city.id;
            Point point = city.coordinates;
            mRtree = mRtree.add("" + id, Geometries.point(point.x(), point.y()));

            mCityNameLookupById.put("" + id, city);
        }
    }
}
