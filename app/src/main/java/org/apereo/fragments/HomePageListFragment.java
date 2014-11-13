package org.apereo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.apache.commons.lang.StringUtils;
import org.apereo.App;
import org.apereo.R;
import org.apereo.adapters.PortletListAdapter;
import org.apereo.constants.AppConstants;
import org.apereo.interfaces.IActionListener;
import org.apereo.models.Portlet;
import org.apereo.utils.LayoutManager;

import java.util.List;

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

    private IActionListener actionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = mFadingHelper.createView(inflater);

        if (mArguments != null){
            position = mArguments.getInt(AppConstants.POSITION);
        }

        return view;
    }

    @AfterViews
    void initialize() {
        portlets = layoutManager.getLayout().getFolders().get(position).getPortlets();
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

                    actionListener.launchWebView(p.getName(), concat ? App.getRootUrl().concat(p.getUrl()) : p.getUrl());
                }
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mArguments = getArguments();

        mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(R.color.theme_primary)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.activity_listview);
        mFadingHelper.initActionBar(activity);

        this.activity = activity;
    }

    public void update(int resourseId) {
        portlets = layoutManager.getLayout().getFolders().get(position).getPortlets();
        adapter.notifyDataSetChanged();
    }

    public static Fragment getFragment(IActionListener actionListener) {
        HomePageListFragment fragment = new HomePageListFragment_();
        fragment.setActionListener(actionListener);
        return fragment;
    }

    public void setActionListener(IActionListener actionListener) {
        this.actionListener = actionListener;
    }
}

