package com.vortex.tool;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import java.util.function.BiConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import android.view.ViewGroup;

public class FloatBallService extends Service {

    private enum State { BALL, ANIMATING, PANEL }
    private State state = State.BALL;

    private record MenuItem(String label, int iconRes) {}
    private enum MenuCategory { GENERAL, BATTLE, PROPERTY, AIM, FUNCTION, VISION, EDIT, LIST, FAST, TELEPORT, OTHER, EXIT }

    private static final int BALL_SIZE_DP = 39;
    private static final int PANEL_WIDTH_DP = 357;
    private static final int PANEL_HEIGHT_DP = 263;
    private static final int MENU_WIDTH_DP = 64;
    private static final int CORNER_RADIUS_DP = 20;
    private static final int MENU_ITEM_HEIGHT_DP = 38;
    private static final long ANIM_DURATION_MS = 300;

    private static final int COLOR_BALL = 0xFF2196F3;
    private static final int COLOR_BALL_BORDER = 0xFF2196F3;
    private static final int COLOR_BALL_CENTER = 0xFF212121;
    private static final int COLOR_BALL_EDGE = 0xFF212121;
    private static final int COLOR_DARK_BG = 0xCC19191E;
    private static final int COLOR_MENU_SELECTED = 0x332196F3;
    private static final int COLOR_MENU_NORMAL = 0x00FFFFFF;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY  = 0xAAFFFFFF;
    private static final int COLOR_ACCENT    = 0xFF2196F3;
    private static final int COLOR_CLOSE_BTN = 0xBBFFFFFF;

    private WindowManager windowManager;
    private FrameLayout rootView;
    private WindowManager.LayoutParams params;

    private float ballInitX, ballInitY;
    private float ballTouchX, ballTouchY;
    private boolean isMoving;

    private ValueAnimator morphAnimator;
    private boolean morphToPanel;
    private boolean animCancelled;
    private ValueAnimator pinchAnimator;

    private int screenWidth, screenHeight;
    private int savedBallX, savedBallY;

    private LinearLayout rightPanel;
    private FrameLayout contentContainer;
    private LinearLayout screenNotificationContainer;
    private WindowManager.LayoutParams screenNotificationParams;
    private java.util.HashMap<String, LinearLayout> activeNotifications;
    private java.util.HashMap<String, Runnable> dismissRunnables;
    private LinearLayout[] menuItems = new LinearLayout[12];
    private MenuCategory currentCategory = MenuCategory.GENERAL;

    private boolean isTilting = false;
    private static final float MAX_TILT_DEG = 5f;
    private static final int TILT_CANCEL_THRESHOLD_DP = 8;
    private TextView leftTitle;


    private final Map<String, Runnable> callbacks = new HashMap<>();
    private final Map<String, IntFunction<String>> seekFormatters = new HashMap<>();
    private List<DslPage> dslPages;
    private boolean dslLoaded = false;

    // 状态持久化
    private final HashMap<String, Boolean> toggleStates = new HashMap<>();
    private final HashMap<String, Integer> seekBarValues = new HashMap<>();
    private final HashMap<Integer, Integer> lastTabIndex = new HashMap<>();

    // ==================== 快捷键系统 ====================
    private boolean shortcutsVisible = false;
    private final ArrayList<View> shortcutButtons = new ArrayList<>();
    private final HashMap<String, float[]> labelViewPositions = new HashMap<>(); // label → [x,y]
    private final HashMap<String, WindowManager.LayoutParams> shortcutParams = new HashMap<>();
    private static final int SHORTCUT_H_DP = 32;

