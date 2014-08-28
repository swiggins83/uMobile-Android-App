package org.apereo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.apache.commons.lang.StringUtils;
import org.apereo.App;
import org.apereo.activities.HomePage;
import org.apereo.activities.PortletWebViewActivity;
import org.apereo.activities.PortletWebViewActivity_;
import org.apereo.adapters.PortletListAdapter;
import org.apereo.constants.AppConstants;
import org.apereo.interfaces.ActionListener;
import org.apereo.models.Portlet;
import org.apereo.utils.LayoutManager;
import java.util.List;

import org.apereo.R;
import org.apereo.utils.Logger;

@EFragment(R.layout.activity_listview)
public class HomePageListFragment extends ListFragment {
    private FadingActionBarHelper mFadingHelper;
    private Bundle mArguments;
    private View view;
    private PortletListAdapter adapter;
    private List<Portlet> portlets = null;
    private final String TAG = HomePageListFragment.class.getName();
    private Activity activity;
    private int position;

    @Bean
    LayoutManager layoutManager;

    private ActionListener actionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = mFadingHelper.createView(inflater);
        Log.d(TAG, "onCreateView");

        if (mArguments != null){
            position = mArguments.getInt(AppConstants.POSITION);
        }

        return view;
    }
    @AfterViews
    void initialize() {
        Logger.d("folder name = ", layoutManager.getLayout().getFolders().get(position).getName());
        portlets = layoutManager.getLayout().getFolders().get(position).getPortlets();
        Logger.d("PORTLETS SIZE = ", ""+portlets.size());
        adapter = new PortletListAdapter(activity, R.layout.portlet_row, portlets);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = parent.getItemAtPosition(position);
                if (o instanceof Portlet) {
                    Portlet p = (Portlet) o;

                    // TODO have a flag in portlet to decide this
                    boolean concat  = !StringUtils.equalsIgnoreCase(p.getIconUrl(), getResources().getString(R.string.use_drawable));

                    actionListener.launchWebView(concat ? App.getRootUrl().concat(p.getUrl()) : p.getUrl());
                }
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        int actionBarBg = R.drawable.ab_background;

        mArguments = getArguments();

        mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(actionBarBg)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.activity_listview);
        mFadingHelper.initActionBar(activity);

        Logger.d(TAG, "onAttach");
        this.activity = activity;


    }
    public void update(int resourseId) {
        portlets = layoutManager.getLayout().getFolders().get(position).getPortlets();
        adapter.notifyDataSetChanged();
    }

    public static Fragment getFragment(ActionListener actionListener) {
        HomePageListFragment fragment = new HomePageListFragment_();
        fragment.setActionListener(actionListener);
        return fragment;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
}

