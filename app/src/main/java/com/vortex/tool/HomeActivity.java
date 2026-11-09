package com.vortex.tool;

import android.app.Activity;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vortex.tool.fragment.HomeFragment;
import com.vortex.tool.fragment.SettingsFragment;

public class HomeActivity extends Activity {

    public static final int CONTENT_CONTAINER_ID = 10001;

    private static final int COLOR_NAV_BG = Color.parseColor("#FFFFFF");
    private static final int COLOR_NAV_SELECTED = Color.parseColor("#2196F3");
    private static final int COLOR_WHITE = Color.parseColor("#FFFFFF");
    private static final int COLOR_GRAY = Color.parseColor("#666666");

    private FrameLayout contentContainer;
    private View slideBg;
    private ImageView[] tabIcons = new ImageView[2];
    private TextView[] tabLabels = new TextView[2];
    private int currentTab = 0;
    private int tabWidth;

    private final String[] tabTitles = {"首页", "设置"};
    private final int[] tabIconResIds = {R.drawable.ic_home, R.drawable.ic_settings};
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        setupImmersiveBars();

        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        root.setBackgroundColor(Color.parseColor("#FAFAFA"));

        contentContainer = new FrameLayout(this);
        contentContainer.setId(CONTENT_CONTAINER_ID);
        int navBarHeight = getNavigationBarHeight();
        contentContainer.setPadding(0, getStatusBarHeight(), 0, dp2px(92) + navBarHeight);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT);
        contentContainer.setLayoutParams(contentParams);
        root.addView(contentContainer);

        FrameLayout bottomNav = createBottomNav();
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            dp2px(68));
        navParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        navParams.bottomMargin = navBarHeight;
        bottomNav.setLayoutParams(navParams);
        root.addView(bottomNav);

        setContentView(root);

        fragments = new Fragment[] {
            new HomeFragment(),
            new SettingsFragment()
        };

        getFragmentManager()
            .beginTransaction()
            .add(contentContainer.getId(), fragments[0])
            .commitAllowingStateLoss();
    }

    private FrameLayout createBottomNav() {
        FrameLayout nav = new FrameLayout(this);
        nav.setBackground(createRoundRectDrawable(COLOR_NAV_BG, dp2px(26), Color.parseColor("#EEEEEE")));
        nav.setPadding(dp2px(6), dp2px(4), dp2px(6), dp2px(4));

        tabWidth = dp2px(96);

        slideBg = new View(this);
        slideBg.setBackground(createRoundRectDrawable(COLOR_NAV_SELECTED, dp2px(24), 0));
        FrameLayout.LayoutParams slideParams = new FrameLayout.LayoutParams(
            tabWidth, dp2px(56));
        slideParams.gravity = Gravity.CENTER_VERTICAL;
        slideParams.leftMargin = 0;
        slideBg.setLayoutParams(slideParams);
        nav.addView(slideBg);

        LinearLayout tabRow = new LinearLayout(this);
        tabRow.setOrientation(LinearLayout.HORIZONTAL);
        tabRow.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        nav.addView(tabRow);

        for (int i = 0; i < 2; i++) {
            final int idx = i;
            FrameLayout tab = createTab(i);
            tab.setOnTouchListener(new TabTouchListener(idx));
            tabRow.addView(tab);
        }

        return nav;
    }

    private FrameLayout createTab(int index) {
        FrameLayout tab = new FrameLayout(this);
        tab.setLayoutParams(new LinearLayout.LayoutParams(
            tabWidth, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        tab.addView(content);

        ImageView icon = new ImageView(this);
        icon.setImageResource(tabIconResIds[index]);
        icon.setColorFilter(index == 0 ? COLOR_WHITE : COLOR_GRAY);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            dp2px(22), dp2px(22));
        iconParams.gravity = Gravity.CENTER;
        icon.setLayoutParams(iconParams);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tabIcons[index] = icon;
        content.addView(icon);

        TextView label = new TextView(this);
        label.setText(tabTitles[index]);
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(index == 0 ? COLOR_WHITE : COLOR_GRAY);
        label.setPadding(0, dp2px(2), 0, 0);
        tabLabels[index] = label;
        content.addView(label);

        return tab;
    }

    private void switchTab(int index) {
        if (currentTab == index) return;

        getFragmentManager().executePendingTransactions();

        int fromX = currentTab * tabWidth;
        int toX = index * tabWidth;

        ValueAnimator animator = ValueAnimator.ofInt(fromX, toX);
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                slideBg.setTranslationX(value);
            }
        });
        animator.start();

        tabIcons[currentTab].setColorFilter(COLOR_GRAY);
        tabLabels[currentTab].setTextColor(COLOR_GRAY);
        tabIcons[index].setColorFilter(COLOR_WHITE);
        tabLabels[index].setTextColor(COLOR_WHITE);

        if (index > currentTab) {
            getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
                .replace(contentContainer.getId(), fragments[index])
                .commitAllowingStateLoss();
        } else {
            getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(contentContainer.getId(), fragments[index])
                .commitAllowingStateLoss();
        }

        currentTab = index;
    }

    private class TabTouchListener implements View.OnTouchListener {
        private final int tabIndex;
        private float startX = 0;
        private float startY = 0;
        private boolean isSliding = false;
        private static final int SWIPE_THRESHOLD = 40;

        TabTouchListener(int index) {
            this.tabIndex = index;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    isSliding = false;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - startX;
                    float dy = event.getRawY() - startY;
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > SWIPE_THRESHOLD) {
                        isSliding = true;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    float endX = event.getRawX();
                    float totalDx = endX - startX;
                    if (isSliding && Math.abs(totalDx) > SWIPE_THRESHOLD) {
                        if (totalDx > 0 && currentTab > 0) {
                            switchTab(currentTab - 1);
                        } else if (totalDx < 0 && currentTab < 1) {
                            switchTab(currentTab + 1);
                        }
                    } else {
                        switchTab(tabIndex);
                    }
                    return true;
            }
            return false;
        }
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

    private void setupImmersiveBars() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return dp2px(24);
    }

    private int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

}
