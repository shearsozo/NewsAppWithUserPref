package tourguide.android.example.com.newsapplist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PreferencesData implements Parcelable {
    private List<String> sectionNames;
    private List<String> sectionIds;

    public PreferencesData() {
        this.sectionNames = new ArrayList<>();
        this.sectionIds = new ArrayList<>();
    }

    public PreferencesData(Parcel source) {
        this.sectionIds = source.createStringArrayList();
        this.sectionNames = source.createStringArrayList();
    }

    public void addSection(String sectionId, String sectionName) {
        if(sectionIds.contains(sectionId)) {
            return;
        }
        sectionIds.add(sectionId);
        sectionNames.add(sectionName);
    }


    public List<String> getSectionNames() {
        return sectionNames;
    }

    public List<String> getSectionIds() {
        return sectionIds;
    }

    public void clear() {
        sectionNames.clear();
        sectionIds.clear();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(sectionIds);
        dest.writeStringList(sectionNames);
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
