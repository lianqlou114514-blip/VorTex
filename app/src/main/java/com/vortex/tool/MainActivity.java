package com.vortex.tool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "vortex_prefs";
    private static final String KEY_INPUT = "saved_input";
    private static final String KEY_EYE_STATE = "eye_state";
    private EditText input;
    private TextView eyeView;
    private boolean isVisible = true;

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
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));
        root.setPadding(0, 0, 0, getNavigationBarHeight());

        // VorTex 标题
        TextView title = new TextView(this);
        title.setText("VorTex");
        title.setTextSize(32);
        title.setTextColor(Color.parseColor("#212121"));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
        titleParams.leftMargin = dp2px(32);
        titleParams.topMargin = dp2px(100) + getStatusBarHeight();
        title.setLayoutParams(titleParams);
        root.addView(title);

        // 小字 "登录"（黑色）
        TextView subTitle = new TextView(this);
        subTitle.setText("登录");
        subTitle.setTextSize(14);
        subTitle.setTextColor(Color.parseColor("#212121"));
        FrameLayout.LayoutParams subParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
        subParams.leftMargin = dp2px(32);
        subParams.topMargin = dp2px(100) + getStatusBarHeight() + dp2px(44);
        subTitle.setLayoutParams(subParams);
        root.addView(subTitle);

        // 中间内容区域
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
        contentParams.gravity = Gravity.CENTER;
        contentParams.leftMargin = dp2px(48);
        contentParams.rightMargin = dp2px(48);
        contentParams.topMargin = dp2px(80);
        content.setLayoutParams(contentParams);

        // 输入框 + 眼睛图标 容器
        FrameLayout inputWrap = new FrameLayout(this);
        inputWrap.setBackground(createRoundRectDrawable(Color.WHITE, dp2px(12), Color.parseColor("#E0E0E0")));
        LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        content.addView(inputWrap, wrapParams);

        // 输入框
        input = new EditText(this);
        input.setHint("请输入内容");
        input.setTextSize(15);
        input.setTextColor(Color.parseColor("#212121"));
        input.setHintTextColor(Color.parseColor("#AAAAAA"));
        input.setBackground(null);
        input.setPadding(dp2px(16), dp2px(14), dp2px(48), dp2px(14));
        FrameLayout.LayoutParams inputParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
        inputWrap.addView(input, inputParams);

        // 眼睛图标
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String saved = prefs.getString(KEY_INPUT, "");
        isVisible = prefs.getBoolean(KEY_EYE_STATE, saved.isEmpty());
        updateInputType();

        eyeView = new TextView(this);
        eyeView.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams eyeParams = new FrameLayout.LayoutParams(
            dp2px(40), FrameLayout.LayoutParams.MATCH_PARENT);
        eyeParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        eyeParams.rightMargin = dp2px(4);
        eyeView.setLayoutParams(eyeParams);
        updateEyeIcon();
        inputWrap.addView(eyeView);

        eyeView.setOnClickListener(v -> {
            isVisible = !isVisible;
            updateInputType();
            updateEyeIcon();
            prefs.edit().putBoolean(KEY_EYE_STATE, isVisible).apply();
        });

        // 间距
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp2px(20)));
        content.addView(spacer);

        // 卡片容器
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(createRoundRectDrawable(Color.WHITE, dp2px(12), Color.parseColor("#E0E0E0")));
        card.setPadding(0, dp2px(4), 0, dp2px(4));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        content.addView(card, cardParams);

        // 登录按钮行
        LinearLayout loginRow = createButtonRow("▶", "登录", Color.parseColor("#2196F3"));
        loginRow.setOnClickListener(v -> onLoginClick());
        card.addView(loginRow);

        // 分隔线
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
        divider.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        card.addView(divider);

        // 解绑按钮行
        LinearLayout unbindRow = createButtonRow("✕", "解绑", Color.parseColor("#2196F3"));
        unbindRow.setOnClickListener(v -> onUnbindClick());
        card.addView(unbindRow);

        root.addView(content);
        setContentView(root);

        if (!saved.isEmpty()) {
            input.setText(saved);
            input.setSelection(saved.length());
        }
    }

    private LinearLayout createButtonRow(String iconText, String labelText, int iconColor) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp2px(16), dp2px(14), dp2px(16), dp2px(14));
        row.setClickable(true);
        row.setBackground(createRippleBg());
        row.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView icon = new TextView(this);
        icon.setText(iconText);
        icon.setTextSize(16);
        icon.setTextColor(iconColor);
        icon.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        iconParams.rightMargin = dp2px(12);
        icon.setLayoutParams(iconParams);
        row.addView(icon);

        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextSize(16);
        label.setTextColor(Color.parseColor("#212121"));
        row.addView(label);

        return row;
    }

    private void updateInputType() {
        int pos = input.getSelectionStart();
        if (isVisible) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        input.setSelection(pos >= 0 ? pos : input.getText().length());
    }

    private void updateEyeIcon() {
        if (isVisible) {
            eyeView.setCompoundDrawablesWithIntrinsicBounds(
                new EyeOpenIconDrawable(this, Color.parseColor("#999999")), null, null, null);
        } else {
            eyeView.setCompoundDrawablesWithIntrinsicBounds(
                new EyeCloseIconDrawable(this, Color.parseColor("#999999")), null, null, null);
        }
    }

    private void onLoginClick() {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_INPUT, text).apply();
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void onUnbindClick() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().remove(KEY_INPUT).remove(KEY_EYE_STATE).apply();
        input.setText("");
        isVisible = true;
        updateInputType();
        updateEyeIcon();
        Toast.makeText(this, "解绑成功", Toast.LENGTH_SHORT).show();
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

    private GradientDrawable createRippleBg() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.TRANSPARENT);
        return drawable;
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    // ========== 矢量眼睛图标绘制 ==========

    /**
     * 睁眼图标
     */
    public static class EyeOpenIconDrawable extends Drawable {
        private final Paint paint;
        private final int size;

        public EyeOpenIconDrawable(Activity activity, int color) {
            this.size = (int) (activity.getResources().getDisplayMetrics().density * 20 + 0.5f);
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.paint.setColor(color);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(size * 0.12f);
            this.paint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            float cx = bounds.centerX();
            float cy = bounds.centerY();
            float w = size * 0.45f;
            float h = size * 0.3f;

            // 眼睛轮廓（上下弧线）
            Path path = new Path();
            path.moveTo(cx - w, cy);
            path.quadTo(cx - w * 0.5f, cy - h, cx, cy - h);
            path.quadTo(cx + w * 0.5f, cy - h, cx + w, cy);
            path.quadTo(cx + w * 0.5f, cy + h, cx, cy + h);
            path.quadTo(cx - w * 0.5f, cy + h, cx - w, cy);
            canvas.drawPath(path, paint);

            // 瞳孔（实心圆）
            Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fillPaint.setColor(paint.getColor());
            fillPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, size * 0.12f, fillPaint);
        }

        @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); }
        @Override public void setColorFilter(ColorFilter colorFilter) { paint.setColorFilter(colorFilter); }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
        @Override public int getIntrinsicWidth() { return size; }
        @Override public int getIntrinsicHeight() { return size; }
    }

    /**
     * 闭眼图标
     */
    public static class EyeCloseIconDrawable extends Drawable {
        private final Paint paint;
        private final int size;

        public EyeCloseIconDrawable(Activity activity, int color) {
            this.size = (int) (activity.getResources().getDisplayMetrics().density * 20 + 0.5f);
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.paint.setColor(color);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(size * 0.12f);
            this.paint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            float cx = bounds.centerX();
            float cy = bounds.centerY();
            float w = size * 0.45f;
            float h = size * 0.3f;

            // 眼睛轮廓（上下弧线）
            Path path = new Path();
            path.moveTo(cx - w, cy);
            path.quadTo(cx - w * 0.5f, cy - h, cx, cy - h);
            path.quadTo(cx + w * 0.5f, cy - h, cx + w, cy);
            path.quadTo(cx + w * 0.5f, cy + h, cx, cy + h);
            path.quadTo(cx - w * 0.5f, cy + h, cx - w, cy);
            canvas.drawPath(path, paint);

            // 斜线（表示不可见）
            Path line = new Path();
            line.moveTo(cx - w * 0.6f, cy - h * 0.6f);
            line.lineTo(cx + w * 0.6f, cy + h * 0.6f);
            canvas.drawPath(line, paint);
        }

        @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); }
        @Override public void setColorFilter(ColorFilter colorFilter) { paint.setColorFilter(colorFilter); }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
        @Override public int getIntrinsicWidth() { return size; }
        @Override public int getIntrinsicHeight() { return size; }
    }

    private int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
