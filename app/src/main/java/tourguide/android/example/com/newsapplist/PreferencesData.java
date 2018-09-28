package tourguide.android.example.com.newsapplist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PreferencesData implements Parcelable {
    private Set<String> sectionNames;
    private Set<String> sectionIds;
    private Set<String> orderDates;
    private Set<String> orderBy;

    public PreferencesData() {
        this.sectionNames = new LinkedHashSet<>();
        this.sectionIds = new LinkedHashSet<>();
        this.orderBy = new LinkedHashSet<>();
        this.orderDates = new LinkedHashSet<>();
    }

    public PreferencesData(Parcel source) {
        List<String> sectionIdsList = source.readArrayList(String.class.getClassLoader());
        this.sectionIds = new LinkedHashSet<>();
        this.sectionIds.addAll(sectionIdsList);

        List<String> sectionNamesList = source.readArrayList(String.class.getClassLoader());
        this.sectionNames = new LinkedHashSet<>();
        this.sectionNames.addAll(sectionIdsList);

    }

    public void addSection(String sectionId, String sectionName) {
        sectionIds.add(sectionId);
        sectionNames.add(sectionName);
    }


    public Set<String> getSectionNames() {
        return sectionNames;
    }

    public Set<String> getSectionIds() {
        return sectionIds;
    }

    public void clear() {
        sectionNames.clear();
        sectionIds.clear();
        orderDates.clear();
        orderBy.clear();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(new ArrayList<String>(sectionIds));
        dest.writeStringList(new ArrayList<String>(sectionNames));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PreferencesData> CREATOR = new Creator<PreferencesData>() {

        @Override
        public PreferencesData createFromParcel(Parcel source) {
            return new PreferencesData(source);
        }

        @Override
        public PreferencesData[] newArray(int size) {
            return new PreferencesData[0];
        }
    };
}