    private final MenuItem[] menuData = {
        new MenuItem("战斗", R.drawable.ic_battle),
        new MenuItem("通用", R.drawable.ic_home),
        new MenuItem("属性", R.drawable.ic_property),
        new MenuItem("瞄准", R.drawable.ic_aim),
        new MenuItem("功能", R.drawable.ic_settings),
        new MenuItem("视觉", R.drawable.ic_float_window),
        new MenuItem("修改", R.drawable.ic_apply),
        new MenuItem("列表", R.drawable.ic_resource),
        new MenuItem("快捷", R.drawable.ic_fast),
        new MenuItem("传送", R.drawable.ic_teleport),
        new MenuItem("其他", R.drawable.ic_more),
        new MenuItem("退出", R.drawable.ic_download),
    };

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        var metrics = new android.util.DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        showBall();
        initScreenNotification();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        if (morphAnimator != null && morphAnimator.isRunning()) morphAnimator.cancel();
        if (rootView != null && windowManager != null) {
            try { windowManager.removeView(rootView); } catch (IllegalArgumentException ignored) {}
        }
        if (screenNotificationContainer != null && windowManager != null) {
            try { windowManager.removeView(screenNotificationContainer); } catch (IllegalArgumentException ignored) {}
        }
        super.onDestroy();
    }

    // ==================== 小球 ====================

    private void showBall() {
        state = State.BALL;
        var size = dp2px(BALL_SIZE_DP);
        rootView = new FrameLayout(this);

        var bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{COLOR_BALL_CENTER, COLOR_BALL_EDGE});
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp2px(CORNER_RADIUS_DP));
        bg.setStroke(dp2px(1), COLOR_BALL_BORDER);
        rootView.setBackground(bg);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
        var flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                  | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        params = new WindowManager.LayoutParams(size, size, type, flags, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = dp2px(16);
        params.y = dp2px(200);

        rootView.setOnTouchListener(this::onBallTouch);
        windowManager.addView(rootView, params);
    }

    private boolean onBallTouch(View v, MotionEvent event) {
        if (state != State.BALL) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                ballInitX = params.x; ballInitY = params.y;
                ballTouchX = event.getRawX(); ballTouchY = event.getRawY();
                isMoving = false;
                return true;
            }
            case MotionEvent.ACTION_MOVE -> {
                var dx = event.getRawX() - ballTouchX;
                var dy = event.getRawY() - ballTouchY;
                if (Math.abs(dx) > 8 || Math.abs(dy) > 8) isMoving = true;
                params.x = (int) (ballInitX + dx);
                params.y = (int) (ballInitY + dy);
                clampBallPosition();
                try { windowManager.updateViewLayout(rootView, params); } catch (IllegalArgumentException ignored) {}
                return true;
            }
            case MotionEvent.ACTION_UP -> {
                savedBallX = params.x;
                savedBallY = params.y;
                if (!isMoving) animateMorph(true);
                return true;
            }
            default -> { return false; }
        }
    }

    private void clampBallPosition() {
        var size = dp2px(BALL_SIZE_DP);
        params.x = Math.max(0, Math.min(params.x, screenWidth - size));
        params.y = Math.max(0, Math.min(params.y, screenHeight - size - getNavBarHeight()));
    }

    private int getNavBarHeight() {
        var res = getResources();
        int id = res.getIdentifier("navigation_bar_height", "dimen", "android");
        return id > 0 ? res.getDimensionPixelSize(id) : 0;
    }

    // ==================== 统一形变动画（可打断切换方向） ====================

    void animateMorph(boolean toPanel) {
        if (morphAnimator != null && morphAnimator.isRunning()) {
            morphAnimator.cancel();
        }
        if (pinchAnimator != null && pinchAnimator.isRunning()) {
            pinchAnimator.cancel();
        }
        rootView.setScaleX(1f);
        rootView.setScaleY(1f);
        animCancelled = false;

        state = State.ANIMATING;
        morphToPanel = toPanel;
        rootView.removeAllViews();

        var startW = params.width;
        var startH = params.height;
        var endW = toPanel ? dp2px(PANEL_WIDTH_DP) : dp2px(BALL_SIZE_DP);
        var endH = toPanel ? dp2px(PANEL_HEIGHT_DP) : dp2px(BALL_SIZE_DP);

        var bg = (GradientDrawable) rootView.getBackground();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp2px(CORNER_RADIUS_DP));
        var startAlpha  = toPanel ? 255 : 204;
        var endAlpha    = toPanel ? 204 : 255;

        // 获取屏幕尺寸（用于墙面碰撞检测）
        var display = windowManager.getDefaultDisplay();
        var screenSize = new Point();
        display.getRealSize(screenSize);
        var screenW = screenSize.x;
        var screenH = screenSize.y;

        // 展开时以球的位置为起点，墙面阻挡时会贴边
        var startX = params.x;
        var startY = params.y;
        // 受墙面限制的实际终点
        var clampedEndX = Math.max(0, Math.min(startX, screenW - dp2px(PANEL_WIDTH_DP)));
        var clampedEndY = Math.max(0, Math.min(startY, screenH - dp2px(PANEL_HEIGHT_DP)));

        // 动画期间点击可打断切换
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                animateMorph(!morphToPanel);
                return true;
            }
            return false;
        });

        morphAnimator = ValueAnimator.ofFloat(0f, 1f);
        morphAnimator.setDuration(ANIM_DURATION_MS);
        morphAnimator.setInterpolator(new LinearInterpolator());

        morphAnimator.addUpdateListener(anim -> {
            var f = anim.getAnimatedFraction();

            params.width  = (int) (startW + (endW - startW) * f);
            params.height = (int) (startH + (endH - startH) * f);

            if (toPanel) {
                // 展开：向右下漂移，但每帧检测墙面碰撞并阻挡
                var naturalX = (int) (startX + (clampedEndX - startX) * f);
                var naturalY = (int) (startY + (clampedEndY - startY) * f);
                // 墙面阻挡：面板不能超出屏幕
                params.x = Math.max(0, Math.min(naturalX, screenW - params.width));
                params.y = Math.max(0, Math.min(naturalY, screenH - params.height));
            } else {
                // 缩小：仅保存水平坐标，Y坐标保持面板位置
                params.x = (int) (startX + (savedBallX - startX) * f);
                params.y = startY;
            }

            var alpha = (int) (startAlpha + (endAlpha - startAlpha) * f);
            var rs = toPanel ? 30  : 25;
            var re = toPanel ? 25  : 30;
            var gs = toPanel ? 30  : 25;
            var ge = toPanel ? 25  : 30;
            var bs = toPanel ? 32  : 30;
            var be = toPanel ? 30  : 32;
            bg.setColor(Color.argb(alpha,
                (int) (rs + (re - rs) * f),
                (int) (gs + (ge - gs) * f),
                (int) (bs + (be - bs) * f)));

            try { windowManager.updateViewLayout(rootView, params); } catch (IllegalArgumentException ignored) {}
        });

        morphAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animCancelled) return; // 被打断取消，不执行终态
                if (morphToPanel) {
                    var finalBg = new GradientDrawable();
                    finalBg.setShape(GradientDrawable.RECTANGLE);
                    finalBg.setCornerRadius(dp2px(CORNER_RADIUS_DP));
                    finalBg.setColor(COLOR_DARK_BG);
                    finalBg.setStroke(dp2px(1), COLOR_BALL_BORDER);
                    rootView.setBackground(finalBg);

                    params.x = clampedEndX; params.y = clampedEndY;
                    params.width = endW; params.height = endH;
                    try { windowManager.updateViewLayout(rootView, params); } catch (IllegalArgumentException ignored) {}

                    state = State.PANEL;
                    buildPanelContent();
                } else {
                    var ballBg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{COLOR_BALL_CENTER, COLOR_BALL_EDGE});
                    ballBg.setShape(GradientDrawable.RECTANGLE);
                    ballBg.setCornerRadius(dp2px(CORNER_RADIUS_DP));
                    ballBg.setStroke(dp2px(1), COLOR_BALL_BORDER);
                    rootView.setBackground(ballBg);

                    params.x = savedBallX; params.y = startY;
                    params.width = endW; params.height = endH;
                    try { windowManager.updateViewLayout(rootView, params); } catch (IllegalArgumentException ignored) {}

                    state = State.BALL;
                    rootView.setOnTouchListener(FloatBallService.this::onBallTouch);
                }
            }
            @Override public void onAnimationCancel(Animator animation) {
                animCancelled = true;
            }
        });

        morphAnimator.start();
    }

