/*
 * Copyright (C) 2015 Maros Cavojsky, (mpage.sk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.fei.mobv.pivarci;

import android.accounts.AccountManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sk.fei.mobv.pivarci.drawer.CustomAdapter;
import sk.fei.mobv.pivarci.drawer.MenuItemObject;
import sk.fei.mobv.pivarci.fragments.ChatFragment;
import sk.fei.mobv.pivarci.fragments.ChatPoiFragment;
import sk.fei.mobv.pivarci.fragments.GeneralFragment;
import sk.fei.mobv.pivarci.fragments.NumberFragment;
import sk.fei.mobv.pivarci.fragments.PoiFragment;
import sk.fei.mobv.pivarci.fragments.ProfileFragment;
import sk.fei.mobv.pivarci.fragments.SettingsFragment;
import sk.fei.mobv.pivarci.model.User;
import sk.fei.mobv.pivarci.settings.ComplexPreferences;
import sk.fei.mobv.pivarci.settings.General;

public class MainActivity extends AppCompatActivity {

    private String[] titles;
    int[] titleIcons = {
            R.drawable.ic_profile,
            R.drawable.ic_edit,
            R.drawable.ic_compass,
            android.R.drawable.ic_dialog_email,
            android.R.drawable.star_big_on
    };
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar topToolBar;
    private ComplexPreferences complexPreferences;

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    private String username;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set title
        mTitle = mDrawerTitle = getTitle();
        // set toolbar
        topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        // set main drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set drawer header
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.header_list, null, false);
        // set preferences
        complexPreferences = ComplexPreferences.getComplexPreferences(getApplicationContext(), General.PREFS, MODE_PRIVATE);
        // set user
        user = complexPreferences.getObject("user", User.class);
        username = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        // set drawer header content
        TextView username = (TextView) listHeaderView.findViewById(R.id.profile_username);
        username.setText(complexPreferences.getObject(AccountManager.KEY_ACCOUNT_NAME,String.class));
        TextView full_name = (TextView) listHeaderView.findViewById(R.id.profile_name);
        full_name.setText(user.getFullName());
        mDrawerList.addHeaderView(listHeaderView);
        // set drawer menu items
        titles = getResources().getStringArray(R.array.nav_items);
        List<MenuItemObject> listViewItems = new ArrayList<>();
        for (int i = 1; i < titles.length; i++) {
            listViewItems.add(new MenuItemObject(titles[i], titleIcons[i]));
        }
        // set adapter for items
        mDrawerList.setAdapter(new CustomAdapter(this, listViewItems));
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // set drawer listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFragment(position);
            }
        });
        // if nothing selected, set default first one
        if (savedInstanceState == null) {
            selectItemFragment(1);
        }

    }

    private void selectItemFragment(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();

        switch (position) {
            case 0:
                fragment = new ProfileFragment();
                break;
            case 1:
                fragment = new NumberFragment();
                break;
            case 2:
                fragment = new PoiFragment();
                break;
            case 3:
                fragment = new ChatFragment();
                break;
            case 4:
                fragment = new ChatPoiFragment();
                break;
            default:
                fragment = new GeneralFragment();
                break;
        }

        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(titles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(item.getItemId()) {
            case R.id.settings:
                Fragment fragment = new SettingsFragment();
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
