package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios;

import tk.sbarjola.pa.featherlyricsapp.R;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by 46465442z on 29/04/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();
    Context context;

    public ViewPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public View getTabView(int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tabItemName = (TextView) view.findViewById(R.id.textViewTabItemName);

        tabItemName.setText(mFragmentTitleList.get(position));
        tabItemName.setTextColor(context.getResources().getColor(android.R.color.background_light));

        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}