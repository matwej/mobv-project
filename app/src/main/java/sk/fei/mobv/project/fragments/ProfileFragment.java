package sk.fei.mobv.project.fragments;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sk.fei.mobv.project.R;
import sk.fei.mobv.project.model.User;
import sk.fei.mobv.project.settings.AccountGeneral;
import sk.fei.mobv.project.settings.ComplexPreferences;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), AccountGeneral.PREFS, Context.MODE_PRIVATE);
        final User user = complexPreferences.getObject("user", User.class);

        TextView id = (TextView) rootView.findViewById(R.id.profile_id);
        id.setText(String.valueOf(user.getUser_id()));
        TextView first_name = (TextView) rootView.findViewById(R.id.first_name);
        first_name.setText(user.getFirst_name());
        TextView surname = (TextView) rootView.findViewById(R.id.surname);
        surname.setText(user.getLast_name());
        TextView username = (TextView) rootView.findViewById(R.id.username);
        username.setText(complexPreferences.getObject(AccountManager.KEY_ACCOUNT_NAME,String.class));

        return rootView;
    }

}
