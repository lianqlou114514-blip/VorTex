package com.vortex.tool.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vortex.tool.R;
import com.vortex.tool.MockData;
import com.vortex.tool.model.Resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceFragment extends Fragment {

    private static final String[] CATEGORIES = {"全部", "工具", "材质"};
    private static final int COLOR_BG = Color.parseColor("#FAFAFA");
    private static final int COLOR_CARD = Color.parseColor("#FFFFFF");
    private static final int COLOR_TEXT_PRIMARY = Color.parseColor("#212121");
    private static final int COLOR_TEXT_SECONDARY = Color.parseColor("#666666");
    private static final int COLOR_HINT = Color.parseColor("#AAAAAA");
    private static final int COLOR_DIVIDER = Color.parseColor("#E0E0E0");
    private static final int COLOR_BLUE = Color.parseColor("#2196F3");
    private static final int COLOR_GREEN = Color.parseColor("#4CAF50");
    private static final int COLOR_RED = Color.parseColor("#E53935");

    private String currentCategory = "全部";
    private LinearLayout resourceListContainer;
    private List<TextView> categoryTabViews = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();
    private SharedPreferences downloadPrefs;
    private SharedPreferences applyPrefs;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        downloadPrefs = getActivity().getSharedPreferences("downloads", Context.MODE_PRIVATE);
        applyPrefs = getActivity().getSharedPreferences("applied", Context.MODE_PRIVATE);
        initResources();
        loadDownloadProgress();

        LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT));
        root.setBackgroundColor(COLOR_BG);

        root.addView(createTitleBar());
        root.addView(createDivider());
        root.addView(createCategoryTabs());
        root.addView(createDivider());

        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        root.addView(scrollView);

        resourceListContainer = new LinearLayout(getActivity());
        resourceListContainer.setOrientation(LinearLayout.VERTICAL);
        resourceListContainer.setPadding(dp2px(12), dp2px(8), dp2px(12), dp2px(8));
        resourceListContainer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        scrollView.addView(resourceListContainer);

        refreshResourceList();

        return root;
    }

    private LinearLayout createTitleBar() {
        LinearLayout titleBar = new LinearLayout(getActivity());
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp2px(56)));
        titleBar.setBackgroundColor(COLOR_CARD);

        TextView backBtn = new TextView(getActivity());
        backBtn.setText("\u2190");
        backBtn.setTextSize(24);
        backBtn.setTextColor(COLOR_TEXT_PRIMARY);
        backBtn.setGravity(Gravity.CENTER);
        backBtn.setPadding(dp2px(16), 0, dp2px(16), 0);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        titleBar.addView(backBtn);

        TextView title = new TextView(getActivity());
        title.setText("资源管理");
        title.setTextSize(18);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        titleBar.addView(title);

        return titleBar;
    }

    private HorizontalScrollView createCategoryTabs() {
        HorizontalScrollView scrollView = new HorizontalScrollView(getActivity());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setBackgroundColor(COLOR_CARD);

        LinearLayout tabs = new LinearLayout(getActivity());
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setGravity(Gravity.CENTER_VERTICAL);
        tabs.setPadding(dp2px(12), dp2px(10), dp2px(12), dp2px(10));
        tabs.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        scrollView.addView(tabs);

        for (String category : CATEGORIES) {
            TextView tab = new TextView(getActivity());
            tab.setText(category);
            tab.setTextSize(14);
            tab.setPadding(dp2px(16), dp2px(6), dp2px(16), dp2px(6));
            tab.setGravity(Gravity.CENTER);

            if (category.equals(currentCategory)) {
                tab.setTextColor(Color.WHITE);
                tab.setBackground(createRoundRectDrawable(COLOR_BLUE, dp2px(16), 0));
            } else {
                tab.setTextColor(COLOR_TEXT_SECONDARY);
                tab.setBackground(createRoundRectDrawable(Color.parseColor("#F0F0F0"), dp2px(16), 0));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = dp2px(8);
            tab.setLayoutParams(params);

            tab.setOnClickListener(v -> {
                currentCategory = category;
                updateCategoryStyles();
                refreshResourceList();
            });

            categoryTabViews.add(tab);
            tabs.addView(tab);
        }

        return scrollView;
    }

    private void updateCategoryStyles() {
        for (TextView tab : categoryTabViews) {
            if (tab.getText().toString().equals(currentCategory)) {
                tab.setTextColor(Color.WHITE);
                tab.setBackground(createRoundRectDrawable(COLOR_BLUE, dp2px(16), 0));
            } else {
                tab.setTextColor(COLOR_TEXT_SECONDARY);
                tab.setBackground(createRoundRectDrawable(Color.parseColor("#F0F0F0"), dp2px(16), 0));
            }
        }
    }

    private void refreshResourceList() {
        resourceListContainer.removeAllViews();
        for (Resource res : resources) {
            if (currentCategory.equals("全部") || res.category.equals(currentCategory)) {
                resourceListContainer.addView(createResourceCard(res));
                View gap = new View(getActivity());
                gap.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp2px(8)));
                resourceListContainer.addView(gap);
            }
        }
    }

    private LinearLayout createResourceCard(Resource res) {
        LinearLayout card = new LinearLayout(getActivity());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));
        card.setBackground(createRoundRectDrawable(COLOR_CARD, dp2px(12), Color.parseColor("#EEEEEE")));
        card.setClickable(true);
        card.setOnClickListener(v -> showDetailDialog(res));

        // 标题行
        LinearLayout titleRow = new LinearLayout(getActivity());
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(titleRow);

        TextView icon = new TextView(getActivity());
        icon.setText(res.title.substring(0, 1));
        icon.setTextSize(18);
        icon.setTextColor(Color.WHITE);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(createCircleDrawable(COLOR_BLUE));
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp2px(44), dp2px(44)));
        titleRow.addView(icon);

        LinearLayout infoCol = new LinearLayout(getActivity());
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setPadding(dp2px(12), 0, 0, 0);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        infoCol.setLayoutParams(infoParams);
        titleRow.addView(infoCol);

        TextView titleView = new TextView(getActivity());
        titleView.setText(res.title);
        titleView.setTextSize(16);
        titleView.setTextColor(COLOR_TEXT_PRIMARY);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        infoCol.addView(titleView);

        TextView descView = new TextView(getActivity());
        descView.setText(res.description);
        descView.setTextSize(13);
        descView.setTextColor(COLOR_TEXT_SECONDARY);
        descView.setPadding(0, dp2px(2), 0, 0);
        descView.setMaxLines(2);
        descView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        infoCol.addView(descView);

        // 元信息行
        LinearLayout metaRow = new LinearLayout(getActivity());
        metaRow.setOrientation(LinearLayout.HORIZONTAL);
        metaRow.setGravity(Gravity.CENTER_VERTICAL);
        metaRow.setPadding(0, dp2px(10), 0, 0);
        card.addView(metaRow);

        TextView versionView = new TextView(getActivity());
        versionView.setText(res.version);
        versionView.setTextSize(11);
        versionView.setTextColor(COLOR_HINT);
        versionView.setBackground(createRoundRectDrawable(Color.parseColor("#F0F0F0"), dp2px(4), 0));
        versionView.setPadding(dp2px(6), dp2px(2), dp2px(6), dp2px(2));
        metaRow.addView(versionView);

        View gap1 = new View(getActivity());
        gap1.setLayoutParams(new LinearLayout.LayoutParams(dp2px(8), 0));
        metaRow.addView(gap1);

        TextView sizeView = new TextView(getActivity());
        sizeView.setText(res.size);
        sizeView.setTextSize(11);
        sizeView.setTextColor(COLOR_HINT);
        metaRow.addView(sizeView);

        View gap2 = new View(getActivity());
        gap2.setLayoutParams(new LinearLayout.LayoutParams(dp2px(8), 0));
        metaRow.addView(gap2);

        TextView countView = new TextView(getActivity());
        countView.setText(res.downloadCount + "次下载");
        countView.setTextSize(11);
        countView.setTextColor(COLOR_HINT);
        metaRow.addView(countView);

        // 操作按钮行（下载/应用/卸载）
        LinearLayout actionRow = new LinearLayout(getActivity());
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        actionRow.setGravity(Gravity.CENTER_VERTICAL);
        actionRow.setPadding(0, dp2px(10), 0, 0);
        card.addView(actionRow);

        if (res.progress == 0) {
            // 未下载：显示下载按钮
            TextView downloadBtn = new TextView(getActivity());
            downloadBtn.setText("下载");
            downloadBtn.setTextSize(13);
            downloadBtn.setTextColor(COLOR_BLUE);
            downloadBtn.setBackground(createRoundRectDrawable(Color.parseColor("#E3F2FD"), dp2px(8), 0));
            downloadBtn.setPadding(dp2px(16), dp2px(6), dp2px(16), dp2px(6));
            downloadBtn.setOnClickListener(v -> startDownload(res));
            actionRow.addView(downloadBtn);
        } else if (res.progress > 0 && res.progress < 100) {
            // 下载中：显示进度条
            ProgressBar progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setProgress(res.progress);
            progressBar.setMax(100);
            LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                0, dp2px(6), 1);
            progressBar.setLayoutParams(pbParams);
            actionRow.addView(progressBar);

            TextView progressText = new TextView(getActivity());
            progressText.setText(res.progress + "%");
            progressText.setTextSize(12);
            progressText.setTextColor(COLOR_BLUE);
            progressText.setPadding(dp2px(8), 0, 0, 0);
            actionRow.addView(progressText);
        } else if (res.progress == 100) {
            // 下载完成：显示应用/卸载按钮
            boolean isApplied = applyPrefs.getBoolean("applied_" + res.id, false);
            TextView actionBtn = new TextView(getActivity());
            if (isApplied) {
                actionBtn.setText("卸载");
                actionBtn.setTextColor(COLOR_RED);
                actionBtn.setBackground(createRoundRectDrawable(Color.parseColor("#FFEBEE"), dp2px(8), 0));
            } else {
                actionBtn.setText("应用");
                actionBtn.setTextColor(COLOR_BLUE);
                actionBtn.setBackground(createRoundRectDrawable(Color.parseColor("#E3F2FD"), dp2px(8), 0));
            }
            actionBtn.setTextSize(13);
            actionBtn.setPadding(dp2px(16), dp2px(6), dp2px(16), dp2px(6));
            actionBtn.setOnClickListener(v -> {
                boolean current = applyPrefs.getBoolean("applied_" + res.id, false);
                boolean next = !current;
                applyPrefs.edit().putBoolean("applied_" + res.id, next).apply();
                refreshResourceList();
            });
            actionRow.addView(actionBtn);
        }

        return card;
    }

    private void startDownload(Resource res) {
        if (res.progress >= 100) return;
        res.progress = 1;
        saveDownloadProgress(res.id, 1);
        refreshResourceList();

        new Thread(() -> {
            while (res.progress < 100) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
                res.progress += 5;
                if (res.progress > 100) res.progress = 100;
                final int p = res.progress;
                saveDownloadProgress(res.id, p);
                if (p % 20 == 0 || p == 100) {
                    handler.post(() -> refreshResourceList());
                }
            }
            handler.post(() -> refreshResourceList());
        }).start();
    }

    private void showDetailDialog(Resource res) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

        FrameLayout root = new FrameLayout(getActivity());
        root.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        root.setBackgroundColor(Color.parseColor("#CC000000"));
        root.setOnClickListener(v -> dialog.dismiss());

        LinearLayout card = new LinearLayout(getActivity());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp2px(24), dp2px(24), dp2px(24), dp2px(24));
        card.setBackground(createRoundRectDrawable(COLOR_CARD, dp2px(16), 0));
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
            dp2px(300), FrameLayout.LayoutParams.WRAP_CONTENT);
        cardParams.gravity = Gravity.CENTER;
        card.setLayoutParams(cardParams);
        card.setClickable(true);

        TextView titleView = new TextView(getActivity());
        titleView.setText(res.title);
        titleView.setTextSize(18);
        titleView.setTextColor(COLOR_TEXT_PRIMARY);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setGravity(Gravity.CENTER);
        card.addView(titleView);

        TextView descView = new TextView(getActivity());
        descView.setText(res.description);
        descView.setTextSize(14);
        descView.setTextColor(COLOR_TEXT_SECONDARY);
        descView.setPadding(0, dp2px(12), 0, 0);
        descView.setGravity(Gravity.CENTER);
        card.addView(descView);

        TextView infoView = new TextView(getActivity());
        infoView.setText("版本: " + res.version + "  |  大小: " + res.size + "  |  分类: " + res.category);
        infoView.setTextSize(12);
        infoView.setTextColor(COLOR_HINT);
        infoView.setPadding(0, dp2px(12), 0, 0);
        infoView.setGravity(Gravity.CENTER);
        card.addView(infoView);

        TextView statusView = new TextView(getActivity());
        if (res.progress == 100) {
            boolean isApplied = applyPrefs.getBoolean("applied_" + res.id, false);
            statusView.setText(isApplied ? "已应用" : "已下载");
            statusView.setTextColor(COLOR_GREEN);
        } else if (res.progress > 0) {
            statusView.setText("下载进度: " + res.progress + "%");
            statusView.setTextColor(COLOR_BLUE);
        } else {
            statusView.setText("未下载");
            statusView.setTextColor(COLOR_HINT);
        }
        statusView.setTextSize(14);
        statusView.setPadding(0, dp2px(16), 0, 0);
        statusView.setGravity(Gravity.CENTER);
        card.addView(statusView);

        TextView closeBtn = new TextView(getActivity());
        closeBtn.setText("关闭");
        closeBtn.setTextSize(14);
        closeBtn.setTextColor(Color.WHITE);
        closeBtn.setBackground(createRoundRectDrawable(COLOR_BLUE, dp2px(8), 0));
        closeBtn.setPadding(dp2px(24), dp2px(10), dp2px(24), dp2px(10));
        closeBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        closeParams.topMargin = dp2px(16);
        closeBtn.setLayoutParams(closeParams);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        card.addView(closeBtn);

        root.addView(card);
        dialog.setContentView(root);
        dialog.show();
    }

    private void initResources() {
        resources.add(new Resource("r1", "VorTex工具箱", "游戏辅助工具，支持多种实用功能", "工具", "v2.1.0", "12.5MB", 1250, 0, ""));
        resources.add(new Resource("r2", "高清材质包", "4K分辨率材质包，提升画面质量", "材质", "v1.5", "256MB", 890, 0, ""));
        resources.add(new Resource("r3", "优化插件", "提升帧率，降低卡顿，适配低配置", "工具", "v1.2", "3.2MB", 3400, 0, ""));
    }

    private void loadDownloadProgress() {
        for (Resource res : resources) {
            res.progress = downloadPrefs.getInt("progress_" + res.id, 0);
        }
    }

    private void saveDownloadProgress(String id, int progress) {
        downloadPrefs.edit().putInt("progress_" + id, progress).apply();
    }

    private View createDivider() {
        View divider = new View(getActivity());
        divider.setBackgroundColor(COLOR_DIVIDER);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        return divider;
    }

    private GradientDrawable createRoundRectDrawable(int fillColor, int radius, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(fillColor);
        if (strokeColor != 0) {
            drawable.setStroke(1, strokeColor);
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
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
