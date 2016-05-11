package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments.UserArtists;
import tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments.UserProfile;
import tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments.UserSongs;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 29/04/16.
 */
public class BaseFragmentUser extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_profile_base, container, false);

        getIDs(view);
        setEvents();

        this.addUser(getString(R.string.tab_usuario));
        this.addArtist(getString(R.string.tab_artistas));
        this.addSongs(getString(R.string.tab_canciones));

        return view;
    }

    private void getIDs(View view) {

        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);

        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity());
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(1);
    }

    int selectedTabPosition;

    private void setEvents() {

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager.getCurrentItem();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){ //Afegim una opcio "Refresh" al menu del fragment
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.local, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.localMusic) {

            ((MainActivity) getActivity()).abrirPersonalProfile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addUser(String pagename) {
        Bundle bundle = new Bundle();
        bundle.putString("data", pagename);
        UserProfile fragmentChild = new UserProfile();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, pagename);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0)
            tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(adapter.getCount() - 1);
        setupTabLayout();
    }

    public void addArtist(String pagename) {
        Bundle bundle = new Bundle();
        bundle.putString("data", pagename);
        UserArtists fragmentChild = new UserArtists();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, pagename);
        adapter.notifyDataSetChanged();

        if (adapter.getCount() > 0)
            tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(adapter.getCount() - 1);
        setupTabLayout();
    }

    public void addSongs(String pagename) {
        Bundle bundle = new Bundle();
        bundle.putString("data", pagename);
        UserSongs fragmentChild = new UserSongs();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, pagename);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0)
            tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(adapter.getCount() - 1);
        setupTabLayout();
    }

    public void setupTabLayout() {
        selectedTabPosition = viewPager.getCurrentItem();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));

        }
    }
}