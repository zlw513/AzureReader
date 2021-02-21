package com.zhlw.azurereader.ui.font;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.application.SysManager;
import com.zhlw.azurereader.bean.Setting;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.enums.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FontsAdapter extends ArrayAdapter<Font> {

    private int mResourceId;
    private Setting setting;
    private Map<Font, Typeface> mTypefaceMap;


    public FontsAdapter(Context context, int resourceId, ArrayList<Font> datas) {
        super(context, resourceId, datas);
        mResourceId = resourceId;
        setting = SysManager.getSetting();
        mTypefaceMap = new HashMap<>();

    }

    @Override
    public void notifyDataSetChanged() {
        setting = SysManager.getSetting();
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(mResourceId, null);
            viewHolder.tvFontName = (TextView) convertView.findViewById(R.id.tv_font_name);
            viewHolder.btnFontUse = (Button) convertView.findViewById(R.id.btn_font_use);
            viewHolder.tvExample = (TextView)convertView.findViewById(R.id.tv_font_example);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initView(position, viewHolder);
        return convertView;
    }

    private void initView(int postion, final ViewHolder viewHolder) {
        final Font font = getItem(postion);
        Typeface typeFace = null;
        if (font != Font.默认字体) {
            if (!mTypefaceMap.containsKey(font)){
                typeFace = Typeface.createFromAsset(getContext().getAssets(), font.path);
                mTypefaceMap.put(font,typeFace);
            }else {
                typeFace = mTypefaceMap.get(font);
            }
        }
        viewHolder.tvExample.setTypeface(typeFace);
//        viewHolder.tvExample.setTypeface(mTypefaceMap.get(font));
        viewHolder.tvFontName.setText(font.toString());

        if (setting.getFont() == font){
            viewHolder.btnFontUse.setBackgroundResource(R.drawable.font_using_btn_bg);
            viewHolder.btnFontUse.setTextColor(getContext().getResources().getColor(R.color.sys_font_using_btn));
            viewHolder.btnFontUse.setText(getContext().getString(R.string.font_using));
            viewHolder.btnFontUse.setOnClickListener(null);
        }else {
            viewHolder.btnFontUse.setBackgroundResource(R.drawable.font_use_btn_bg);
            viewHolder.btnFontUse.setTextColor(getContext().getResources().getColor(R.color.sys_font_use_btn));
            viewHolder.btnFontUse.setText(getContext().getString(R.string.font_use));
            viewHolder.btnFontUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setting.setFont(font);
                    SysManager.saveSetting(setting);
                    notifyDataSetChanged();
                    Intent intent = new Intent();
                    intent.putExtra(APPCONST.FONT,font);
                    ((Activity)getContext()).setResult(Activity.RESULT_OK,intent);
                }
            });
        }


    }


    class ViewHolder {

        TextView tvExample;
        TextView tvFontName;
        Button btnFontUse;

    }

}
