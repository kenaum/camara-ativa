package br.gov.ms.camara.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.gov.ms.camara.R;

/**
 * Created by Elisa Freitas on 01/03/2016.
 */
public class AdapterListMainMenu extends BaseAdapter {
    private Activity activity;
    private String[] menuLabel;
    private Integer[] menuIcons;
    private LinearLayout lineMainMenu;
    private LinearLayout lineEmail;
    private TextView lbEmail;
    private TextView lbNome;
    private ImageView imageMenu;

    public AdapterListMainMenu() {

    }

    public AdapterListMainMenu(Activity activity, String[] menuLabel, Integer[] menuIcons) {
        this.menuLabel = menuLabel;
        this.menuIcons = menuIcons;
        this.activity  = activity;
    }

    @Override
    public int getCount() {
        return menuLabel.length;
    }

    @Override
    public Object getItem(int pos) {
        return menuLabel[pos];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_main_menu_adapter, null);
        }

        Integer intMenuIcon = menuIcons[position];
        String strMenuLabel = menuLabel[position];

        ImageView menuIcon = (ImageView) view.findViewById(R.id.menuIcon);
        TextView lbNome = (TextView) view.findViewById(R.id.list_item_string);

        menuIcon.setImageDrawable(activity.getResources().getDrawable(intMenuIcon));
        lbNome.setText(strMenuLabel);

        return view;
    }
}