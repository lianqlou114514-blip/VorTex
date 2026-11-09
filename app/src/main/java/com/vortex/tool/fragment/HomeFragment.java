package com.vortex.tool.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vortex.tool.FloatBallService;
import com.vortex.tool.HomeActivity;
import com.vortex.tool.R;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(getActivity());
        root.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#FAFAFA"));
        root.setPadding(dp2px(20), dp2px(60), dp2px(20), 0);

        // 标题
        TextView title = new TextView(getActivity());
        title.setText("VorTex");
        title.setTextSize(32);
        title.setTextColor(Color.parseColor("#212121"));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        // 间距
        View spacer1 = new View(getActivity());
        spacer1.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp2px(24)));
        root.addView(spacer1);

        // 公告卡片
        LinearLayout noticeCard = new LinearLayout(getActivity());
        noticeCard.setOrientation(LinearLayout.VERTICAL);
        noticeCard.setBackground(createRoundRectDrawable(Color.WHITE, dp2px(16), Color.parseColor("#E0E0E0")));
        noticeCard.setPadding(dp2px(16), dp2px(20), dp2px(16), dp2px(20));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        noticeCard.setLayoutParams(cardParams);

        TextView noticeTitle = new TextView(getActivity());
        noticeTitle.setText("公告");
        noticeTitle.setTextSize(16);
        noticeTitle.setTextColor(Color.parseColor("#212121"));
        noticeTitle.setTypeface(Typeface.DEFAULT_BOLD);
        noticeCard.addView(noticeTitle);

        TextView noticeContent = new TextView(getActivity());
        noticeContent.setText("欢迎使用 VorTex，本应用提供悬浮窗辅助功能，请遵守相关法律法规使用。\n\n当前版本：1.0\n如有问题请联系开发者。");
        noticeContent.setTextSize(14);
        noticeContent.setTextColor(Color.parseColor("#757575"));
        noticeContent.setPadding(0, dp2px(12), 0, 0);
        noticeContent.setLineSpacing(dp2px(4), 1.0f);
        noticeCard.addView(noticeContent);

        root.addView(noticeCard);

        // 间距
        View spacer2 = new View(getActivity());
        spacer2.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp2px(24)));
        root.addView(spacer2);

        // 启动悬浮窗卡片
        LinearLayout floatCard = createActionCard("启动悬浮窗", R.drawable.ic_float_window, Color.parseColor("#2196F3"), v -> {
            var activity = getActivity();
            if (activity == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(activity)) {
                Toast.makeText(activity, "请先授予悬浮窗权限", Toast.LENGTH_LONG).show();
                var intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            } else {
                activity.startService(new Intent(activity, FloatBallService.class));
                Toast.makeText(activity, "悬浮球已启动", Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(floatCard);

        // 间距
        View spacer3 = new View(getActivity());
        spacer3.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp2px(16)));
        root.addView(spacer3);

        // 资源管理入口卡片
        LinearLayout resourceCard = createActionCard("资源管理", R.drawable.ic_resource, Color.parseColor("#2196F3"), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
                    .replace(HomeActivity.CONTENT_CONTAINER_ID, new ResourceFragment())
                    .addToBackStack("resource")
                    .commit();
            }
        });
        root.addView(resourceCard);

        return root;
    }

    private LinearLayout createActionCard(String label, int iconRes, int iconBgColor, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(getActivity());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setBackground(createRoundRectDrawable(Color.WHITE, dp2px(16), Color.parseColor("#E0E0E0")));
        card.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));
        card.setClickable(true);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(cardParams);
        card.setOnClickListener(listener);

        // 蓝色圆形图标容器
        FrameLayout iconWrap = new FrameLayout(getActivity());
        iconWrap.setLayoutParams(new LinearLayout.LayoutParams(dp2px(44), dp2px(44)));
        View iconBg = new View(getActivity());
        iconBg.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        iconBg.setBackground(createCircleDrawable(iconBgColor));
        iconWrap.addView(iconBg);

        // 矢量图标 ImageView
        ImageView iconView = new ImageView(getActivity());
        iconView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iconView.setPadding(dp2px(10), dp2px(10), dp2px(10), dp2px(10));
        iconView.setImageResource(iconRes);
        iconView.setColorFilter(Color.WHITE);
        iconWrap.addView(iconView);

        card.addView(iconWrap);

        // 文字
        TextView text = new TextView(getActivity());
        text.setText(label);
        text.setTextSize(16);
        text.setTextColor(Color.parseColor("#212121"));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.leftMargin = dp2px(16);
        text.setLayoutParams(textParams);
        card.addView(text);

        // 右侧箭头
        TextView arrow = new TextView(getActivity());
        arrow.setText(">");
        arrow.setTextSize(18);
        arrow.setTextColor(Color.parseColor("#AAAAAA"));
        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        arrowParams.leftMargin = dp2px(8);
        arrow.setLayoutParams(arrowParams);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) arrow.getLayoutParams();
        lp.weight = 1;
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        arrow.setLayoutParams(lp);
        arrow.setGravity(Gravity.END);
        card.addView(arrow);

        return card;
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

    private int dp2px(int dp) {
        return (int) (dp * getActivity().getResources().getDisplayMetrics().density + 0.5f);
    }
}
