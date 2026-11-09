package com.vortex.tool.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.vortex.tool.R;

public class SettingsFragment extends Fragment {

    private static final int COLOR_BG = Color.parseColor("#FAFAFA");
    private static final int COLOR_CARD = Color.parseColor("#FFFFFF");
    private static final int COLOR_TEXT_PRIMARY = Color.parseColor("#212121");
    private static final int COLOR_TEXT_SECONDARY = Color.parseColor("#757575");
    private static final int COLOR_TEXT_HINT = Color.parseColor("#AAAAAA");
    private static final int COLOR_DIVIDER = Color.parseColor("#E0E0E0");
    private static final int COLOR_BLUE = Color.parseColor("#2196F3");
    private static final int COLOR_SECTION_TEXT = Color.parseColor("#757575");

    private static final String PREFS_NAME = "vortex_settings";
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(PREFS_NAME, 0);

        ScrollView scroll = new ScrollView(getActivity());
        scroll.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        scroll.setBackgroundColor(COLOR_BG);
        scroll.setPadding(0, dp2px(60), 0, dp2px(20));

        LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll.addView(root);

        // 页面标题
        root.addView(createPageTitle("设置"));

        root.addView(createGap(dp2px(12)));

        // 应用设置
        root.addView(createSectionTitle("应用"));
        root.addView(createToggleItem("防录屏", "anti_screen_record", false));
        root.addView(createDivider());
        root.addView(createToggleItem("聊天室", "map_channel", true));
        root.addView(createDivider());
        root.addView(createToggleItem("切换加载方式", "load_mode", true));
        root.addView(createDivider());
        root.addView(createArrowItem("关于应用", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "关于应用", Toast.LENGTH_SHORT).show();
            }
        }));
        root.addView(createDivider());
        root.addView(createArrowItem("清理缓存", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "缓存已清理", Toast.LENGTH_SHORT).show();
            }
        }));

        root.addView(createGap(dp2px(12)));

        // 游戏设置
        root.addView(createSectionTitle("游戏"));
        root.addView(createToggleItem("自动启动游戏", "auto_start_game", true));
        root.addView(createDivider());
        root.addView(createArrowItem("选择游戏", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "选择游戏", Toast.LENGTH_SHORT).show();
            }
        }));
        root.addView(createDivider());
        root.addView(createArrowItem("下载游戏", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "下载游戏", Toast.LENGTH_SHORT).show();
            }
        }));

        return scroll;
    }

    private TextView createPageTitle(String title) {
        TextView tv = new TextView(getActivity());
        tv.setText(title);
        tv.setTextSize(32);
        tv.setTextColor(Color.parseColor("#212121"));
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(dp2px(20), 0, dp2px(20), 0);
        return tv;
    }

    private TextView createSectionTitle(String title) {
        TextView tv = new TextView(getActivity());
        tv.setText(title);
        tv.setTextSize(13);
        tv.setTextColor(COLOR_SECTION_TEXT);
        tv.setPadding(dp2px(20), dp2px(12), dp2px(20), dp2px(8));
        return tv;
    }

    private LinearLayout createToggleItem(String label, String key, boolean defaultValue) {
        LinearLayout row = new LinearLayout(getActivity());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(COLOR_CARD);
        row.setPadding(dp2px(20), dp2px(14), dp2px(20), dp2px(14));
        row.setClickable(true);
        row.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelView = new TextView(getActivity());
        labelView.setText(label);
        labelView.setTextSize(15);
        labelView.setTextColor(COLOR_TEXT_PRIMARY);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        labelView.setLayoutParams(labelParams);
        row.addView(labelView);

        Switch sw = new Switch(getActivity());
        boolean checked = prefs.getBoolean(key, defaultValue);
        sw.setChecked(checked);
        sw.setClickable(false);
        sw.setFocusable(false);

        int[][] states = new int[][]{
            new int[]{android.R.attr.state_checked},
            new int[]{-android.R.attr.state_checked}
        };
        int[] thumbColors = new int[]{COLOR_BLUE, Color.parseColor("#BDBDBD")};
        int[] trackColors = new int[]{Color.parseColor("#B3E5FC"), Color.parseColor("#E0E0E0")};
        sw.setThumbTintList(new ColorStateList(states, thumbColors));
        sw.setTrackTintList(new ColorStateList(states, trackColors));

        sw.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(key, isChecked).apply();
            }
        });
        row.addView(sw);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sw.setChecked(!sw.isChecked());
            }
        });

        return row;
    }

    private LinearLayout createArrowItem(String label, Runnable onClick) {
        LinearLayout row = new LinearLayout(getActivity());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        applyRippleRect(row, COLOR_CARD);
        row.setPadding(dp2px(20), dp2px(14), dp2px(20), dp2px(14));
        row.setClickable(true);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.run();
            }
        });
        row.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelView = new TextView(getActivity());
        labelView.setText(label);
        labelView.setTextSize(15);
        labelView.setTextColor(COLOR_TEXT_PRIMARY);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        labelView.setLayoutParams(labelParams);
        row.addView(labelView);

        TextView arrow = new TextView(getActivity());
        arrow.setText(">");
        arrow.setTextSize(18);
        arrow.setTextColor(COLOR_TEXT_HINT);
        row.addView(arrow);

        return row;
    }

    private View createDivider() {
        View div = new View(getActivity());
        div.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        div.setBackgroundColor(COLOR_DIVIDER);
        return div;
    }

    private View createGap(int height) {
        View gap = new View(getActivity());
        gap.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height));
        return gap;
    }

    private GradientDrawable createRoundRectDrawable(int fillColor, int radius, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(fillColor);
        if (strokeColor != 0) {
            drawable.setStroke(2, strokeColor);
        }
        return drawable;
    }

    private GradientDrawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private void applyRipple(View view, int bgColor, int radius, int strokeColor) {
        GradientDrawable normal = createRoundRectDrawable(bgColor, radius, strokeColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(new RippleDrawable(
                ColorStateList.valueOf(Color.parseColor("#1F000000")),
                normal,
                normal
            ));
        } else {
            view.setBackground(normal);
        }
    }

    private void applyRippleRect(View view, int bgColor) {
        GradientDrawable normal = new GradientDrawable();
        normal.setShape(GradientDrawable.RECTANGLE);
        normal.setColor(bgColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(new RippleDrawable(
                ColorStateList.valueOf(Color.parseColor("#1F000000")),
                normal,
                normal
            ));
        } else {
            view.setBackground(normal);
        }
    }

    private int dp2px(int dp) {
        return (int) (dp * getActivity().getResources().getDisplayMetrics().density + 0.5f);
    }
}
