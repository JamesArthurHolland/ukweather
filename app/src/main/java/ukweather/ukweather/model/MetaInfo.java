package ukweather.ukweather.model;

import com.google.gson.annotations.SerializedName;

public class MetaInfo
{
    @SerializedName("id")
    public int id;
    @SerializedName("main")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public String icon;
}