package sk.fei.mobv.pivarci.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sk.fei.mobv.pivarci.R;
import sk.fei.mobv.pivarci.model.User;
import sk.fei.mobv.pivarci.settings.ComplexPreferences;
import sk.fei.mobv.pivarci.settings.General;

import static sk.fei.mobv.pivarci.settings.General.S_SERVER_API;

public class FirstTaskFragment extends Fragment {

    private ComplexPreferences complexPreferences;

    public FirstTaskFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_task, container, false);

        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), General.PREFS, Context.MODE_PRIVATE);
        final User user = complexPreferences.getObject("user", User.class);

        TextView mId = (TextView) rootView.findViewById(R.id.results_id);
        mId.setText(String.valueOf(user.getUser_id()));

        final TextView textView = (TextView) rootView.findViewById(R.id.results_number);
        rootView.findViewById(R.id.number_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AsyncTask<String, Void, Integer>() {

                    @Override
                    protected Integer doInBackground(String... params) {
                        try {
                            return S_SERVER_API.sendRandomNumber(user.getSession_token());
                        } catch (Exception e) {
                        }
                        return -1;
                    }

                    @Override
                    protected void onPostExecute(Integer value) {
                        textView.setText(Integer.toString(value));
                    }
                }.execute();
            }
        });

        return rootView;
    }



}
