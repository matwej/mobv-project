package sk.fei.mobv.pivarci.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.settings.AccountGeneral;
import sk.fei.mobv.pivarci.settings.ComplexPreferences;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private ComplexPreferences complexPreferences;
    private int maxDistance = 2000;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private String poi_type = "pub";

    public SettingsFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity().getApplicationContext(), AccountGeneral.PREFS, Context.MODE_PRIVATE);
        if (complexPreferences.getObject(SecondTaskFragment.DISTANCE_KEY, Integer.class) != null)
            maxDistance = complexPreferences.getObject(SecondTaskFragment.DISTANCE_KEY, Integer.class);
        if (complexPreferences.getObject(SecondTaskFragment.POI_TYPE_KEY, String.class) != null)
            poi_type = complexPreferences.getObject(SecondTaskFragment.POI_TYPE_KEY, String.class);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        spinner = (Spinner) rootView.findViewById(R.id.settings_poi_type);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.poi_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner.setSelection(adapter.getPosition(poi_type));
        ((TextView) rootView.findViewById(R.id.settings_distance)).setText(String.valueOf(maxDistance));

        Button button = (Button) rootView.findViewById(R.id.settings_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                submitOnClick(rootView);
            }
        });

        return rootView;
    }

    private void submitOnClick(View rootView) {
        maxDistance = Integer.valueOf(((TextView) rootView.findViewById(R.id.settings_distance)).getText().toString());

        complexPreferences.putObject(SecondTaskFragment.POI_TYPE_KEY, poi_type);
        complexPreferences.putObject(SecondTaskFragment.DISTANCE_KEY, maxDistance);
        complexPreferences.commit();

        Bundle args = new Bundle();
        args.putString(SecondTaskFragment.POI_TYPE_KEY, poi_type);
        args.putInt(SecondTaskFragment.DISTANCE_KEY, maxDistance);
        Fragment fragment = new SecondTaskFragment();
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        poi_type = adapter.getItem(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}