// ==================== 面板内容 ====================

    private void buildPanelContent() {
        rootView.setScaleX(1f);
        rootView.setScaleY(1f);
        var mainRow = new LinearLayout(this);
        mainRow.setOrientation(LinearLayout.HORIZONTAL);
        mainRow.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // 左侧列：标题 + 菜单
        var leftColumn = new LinearLayout(this);
        leftColumn.setOrientation(LinearLayout.VERTICAL);
        leftColumn.setLayoutParams(new LinearLayout.LayoutParams(
            dp2px(MENU_WIDTH_DP), LinearLayout.LayoutParams.MATCH_PARENT));

        leftTitle = new TextView(this);
        leftTitle.setText("首页");
        leftTitle.setTextSize(12);
        leftTitle.setTextColor(COLOR_TEXT_WHITE);
        leftTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        leftTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        leftTitle.setPadding(0, dp2px(12), 0, dp2px(4));
        leftColumn.addView(leftTitle);

        var leftMenu = buildLeftMenu();
        var menuScroll = new ScrollView(this);
        menuScroll.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        menuScroll.setVerticalScrollBarEnabled(false);
        menuScroll.setVerticalFadingEdgeEnabled(true);
        menuScroll.setFadingEdgeLength(dp2px(3));
        menuScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        menuScroll.addView(leftMenu);
        leftColumn.addView(menuScroll);

        mainRow.addView(leftColumn);

        rightPanel = new LinearLayout(this);
        rightPanel.setOrientation(LinearLayout.VERTICAL);
        rightPanel.setPadding(dp2px(2), 0, dp2px(4), dp2px(4));
        rightPanel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

        // 右上角关闭按钮（与左侧标题对齐）
        var closeRow = new LinearLayout(this);
        closeRow.setOrientation(LinearLayout.HORIZONTAL);
        closeRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams closeRowParams = new LinearLayout.LayoutParams(MATCH, dp2px(24));
        closeRowParams.topMargin = dp2px(8);
        closeRow.setLayoutParams(closeRowParams);
        var spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, dp2px(24), 1f));
        closeRow.addView(spacer);
        var closeBtn = new TextView(this);
        closeBtn.setText("✕");
        closeBtn.setTextSize(14);
        closeBtn.setTextColor(COLOR_CLOSE_BTN);
        closeBtn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        closeBtn.setLayoutParams(new LinearLayout.LayoutParams(dp2px(24), dp2px(24)));
        closeBtn.setOnClickListener(v -> animateMorph(false));
        closeRow.addView(closeBtn);
        rightPanel.addView(closeRow);

        contentContainer = new FrameLayout(this);
        contentContainer.setLayoutParams(new LinearLayout.LayoutParams(MATCH, 0, 1f));
        rightPanel.addView(contentContainer);

        mainRow.addView(rightPanel);

        rootView.addView(mainRow);


        // 淡入过渡动画
        mainRow.setAlpha(0f);
        mainRow.animate().alpha(1f).setDuration(220).setInterpolator(
            new android.view.animation.DecelerateInterpolator()).start();
        rootView.setOnTouchListener(this::onPanelDrag);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setClipToOutline(true);
        }
        selectMenuItem(currentCategory.ordinal(), false);
    }

    private float panelDragInitX, panelDragInitY;
    private float panelDragTouchX, panelDragTouchY;

    private boolean isTouchOnInteractiveView(ViewGroup parent, float x, float y) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) continue;
            float localX = x - child.getLeft();
            float localY = y - child.getTop();
            if (localX >= 0 && localX <= child.getWidth() && localY >= 0 && localY <= child.getHeight()) {
                if (child instanceof Button || child instanceof SeekBar || child instanceof android.widget.Switch || child instanceof ImageView) {
                    return true;
                }
                if (child instanceof ViewGroup && ((ViewGroup) child).getChildCount() > 0) {
                    if (isTouchOnInteractiveView((ViewGroup) child, localX, localY)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void startPinchAnimation(boolean pinch) {
        if (pinchAnimator != null && pinchAnimator.isRunning()) {
            pinchAnimator.cancel();
        }
        float from = rootView.getScaleX();
        float to = pinch ? 0.91f : 1.0f;
        pinchAnimator = ValueAnimator.ofFloat(from, to);
        pinchAnimator.setDuration(150);
        pinchAnimator.setInterpolator(new DecelerateInterpolator());
        pinchAnimator.addUpdateListener(anim -> {
            float val = (float) anim.getAnimatedValue();
            rootView.setScaleX(val);
            rootView.setScaleY(val);
        });
        pinchAnimator.start();
    }

    private void startTiltAnimation(float touchX, float touchY) {
        if (isTilting) return;
        isTilting = true;
        float centerX = params.width / 2f;
        float centerY = params.height / 2f;
        float halfW = centerX;
        float halfH = centerY;
        float relX = touchX - centerX;
        float relY = touchY - centerY;
        float targetRotationX = -(relY / Math.max(halfH, 1f)) * MAX_TILT_DEG;
        float targetRotationY = (relX / Math.max(halfW, 1f)) * MAX_TILT_DEG;
        targetRotationX = Math.max(-MAX_TILT_DEG, Math.min(MAX_TILT_DEG, targetRotationX));
        targetRotationY = Math.max(-MAX_TILT_DEG, Math.min(MAX_TILT_DEG, targetRotationY));
        rootView.setPivotX(centerX);
        rootView.setPivotY(centerY);
        rootView.setCameraDistance(dp2px(1200));
        rootView.animate()
            .rotationX(targetRotationX)
            .rotationY(targetRotationY)
            .scaleX(0.91f)
            .scaleY(0.91f)
            .setDuration(350)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    private void cancelTiltAnimation() {
        if (!isTilting) return;
        isTilting = false;
        rootView.animate()
            .rotationX(0f)
            .rotationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(350)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    private boolean onPanelDrag(View v, MotionEvent event) {
        if (state != State.PANEL) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                if (isTouchOnInteractiveView(rootView, event.getX(), event.getY())) {
                    return false;
                }
                panelDragInitX = params.x; panelDragInitY = params.y;
                panelDragTouchX = event.getRawX(); panelDragTouchY = event.getRawY();
                startTiltAnimation(event.getX(), event.getY());
                return true;
            }
            case MotionEvent.ACTION_MOVE -> {
                float dx = event.getRawX() - panelDragTouchX;
                float dy = event.getRawY() - panelDragTouchY;
                if (Math.abs(dx) > dp2px(TILT_CANCEL_THRESHOLD_DP) || Math.abs(dy) > dp2px(TILT_CANCEL_THRESHOLD_DP)) {
                    cancelTiltAnimation();
                }
                params.x = (int) (panelDragInitX + dx);
                params.y = (int) (panelDragInitY + dy);
                clampPanelPosition();
                try { windowManager.updateViewLayout(rootView, params); } catch (IllegalArgumentException ignored) {}
                return true;
            }
            case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                cancelTiltAnimation();
                return true;
            }
            default -> { return false; }
        }
    }

    private void clampPanelPosition() {
        var w = params.width;
        var h = params.height;
        params.x = Math.max(0, Math.min(params.x, screenWidth - w));
        params.y = Math.max(0, Math.min(params.y, screenHeight - h - getNavBarHeight()));
    }

    private LinearLayout buildLeftMenu() {
        var menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setGravity(Gravity.CENTER_HORIZONTAL);
        menu.setPadding(dp2px(2), dp2px(0), dp2px(2), dp2px(4));
        menu.setLayoutParams(new LinearLayout.LayoutParams(dp2px(MENU_WIDTH_DP), LinearLayout.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < menuData.length; i++) {
            var idx = i;
            var item = menuData[i];
            var itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER);
            itemLayout.setPadding(dp2px(2), dp2px(1), dp2px(2), dp2px(1));
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(MENU_ITEM_HEIGHT_DP)));

            var itemBg = new GradientDrawable();
            itemBg.setShape(GradientDrawable.RECTANGLE);
            itemBg.setCornerRadius(dp2px(8));
            itemBg.setColor(COLOR_MENU_NORMAL);
            itemLayout.setBackground(itemBg);

            var icon = new ImageView(this);
            icon.setImageResource(item.iconRes());
            icon.setColorFilter(COLOR_TEXT_GRAY);
            icon.setLayoutParams(new LinearLayout.LayoutParams(dp2px(18), dp2px(18)));
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            itemLayout.addView(icon);

            var label = new TextView(this);
            label.setText(item.label());
            label.setTextSize(9);
            label.setTextColor(COLOR_TEXT_GRAY);
            label.setGravity(Gravity.CENTER);
            label.setPadding(0, dp2px(2), 0, 0);
            itemLayout.addView(label);

            final int itemIndex = idx;
            itemLayout.setOnClickListener(v -> {
                if (menuData[itemIndex].label().equals("退出")) {
                    animateMorph(false);
                    stopSelf();
                    return;
                }
                selectMenuItem(itemIndex);
            });
            menuItems[i] = itemLayout;
            menu.addView(itemLayout);
        }
        return menu;
    }

    private void selectMenuItem(int index) {
        selectMenuItem(index, true);
    }

    private void selectMenuItem(int index, boolean animate) {
        int prevIndex = currentCategory.ordinal();
        if (prevIndex == index) {
            // 重复点击同一菜单：不触发动画，但如果内容为空仍需加载（首次初始化）
            if (contentContainer != null && contentContainer.getChildCount() == 0) {
                addPanelContent();
            }
            setMenuItemState(index, COLOR_ACCENT);
            return;
        }
        currentCategory = MenuCategory.values()[index];

        if (animate) {
            animateMenuItem(prevIndex, COLOR_ACCENT, COLOR_TEXT_GRAY);
            animateMenuItem(index, COLOR_TEXT_GRAY, COLOR_ACCENT);
        } else {
            setMenuItemState(prevIndex, COLOR_TEXT_GRAY);
            setMenuItemState(index, COLOR_ACCENT);
        }

        if (leftTitle != null) {
            leftTitle.setText(menuData[index].label());
        }

        if (animate && contentContainer != null && contentContainer.getChildCount() == 1) {
            // 交叉淡入淡出，仿照左侧菜单颜色渐变动画
            final View oldView = contentContainer.getChildAt(0);
            var newScroll = buildContentScroll();
            newScroll.setAlpha(0f);
            contentContainer.addView(newScroll);

            var crossFade = ValueAnimator.ofFloat(0f, 1f);
            crossFade.setDuration(250);
            crossFade.setInterpolator(new DecelerateInterpolator());
            crossFade.addUpdateListener(anim -> {
                float f = anim.getAnimatedFraction();
                oldView.setAlpha(1f - f);
                newScroll.setAlpha(f);
            });
            crossFade.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (contentContainer.indexOfChild(oldView) >= 0) {
                        contentContainer.removeView(oldView);
                    }
                }
            });
            crossFade.start();
        } else {
            if (contentContainer != null) contentContainer.removeAllViews();
            addPanelContent();
        }
    }

    private void animateMenuItem(int itemIndex, int fromColor, int toColor) {
        var itemLayout = menuItems[itemIndex];
        var icon = (ImageView) itemLayout.getChildAt(0);
        var label = (TextView) itemLayout.getChildAt(1);

        var colorAnim = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnim.setDuration(250);
        colorAnim.setInterpolator(new DecelerateInterpolator());
        colorAnim.addUpdateListener(anim -> {
            int color = (int) anim.getAnimatedValue();
            icon.setColorFilter(color);
            label.setTextColor(color);
        });
        colorAnim.start();
    }

    private void setMenuItemState(int itemIndex, int color) {
        var itemLayout = menuItems[itemIndex];
        var icon = (ImageView) itemLayout.getChildAt(0);
        var label = (TextView) itemLayout.getChildAt(1);
        icon.setColorFilter(color);
        label.setTextColor(color);
    }

    

    private View buildTabbedContent(String[] tabNames, View[] tabViews, int pageKey) {
        var root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));

        var tabArea = new FrameLayout(this);
        tabArea.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));

        var tabBar = new LinearLayout(this);
        tabBar.setOrientation(LinearLayout.HORIZONTAL);
        tabBar.setLayoutParams(new FrameLayout.LayoutParams(MATCH, WRAP));
        tabBar.setPadding(dp2px(8), dp2px(0), dp2px(8), dp2px(6));

        var contentArea = new FrameLayout(this);
        contentArea.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));

        final int savedTabIdx = lastTabIndex.containsKey(pageKey) ? lastTabIndex.get(pageKey) : 0;
        TextView[] tabTexts = new TextView[tabNames.length];
        LinearLayout[] tabWraps = new LinearLayout[tabNames.length];

        for (int i = 0; i < tabNames.length; i++) {
            var tabText = new TextView(this);
            tabText.setText(tabNames[i]);
            tabText.setTextSize(13);
            tabText.setTextColor(i == savedTabIdx ? COLOR_ACCENT : COLOR_TEXT_GRAY);
            tabText.setPadding(dp2px(4), dp2px(0), dp2px(4), dp2px(0));
            tabText.setLayoutParams(new LinearLayout.LayoutParams(WRAP, WRAP));
            tabTexts[i] = tabText;

            var tabWrap = new LinearLayout(this);
            tabWrap.setOrientation(LinearLayout.VERTICAL);
            tabWrap.setGravity(Gravity.CENTER_HORIZONTAL);
            tabWrap.setLayoutParams(new LinearLayout.LayoutParams(WRAP, WRAP));
            tabWrap.addView(tabText);
            tabWraps[i] = tabWrap;

            tabBar.addView(tabWrap);
            contentArea.addView(tabViews[i]);
            tabViews[i].setVisibility(i == savedTabIdx ? View.VISIBLE : View.GONE);
        }

        var indicator = new View(this);
        int indicatorH = dp2px(2);
        var indicatorLp = new FrameLayout.LayoutParams(0, indicatorH);
        indicatorLp.gravity = Gravity.START | Gravity.BOTTOM;
        indicatorLp.bottomMargin = dp2px(2);
        indicator.setLayoutParams(indicatorLp);
        var indicatorBg = new GradientDrawable();
        indicatorBg.setShape(GradientDrawable.RECTANGLE);
        indicatorBg.setCornerRadius(dp2px(1));
        indicatorBg.setColor(COLOR_ACCENT);
        indicator.setBackground(indicatorBg);
        tabArea.addView(indicator);
        tabArea.addView(tabBar);

        root.addView(tabArea);
        root.addView(contentArea);

        final int[] currentTab = {savedTabIdx};

        for (int i = 0; i < tabNames.length; i++) {
            final int idx = i;
            tabWraps[i].setOnClickListener(v -> {
                if (currentTab[0] == idx) return;
                int oldIdx = currentTab[0];
                currentTab[0] = idx;

                int targetX = tabWraps[idx].getLeft() + tabWraps[idx].getPaddingLeft() + dp2px(4);
                int targetW = (int) tabTexts[idx].getPaint().measureText(tabNames[idx]);
                int startX = (int) indicator.getTranslationX();
                int startW = indicator.getWidth();

                // 平移 + 宽度组合动画
                var anim = ValueAnimator.ofFloat(0f, 1f);
                anim.setDuration(250);
                anim.setInterpolator(new DecelerateInterpolator());
                anim.addUpdateListener(a -> {
                    float f = a.getAnimatedFraction();
                    int newX = (int) (startX + (targetX - startX) * f);
                    int newW = (int) (startW + (targetW - startW) * f);
                    indicator.setTranslationX(newX);
                    var lp = indicator.getLayoutParams();
                    lp.width = newW;
                    indicator.setLayoutParams(lp);
                });
                anim.start();

                // 旧标签颜色渐变（显->淡）
                var oldColorAnim = ValueAnimator.ofArgb(COLOR_ACCENT, COLOR_TEXT_GRAY);
                oldColorAnim.setDuration(250);
                oldColorAnim.addUpdateListener(a -> tabTexts[oldIdx].setTextColor((Integer) a.getAnimatedValue()));
                oldColorAnim.start();

                // 新标签颜色渐变（淡->显）
                var newColorAnim = ValueAnimator.ofArgb(COLOR_TEXT_GRAY, COLOR_ACCENT);
                newColorAnim.setDuration(250);
                newColorAnim.addUpdateListener(a -> tabTexts[idx].setTextColor((Integer) a.getAnimatedValue()));
                newColorAnim.start();

                for (int j = 0; j < tabNames.length; j++) {
                    tabViews[j].setVisibility(j == idx ? View.VISIBLE : View.GONE);
                }
                lastTabIndex.put(pageKey, idx);
            });
        }

        tabBar.post(() -> {
            int savedTab = currentTab[0];
            int startX = tabWraps[savedTab].getLeft() + tabWraps[savedTab].getPaddingLeft() + dp2px(4);
            int startW = (int) tabTexts[savedTab].getPaint().measureText(tabNames[savedTab]);
            indicator.setTranslationX(startX);
            var lp = indicator.getLayoutParams();
            lp.width = startW;
            indicator.setLayoutParams(lp);
        });

        return root;
    }private LinearLayout buildColumn() {
        var col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH, 1f));
        return col;
    }















    


    private void initScreenNotification() {
        screenNotificationContainer = new LinearLayout(this);
        screenNotificationContainer.setOrientation(LinearLayout.VERTICAL);
        screenNotificationContainer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
        var flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                  | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                  | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        screenNotificationParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type, flags, PixelFormat.TRANSLUCENT);
        screenNotificationParams.gravity = Gravity.TOP | Gravity.START;
        screenNotificationParams.x = dp2px(8);
        screenNotificationParams.y = dp2px(8);

        windowManager.addView(screenNotificationContainer, screenNotificationParams);
        activeNotifications = new java.util.HashMap<>();
        dismissRunnables = new java.util.HashMap<>();
    }


    private void showNotification(String label, boolean isOn) {
        if (screenNotificationContainer == null) return;

        int statusIcon = isOn ? R.drawable.ic_notif_check : R.drawable.ic_notif_cross;
        int statusColor = isOn ? 0xFF00E676 : 0xFFFF5252;

        // 已有该功能的通知，仅更新图标并刷新消散时间
        if (activeNotifications != null && activeNotifications.containsKey(label)) {
            var existing = activeNotifications.get(label);
            if (existing != null && existing.getParent() != null) {
                var nameTv = (TextView) existing.findViewWithTag("name");
                var statusIconView = (ImageView) existing.findViewWithTag("status");
                if (nameTv != null) nameTv.setText(label);
                if (statusIconView != null) {
                    statusIconView.setImageResource(statusIcon);
                    statusIconView.setColorFilter(statusColor);
                }

                // 取消旧的消散任务
                var oldRunnable = dismissRunnables.get(label);
                if (oldRunnable != null) {
                    existing.removeCallbacks(oldRunnable);
                }

                // 重新创建消散任务
                Runnable newRunnable = createDismissRunnable(label);
                existing.postDelayed(newRunnable, 3000);
                dismissRunnables.put(label, newRunnable);
                return;
            }
        }

        // 创建新通知
        var notification = new LinearLayout(this);
        notification.setOrientation(LinearLayout.HORIZONTAL);
        notification.setGravity(Gravity.CENTER_VERTICAL);

        int notifHeight = dp2px(28);
        var bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(notifHeight / 2f);
        bg.setColor(0xCC000000);
        notification.setBackground(bg);

        int padH = dp2px(14);
        int padV = dp2px(5);
        notification.setPadding(padH, padV, padH, padV);

        var itemLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        itemLp.setMargins(0, 0, 0, dp2px(6));
        notification.setLayoutParams(itemLp);

        // 名字 TextView
        var nameTv = new TextView(this);
        nameTv.setTag("name");
        nameTv.setText(label);
        nameTv.setTextSize(12);
        nameTv.setTextColor(COLOR_TEXT_WHITE);
        nameTv.setIncludeFontPadding(false);
        var nameLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        nameLp.rightMargin = dp2px(6);
        nameTv.setLayoutParams(nameLp);
        notification.addView(nameTv);

        // 状态图标 ImageView
        var statusIconView = new ImageView(this);
        statusIconView.setTag("status");
        int iconSize = dp2px(18);
        statusIconView.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        statusIconView.setImageResource(statusIcon);
        statusIconView.setColorFilter(statusColor);
        notification.addView(statusIconView);

        screenNotificationContainer.addView(notification, 0);
        activeNotifications.put(label, notification);

        // 入场动画：从左到右滑入
        notification.setAlpha(0f);
        notification.setTranslationX(-dp2px(120));
        notification.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(350)
            .setInterpolator(new DecelerateInterpolator())
            .start();

        // 消散任务
        Runnable dismissRunnable = createDismissRunnable(label);
        notification.postDelayed(dismissRunnable, 3000);
        dismissRunnables.put(label, dismissRunnable);
    }

    private Runnable createDismissRunnable(String label) {
        return () -> {
            var notification = activeNotifications.get(label);
            if (notification == null || notification.getParent() == null) return;
            notification.animate()
                .alpha(0f)
                .translationX(-dp2px(120))
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    if (screenNotificationContainer != null) {
                        screenNotificationContainer.removeView(notification);
                    }
                    if (activeNotifications != null) {
                        activeNotifications.remove(label);
                    }
                    if (dismissRunnables != null) {
                        dismissRunnables.remove(label);
                    }
                })
                .start();
        };
    }

    private void addPanelContent() {
        View scroll = buildContentScroll();
        scroll.setAlpha(0f);
        contentContainer.addView(scroll);
        scroll.animate()
            .alpha(1f)
            .setDuration(600)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    private void ensureDslLoaded() {
        if (!dslLoaded) {
            dslPages = parseDsl(DslConfig.DSL_TEXT);
            dslLoaded = true;
            DslConfig.initCallbacks(this, callbacks, seekFormatters);
        }
    }
    private ScrollView buildContentScroll() {
        ensureDslLoaded();
        View panelContent;
        switch (currentCategory) {
            case GENERAL:  panelContent = buildContentForCategory(MenuCategory.GENERAL); break;
            case BATTLE:   panelContent = buildContentForCategory(MenuCategory.BATTLE); break;
            case PROPERTY: panelContent = buildContentForCategory(MenuCategory.PROPERTY); break;
            case AIM:      panelContent = buildContentForCategory(MenuCategory.AIM); break;
            case FUNCTION: panelContent = buildContentForCategory(MenuCategory.FUNCTION); break;
            case VISION:   panelContent = buildContentForCategory(MenuCategory.VISION); break;
            case EDIT:     panelContent = buildContentForCategory(MenuCategory.EDIT); break;
            case LIST:     panelContent = buildContentForCategory(MenuCategory.LIST); break;
            case FAST:     panelContent = buildContentForCategory(MenuCategory.FAST); break;
            case TELEPORT: panelContent = buildContentForCategory(MenuCategory.TELEPORT); break;
            case OTHER:    panelContent = buildContentForCategory(MenuCategory.OTHER); break;
            default:       panelContent = buildContentForCategory(MenuCategory.EXIT); break;
        }
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new FrameLayout.LayoutParams(MATCH, MATCH));
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.addView(panelContent);
        scroll.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isTouchOnInteractiveView((ViewGroup) panelContent, event.getX(), event.getY())) return false;
                boolean canScroll = panelContent.getHeight() > v.getHeight();
                if (canScroll) return false;
                float rootX = v.getLeft() + event.getX();
                float rootY = v.getTop() + event.getY();
                startTiltAnimation(rootX, rootY);
                panelDragInitX = params.x; panelDragInitY = params.y;
                panelDragTouchX = event.getRawX(); panelDragTouchY = event.getRawY();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = event.getRawX() - panelDragTouchX;
                float dy = event.getRawY() - panelDragTouchY;
                if (Math.abs(dx) > dp2px(TILT_CANCEL_THRESHOLD_DP) || Math.abs(dy) > dp2px(TILT_CANCEL_THRESHOLD_DP)) cancelTiltAnimation();
                boolean canScroll = panelContent.getHeight() > v.getHeight();
                if (!canScroll) {
                    params.x = (int) (panelDragInitX + dx); params.y = (int) (panelDragInitY + dy);
                    clampPanelPosition();
                    try { windowManager.updateViewLayout(rootView, params); } catch (IllegalArgumentException ignored) {}
                    return true;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                cancelTiltAnimation();
                boolean canScroll = panelContent.getHeight() > v.getHeight();
                if (!canScroll) {
                    clampPanelPosition();
                    try { windowManager.updateViewLayout(rootView, params); } catch (IllegalArgumentException ignored) {}
                    savedBallX = params.x; savedBallY = params.y;
                    return true;
                }
            }
            return false;
        });
        return scroll;
    }


    // ==================== DSL 节点定义 ====================
    private View buildContentForCategory(MenuCategory cat) {
        ensureDslLoaded();
        int idx = cat.ordinal();
        if (idx >= dslPages.size()) return new LinearLayout(this);
        DslPage page = dslPages.get(idx);
        if (page.tabs.isEmpty() || (page.tabs.size() == 1 && page.tabs.get(0).name.equals("default"))) {
            ScrollView sv = new ScrollView(this);
            sv.setLayoutParams(new FrameLayout.LayoutParams(MATCH, MATCH));
            sv.setVerticalScrollBarEnabled(false);
            sv.setOverScrollMode(View.OVER_SCROLL_NEVER);
            if (page.tabs.size() == 1) sv.addView(buildBalancedCards(page.tabs.get(0).cards));
            return sv;
        }
        String[] tabNames = new String[page.tabs.size()];
        View[] tabViews = new View[page.tabs.size()];
        for (int i = 0; i < page.tabs.size(); i++) {
            DslTab tab = page.tabs.get(i);
            tabNames[i] = tab.name;
            tabViews[i] = buildBalancedCards(tab.cards);
        }
        return buildTabbedContent(tabNames, tabViews, cat.ordinal());
    }

    private View buildBalancedCards(List<DslCard> cards) {
        if (cards.isEmpty()) return new LinearLayout(this);
        // 预估高度
        for (DslCard card : cards) {
            int h = dp2px(10);
            for (DslControl ctrl : card.controls) {
                if (ctrl instanceof DslToggle) h += dp2px(32);
                else if (ctrl instanceof DslSeekBar) h += dp2px(48);
                else if (ctrl instanceof DslButton) h += dp2px(34);
            }
            if (card.description != null && !card.description.isEmpty()) h += dp2px(20);
            if (card.hasDivider) h += dp2px(2);
            card.estimatedHeight = h;
        }
        if (cards.size() == 1) return buildCardFromDsl(cards.get(0));

        // 按高度排序后贪心双列分配
        List<DslCard> sorted = new ArrayList<>(cards);
        sorted.sort((a, b) -> b.estimatedHeight - a.estimatedHeight);
        LinearLayout leftCol = new LinearLayout(this);
        leftCol.setOrientation(LinearLayout.VERTICAL);
        leftCol.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH, 1f));
        LinearLayout rightCol = new LinearLayout(this);
        rightCol.setOrientation(LinearLayout.VERTICAL);
        rightCol.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH, 1f));
        int lh = 0, rh = 0;
        for (DslCard card : sorted) {
            View cv = buildCardFromDsl(card);
            if (lh <= rh) { leftCol.addView(cv); lh += card.estimatedHeight; }
            else { rightCol.addView(cv); rh += card.estimatedHeight; }
        }
        LinearLayout dual = new LinearLayout(this);
        dual.setOrientation(LinearLayout.HORIZONTAL);
        dual.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));
        dual.setPadding(dp2px(2), dp2px(2), dp2px(2), 0);
        dual.addView(leftCol); dual.addView(rightCol);
        return dual;
    }

    private View buildCardFromDsl(DslCard dc) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp2px(4), dp2px(3), dp2px(4), dp2px(3));
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp2px(12));
        bg.setColor(dc.color != null ? parseColor(dc.color, 0xF22A2A2A) : 0xF22A2A2A);
        card.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH, WRAP);
        lp.setMargins(dp2px(2), dp2px(2), dp2px(2), dp2px(2));
        card.setLayoutParams(lp);

        if (dc.description != null && !dc.description.isEmpty()) {
            TextView dsc = new TextView(this);
            dsc.setText(dc.description);
            dsc.setTextSize(10);
            dsc.setTextColor(0xAAFFFFFF);
            dsc.setPadding(0, 0, 0, dp2px(2));
            card.addView(dsc);
        }
        if (dc.hasDivider) {
            View div = new View(this);
            div.setBackgroundColor(0x22FFFFFF);
            div.setLayoutParams(new LinearLayout.LayoutParams(MATCH, dp2px(1)));
            card.addView(div);
        }
        for (DslControl ctrl : dc.controls) {
            if (ctrl instanceof DslToggle) card.addView(buildToggleFromDsl((DslToggle)ctrl));
            else if (ctrl instanceof DslSeekBar) card.addView(buildSeekBarFromDsl((DslSeekBar)ctrl));
            else if (ctrl instanceof DslButton) card.addView(buildButtonFromDsl((DslButton)ctrl));
        }
        return card;
    }

    private View buildToggleFromDsl(DslToggle tg) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp2px(6), dp2px(4), dp2px(6), dp2px(4));
        container.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));
        container.setTag("toggle:" + tg.label);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));

        TextView tv = new TextView(this);
        tv.setText(tg.label); tv.setTextSize(12); tv.setTextColor(COLOR_TEXT_WHITE);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP, 1f));
        row.addView(tv);
        final boolean[] checked = {toggleStates.containsKey(tg.label) ? toggleStates.get(tg.label) : tg.defaultOn};
        int tw = dp2px(32), th = dp2px(18), ts = dp2px(14), mo = dp2px(2), mn = tw - ts - mo;
        GradientDrawable tBg = new GradientDrawable();
        tBg.setShape(GradientDrawable.RECTANGLE); tBg.setCornerRadius(th/2f);
        int ac = tg.color != null ? parseColor(tg.color, COLOR_ACCENT) : COLOR_ACCENT;
        tBg.setColor(checked[0] ? ac : 0xFF555555);
        View track = new View(this); track.setBackground(tBg);
        track.setLayoutParams(new FrameLayout.LayoutParams(tw, th));
            track.setTag("accent:" + ac);
        GradientDrawable umbBg = new GradientDrawable();
        umbBg.setShape(GradientDrawable.OVAL); umbBg.setColor(0xFFFFFFFF);
        View thumb = new View(this); thumb.setBackground(umbBg);
        FrameLayout.LayoutParams tp = new FrameLayout.LayoutParams(ts, ts);
        tp.setMargins(checked[0] ? mn : mo, (th-ts)/2, 0, 0);
        thumb.setLayoutParams(tp);
        FrameLayout tc = new FrameLayout(this);
        tc.setLayoutParams(new LinearLayout.LayoutParams(tw, th));
        tc.addView(track); tc.addView(thumb); row.addView(tc);
        row.setOnClickListener(v -> {
            checked[0] = !checked[0]; boolean on = checked[0];
            ValueAnimator colorAnim2 = ValueAnimator.ofArgb(on ? 0xFF555555 : ac, on ? ac : 0xFF555555);
            colorAnim2.setDuration(200);
            colorAnim2.addUpdateListener(a -> tBg.setColor((int)a.getAnimatedValue()));
            colorAnim2.start();
            ValueAnimator posAnim2 = ValueAnimator.ofInt(on ? mo : mn, on ? mn : mo);
            posAnim2.setDuration(350);
            posAnim2.setInterpolator(new OvershootInterpolator(2.5f));
            posAnim2.addUpdateListener(a -> { tp.setMargins((int)a.getAnimatedValue(), (th-ts)/2, 0, 0); thumb.setLayoutParams(tp); });
            posAnim2.start();
            toggleStates.put(tg.label, on);
            showNotification(tg.label, on);
            // 同步快捷键显示
            syncShortcutButton(tg.label);
            if (tg.callback != null && callbacks.containsKey(tg.callback)) callbacks.get(tg.callback).run();
        });
        row.setOnTouchListener((v, e) -> { if (e.getAction() == MotionEvent.ACTION_DOWN) return true; if (e.getAction() == MotionEvent.ACTION_UP) v.performClick(); return true; });
        container.addView(row);
        // 提示文字直接显示在开关下方
        if (tg.tooltip != null && !tg.tooltip.isEmpty()) {
            TextView tip = new TextView(this);
            tip.setText(tg.tooltip);
            tip.setTextSize(10);
            tip.setTextColor(0x88FFFFFF);
            tip.setPadding(dp2px(2), dp2px(1), 0, 0);
            container.addView(tip);
        }
        return container;
    }

    private View buildSeekBarFromDsl(DslSeekBar sb) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));
        LinearLayout tr = new LinearLayout(this);
        tr.setOrientation(LinearLayout.HORIZONTAL);
        tr.setPadding(dp2px(4), dp2px(2), dp2px(4), 0);
        tr.setLayoutParams(new LinearLayout.LayoutParams(MATCH, WRAP));
        TextView nm = new TextView(this);
        nm.setText(sb.label); nm.setTextSize(11); nm.setTextColor(COLOR_TEXT_WHITE);
        nm.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP, 1f));
        tr.addView(nm);
        TextView vl = new TextView(this);
        vl.setTextSize(10); vl.setTextColor(COLOR_TEXT_GRAY);
        tr.addView(vl); c.addView(tr);
        int initProgress = (seekBarValues.containsKey(sb.label) ? seekBarValues.get(sb.label) : sb.defaultValue) - sb.min;
        SeekBar sk = new SeekBar(this);
        sk.setMax(sb.max - sb.min);
        sk.setProgress(initProgress);
        sk.setLayoutParams(new LinearLayout.LayoutParams(MATCH, dp2px(20)));
        int seekColor = sb.color != null ? parseColor(sb.color, COLOR_ACCENT) : COLOR_ACCENT;
        sk.setProgressTintList(android.content.res.ColorStateList.valueOf(seekColor));
        sk.setThumbTintList(android.content.res.ColorStateList.valueOf(seekColor));
        // 先设进度再设监听器，避免 setProgress 触发 onProgressChanged
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) {
                int rv = p + sb.min;
                String dsp = seekFormatters.containsKey(sb.label) ? seekFormatters.get(sb.label).apply(rv) : rv + (sb.unit != null ? sb.unit : "");
                vl.setText(dsp);
                if (f) seekBarValues.put(sb.label, rv);
                if (f && sb.callback != null && callbacks.containsKey(sb.callback)) callbacks.get(sb.callback).run();
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        // 手动更新文本为真实值
        int realVal = seekBarValues.containsKey(sb.label) ? seekBarValues.get(sb.label) : sb.defaultValue;
        String displayVal = seekFormatters.containsKey(sb.label) ? seekFormatters.get(sb.label).apply(realVal) : realVal + (sb.unit != null ? sb.unit : "");
        vl.setText(displayVal);
        c.addView(sk);
        return c;
    }

    private View buildButtonFromDsl(DslButton btn) {
        Button b = new Button(this);
        b.setText(btn.label); b.setTextSize(12); b.setTextColor(COLOR_TEXT_WHITE);
        b.setPadding(dp2px(10), 0, dp2px(10), 0);
        b.setGravity(Gravity.CENTER); b.setMinHeight(0);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE); bg.setCornerRadius(dp2px(8));
        int bc = btn.color != null ? parseColor(btn.color, 0xFF3A3A3A) : 0xFF3A3A3A;
        bg.setColor(bc); b.setBackground(bg);
        b.setOnClickListener(v -> {
            if (btn.callback != null && callbacks.containsKey(btn.callback)) callbacks.get(btn.callback).run();
            showNotification(btn.label, true);
        });
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(MATCH, dp2px(28));
        bp.setMargins(0, 0, 0, dp2px(3));
        b.setLayoutParams(bp);
        final int nc = bc, pc = 0xFF5A5A5A;
        b.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) bg.setColor(pc);
            else if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) bg.setColor(nc);
            return false;
        });
        return b;
    }

    // 供 DslConfig 回调查询开关状态
    public boolean getToggleState(String label) {
        return toggleStates.containsKey(label) ? toggleStates.get(label) : false;
    }

    private int parseColor(String hex, int def) {
        try { return Color.parseColor(hex); } catch (Exception e) { return def; }
    }

    private static class DslPage {
        String name; String icon;
        List<DslTab> tabs = new ArrayList<>();
    }
    private static class DslTab {
        String name;
        List<DslCard> cards = new ArrayList<>();
    }
    private static class DslCard {
        String name; String description; String color;
        boolean hasDivider;
        List<DslControl> controls = new ArrayList<>();
        int estimatedHeight;
    }
    private static abstract class DslControl {
        String label; String callback;
    }
    private static class DslToggle extends DslControl {
        boolean defaultOn; String color; String tooltip;
    }
    private static class DslSeekBar extends DslControl {
        int defaultValue, min, max, step; String unit, color;
    }
    private static class DslButton extends DslControl {
        String color;
    }

    // ==================== DSL 解析器 ====================
    private List<DslPage> parseDsl(String text) {
        List<DslPage> pages = new ArrayList<>();
        String[] lines = text.split("\n");
        DslPage curPage = null; DslTab curTab = null;
        DslCard curCard = null; DslControl curControl = null;
        List<Integer> stack = new ArrayList<>();

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.equals("}")) continue;

            int indent = 0;
            for (int c = 0; c < rawLine.length(); c++) {
                if (rawLine.charAt(c) == ' ') indent++;
                else break;
            }
            int depth = indent / 4;

            while (stack.size() > depth) {
                int top = stack.remove(stack.size() - 1);
                if (top == 3) curCard = null;
                else if (top == 2) curTab = null;
            }

            if (line.startsWith("分页 ")) {
                curPage = new DslPage();
                curPage.name = extractName(line, "分页 ");
                pages.add(curPage);
                stack.clear(); curTab = null; curCard = null; curControl = null;
            } else if (line.startsWith("标签 ")) {
                curTab = new DslTab();
                curTab.name = extractName(line, "标签 ");
                if (curPage != null) curPage.tabs.add(curTab);
                stack.add(2); curCard = null; curControl = null;
            } else if (line.startsWith("卡片 ")) {
                curCard = new DslCard();
                curCard.name = extractName(line, "卡片 ");
                if (curTab != null) curTab.cards.add(curCard);
                else if (curPage != null) {
                    // 分页下直接放卡片（退出分页）
                    DslTab singleTab = new DslTab();
                    singleTab.name = "default";
                    singleTab.cards.add(curCard);
                    curPage.tabs.add(singleTab);
                    curTab = singleTab;
                }
                stack.add(3); curControl = null;
            } else if (line.startsWith("开关 ")) {
                DslToggle tg = new DslToggle();
                tg.label = extractName(line, "开关 ");
                parseProps(line, tg);
                if (curCard != null) curCard.controls.add(tg);
            } else if (line.startsWith("拉条 ")) {
                DslSeekBar sb = new DslSeekBar();
                sb.label = extractName(line, "拉条 ");
                parseProps(line, sb);
                if (curCard != null) curCard.controls.add(sb);
            } else if (line.startsWith("按钮 ")) {
                DslButton btn = new DslButton();
                btn.label = extractName(line, "按钮 ");
                parseProps(line, btn);
                if (curCard != null) curCard.controls.add(btn);
            } else if (line.startsWith("说明 ") && !line.contains("=")) {
                if (curCard != null) {
                    String d = line.substring("说明 ".length()).trim();
                    if (d.endsWith("{")) d = d.substring(0, d.length()-1).trim();
                    curCard.description = d;
                }
            } else if (line.startsWith("分隔线")) {
                if (curCard != null) curCard.hasDivider = true;
            } else if (line.startsWith("颜色 =")) {
                String val = line.substring(line.indexOf('=')+1).trim().replace("{","").trim();
                if (curCard != null) curCard.color = val;
            } else if (line.startsWith("图标 =")) {
                if (curPage != null) curPage.icon = line.substring(line.indexOf('=')+1).trim().replace("{","").trim();
            }
        }
        return pages;
    }

    private String extractName(String line, String prefix) {
        String rest = line.substring(prefix.length()).trim();
        int brace = rest.indexOf('{');
        int space = rest.indexOf(' ');
        int end = rest.length();
        if (brace >= 0 && space >= 0) end = Math.min(brace, space);
        else if (brace >= 0) end = brace;
        else if (space >= 0) end = space;
        return rest.substring(0, end).trim();
    }

    private void parseProps(String line, DslControl ctrl) {
        int brace = line.indexOf('{');
        if (brace < 0) return;
        String props = line.substring(brace + 1);
        int close = props.lastIndexOf('}');
        if (close >= 0) props = props.substring(0, close);
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\S+)\\s*=\\s*(\\S+)").matcher(props);
        while (m.find()) {
            applyProp(ctrl, m.group(1), m.group(2));
        }
    }

    private void applyProp(DslControl ctrl, String key, String val) {
        switch (key) {
            case "回调" -> ctrl.callback = val;
            case "默认" -> {
                if (ctrl instanceof DslToggle) ((DslToggle)ctrl).defaultOn = "开".equals(val);
                else if (ctrl instanceof DslSeekBar) {
                    try { ((DslSeekBar)ctrl).defaultValue = Integer.parseInt(val); } catch (Exception e) {}
                }
            }
            case "最小" -> { if (ctrl instanceof DslSeekBar) try { ((DslSeekBar)ctrl).min = Integer.parseInt(val); } catch (Exception e) {} }
            case "最大" -> { if (ctrl instanceof DslSeekBar) try { ((DslSeekBar)ctrl).max = Integer.parseInt(val); } catch (Exception e) {} }
            case "步长" -> { if (ctrl instanceof DslSeekBar) try { ((DslSeekBar)ctrl).step = Integer.parseInt(val); } catch (Exception e) {} }
            case "单位" -> { if (ctrl instanceof DslSeekBar) ((DslSeekBar)ctrl).unit = val; }
            case "颜色" -> {
                if (ctrl instanceof DslToggle) ((DslToggle)ctrl).color = val;
                else if (ctrl instanceof DslButton) ((DslButton)ctrl).color = val;
                else if (ctrl instanceof DslSeekBar) ((DslSeekBar)ctrl).color = val;
            }
            case "提示" -> { if (ctrl instanceof DslToggle) ((DslToggle)ctrl).tooltip = val; }
        }
    }


    // ==================== 快捷键系统 ====================

    // 由 DSL 回调查用：开启/关闭单个快捷键
    public void toggleShortcut(String label, String callback, String type) {
        // 检查是否已有此快捷键（已显示）
        boolean alreadyShown = false;
        for (View v : shortcutButtons) {
            if (label.equals(v.getTag())) {
                alreadyShown = true;
                break;
            }
        }
        if (alreadyShown) {
            // 已显示→隐藏
            removeShortcutByLabel(label);
        } else {
            // 未显示→创建并显示
            showSingleShortcut(label, callback, "toggle".equals(type));
        }
    }

    private void showSingleShortcut(String label, String callback, boolean isToggle) {
        final int btnH = dp2px(SHORTCUT_H_DP);
        final boolean isOn = isToggle && toggleStates.containsKey(label) && toggleStates.get(label);
        int bgColor = isToggle ? (isOn ? 0xE62196F3 : 0xE6666666) : 0xE61E88E5;

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(btnH / 2f);
        bg.setColor(bgColor);
        bg.setStroke(dp2px(1), 0x33FFFFFF);

        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(12);
        tv.setTextColor(0xEEFFFFFF);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp2px(12), 0, dp2px(12), 0);
        tv.setSingleLine(true);
        tv.setBackground(bg);
        tv.setTag(label);

        tv.setOnClickListener(v -> {
            if (isToggle) {
                boolean nowOn = !(toggleStates.containsKey(label) && toggleStates.get(label));
                toggleStates.put(label, nowOn);

                int nb = nowOn ? 0xE62196F3 : 0xE6666666;
                ((GradientDrawable)((TextView)v).getBackground()).setColor(nb);
                syncToggleView(label);
            }
            showNotification(label, isToggle ? toggleStates.get(label) : true);
            if (callbacks.containsKey(callback)) callbacks.get(callback).run();
        });

        // 拖动
        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                  | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, btnH,
            type, flags, PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;

        if (labelViewPositions.containsKey(label)) {
            float[] pos = labelViewPositions.get(label);
            lp.x = (int) pos[0];
            lp.y = (int) pos[1];
        } else {
            lp.x = dp2px(12) + (shortcutButtons.size() % 2) * (dp2px(80) + dp2px(8));
            lp.y = dp2px(280) + (shortcutButtons.size() / 2) * (btnH + dp2px(8));
        }

        final int[] dragBase = new int[2];
        final int[] dragTouch = new int[2];
        final boolean[] isDragged = {false};
        tv.setOnTouchListener((v, event) -> {
            WindowManager.LayoutParams p = lp;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dragBase[0] = p.x; dragBase[1] = p.y;
                    dragTouch[0] = (int) event.getRawX();
                    dragTouch[1] = (int) event.getRawY();
                    isDragged[0] = false;
                    v.setAlpha(0.7f);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int)(event.getRawX() - dragTouch[0]);
                    int dy = (int)(event.getRawY() - dragTouch[1]);
                    if (Math.abs(dx) > dp2px(5) || Math.abs(dy) > dp2px(5)) isDragged[0] = true;
                    p.x = dragBase[0] + dx;
                    p.y = dragBase[1] + dy;
                    try { windowManager.updateViewLayout(v, p); } catch (Exception ignored) {}
                    return true;
                case MotionEvent.ACTION_UP:
                    v.setAlpha(1f);
                    labelViewPositions.put(label, new float[]{p.x, p.y});
                    if (!isDragged[0]) v.performClick();
                    return true;
            }
            return false;
        });

        try {
            tv.setAlpha(0f);
            tv.setTranslationY(dp2px(35));
            windowManager.addView(tv, lp);
            shortcutButtons.add(tv);
            tv.post(() -> tv.animate()
                .alpha(1f).translationY(0f)
                .setDuration(500).setInterpolator(new OvershootInterpolator(1.6f)).start());
        } catch (Exception ignored) {}
    }

    private void removeShortcutByLabel(String label) {
        View toRemove = null;
        for (View v : shortcutButtons) {
            if (label.equals(v.getTag())) {
                toRemove = v;
                break;
            }
        }
        if (toRemove != null) {
            shortcutButtons.remove(toRemove);
            final View v = toRemove;
            v.animate()
                .alpha(0f).translationYBy(dp2px(20))
                .setDuration(250)
                .withEndAction(() -> { try { windowManager.removeView(v); } catch (Exception ignored) {} })
                .start();
        }
    }


    // 实时同步：快捷键触发后更新主面板的控件显示
    public void syncToggleView(String label) {
        if (rootView == null) return;
        View tagged = rootView.findViewWithTag("toggle:" + label);
        if (tagged == null) return;
        boolean isOn = toggleStates.containsKey(label) && toggleStates.get(label);
        ViewGroup container = (ViewGroup) tagged;
        // 递归查找 FrameLayout（toggle 开关容器）
        findToggleInViewGroup(container, isOn);
    }

    private void findToggleInViewGroup(ViewGroup parent, boolean isOn) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof FrameLayout) {
                FrameLayout fl = (FrameLayout) child;
                if (fl.getChildCount() == 2) {
                    View track = fl.getChildAt(0);
                    View thumb = fl.getChildAt(1);
                    if (track != null && track.getBackground() instanceof GradientDrawable) {
                        // 从tag读取原始强调色，没有则用默认蓝
                        int accentColor = 0xFF2196F3;
                        Object tag = track.getTag();
                        if (tag instanceof String && ((String)tag).startsWith("accent:")) {
                            try { accentColor = (int) Long.parseLong(((String)tag).substring(7), 16); } catch (Exception e) {}
                        }
                        ((GradientDrawable) track.getBackground()).setColor(isOn ? accentColor : 0xFF555555);
                    }
                    if (thumb != null && thumb.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                        FrameLayout.LayoutParams tp = (FrameLayout.LayoutParams) thumb.getLayoutParams();
                        int tw = dp2px(32), ts = dp2px(14), mo = dp2px(2), mn = tw - ts - mo;
                        tp.setMargins(isOn ? mn : mo, (dp2px(18) - ts) / 2, 0, 0);
                        thumb.setLayoutParams(tp);
                    }
                }
            }
            if (child instanceof ViewGroup) {
                findToggleInViewGroup((ViewGroup) child, isOn);
            }
        }
    }


    // 主面板切换后同步快捷键按钮颜色
    private void syncShortcutButton(String label) {
        for (View v : shortcutButtons) {
            if (label.equals(v.getTag())) {
                boolean isOn = toggleStates.containsKey(label) && toggleStates.get(label);
                if (v.getBackground() instanceof GradientDrawable) {
                    ((GradientDrawable) v.getBackground()).setColor(isOn ? 0xE62196F3 : 0xE6666666);
                }
                break;
            }
        }
    }

// ==================== 工具 ====================

    private static final int MATCH = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int WRAP = LinearLayout.LayoutParams.WRAP_CONTENT;

    private int dp2px(int dp) { return (int) (dp * getResources().getDisplayMetrics().density + 0.5f); }
    void toast(String msg) { android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show(); }
}
