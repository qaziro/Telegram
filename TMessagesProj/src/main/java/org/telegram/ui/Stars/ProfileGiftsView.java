package org.telegram.ui.Stars;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.AndroidUtilities.lerp;
import static org.telegram.ui.Stars.StarsController.findAttribute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.ProfileActivity;

import java.util.ArrayList;
import java.util.HashSet;

public class ProfileGiftsView extends View implements NotificationCenter.NotificationCenterDelegate {

    public static final int DRAW_MODE_GIFTS = 1;
    public static final int DRAW_MODE_PATTERNS = 2;
    public static final int DRAW_MODE_BOTH = 3;

    private final int currentAccount;
    private final long dialogId;
    private final View avatarContainer;
    private final ProfileActivity.AvatarImageView avatarImage;
    private final Theme.ResourcesProvider resourcesProvider;
    private final int drawMode;
    private final Drawable patternDrawable;

    private static final GiftAnimationProperties[] GIFT_ANIMATION_PATTERN = {
            new GiftAnimationProperties(40f, 70f,   0.3f, 1.0f, 0f, 0f,    0.2f, 0.8f, true, 1.0f, 1.0f),
            new GiftAnimationProperties(80f, 90f,   0.3f, 1.0f, 0f, 0f, 0.1f, 0.7f, false, 1.0f, 1.0f),
            new GiftAnimationProperties(40f, 120f,  0.3f, 1.0f, 0f, 0f,    0.5f, 0.95f, false, 1.0f, 1.0f),
            new GiftAnimationProperties(40f, 250f,  0.3f, 1.0f, 0f, 0f,    0.2f, 0.8f, false, 1.0f, 1.0f),
            new GiftAnimationProperties(80f, 270f,  0.3f, 1.0f, 0f, 0f,  0.1f, 0.7f, false, 1.0f, 1.0f),
            new GiftAnimationProperties(40f, 290f,  0.3f, 1.0f, 0f, 0f,    0.5f, 0.95f, false, 1.0f, 1.0f)
    };

    private static final GiftAnimationProperties[] STAR_ANIMATION_PATTERN = new GiftAnimationProperties[]{
            new GiftAnimationProperties(15f, 0.0f, 0.3f, 1.13f, 0f, 0f, 0.503f, 0.934f, false, 0.1f, 0.3f),
            new GiftAnimationProperties(25f, 65f, 0.3f, 1.10f, 0f, 0f, 0.534f, 0.943f, false,  0.15f, 0.25f),
            new GiftAnimationProperties(45f, 90f, 0.3f, 1.13f, 0f, 0f, 0.465f, 0.803f, false, 0.12f,0.22f),
            new GiftAnimationProperties(28f, 120f, 0.3f, 1.107f, 0f, 0f, 0.592f, 0.985f, false, 0.07f, 0.17f),
            new GiftAnimationProperties(18f, 180f, 0.3f, 1.13f, 0f, 0f, 0.523f, 0.936f, false, 0.02f, 0.12f),
            new GiftAnimationProperties(28f, 240f, 0.3f, 1.107f, 0f, 0f, 0.554f, 0.943f, false, 0.07f, 0.17f),
            new GiftAnimationProperties(45f, 270f, 0.3f, 1.13f, 0f, 0f, 0.466f, 0.804f, false, 0.12f, 0.22f),
            new GiftAnimationProperties(25f, 295f, 0.3f, 1.10f, 0f, 0f, 0.513f, 0.937f, false, 0.25f, 0.25f),

            new GiftAnimationProperties(37f, 35f, 0.3f, 0.92f, 0f, 0f, 0.445f, 0.846f, false, 0.047f, 0.147f),
            new GiftAnimationProperties(65f, 65f, 0.3f, 0.95f, 0f, 0f, 0.376f, 0.937f, false, 0.023f, 0.123f),
            new GiftAnimationProperties(95f, 90f, 0.3f, 0.98f, 0f, 0f, 0.283f, 0.706f, false, 0.013f, 0.113f),
            new GiftAnimationProperties(67f, 120f, 0.3f, 1.10f, 0f, 0f, 0.534f, 0.946f, false, 0.05952f, 0.0952f),
            new GiftAnimationProperties(61f, 150f, 0.3f, 1.17f, 0f, 0f, 0.165f, 0.762f, false, 0.05914f, 0.0914f),

            new GiftAnimationProperties(61f, 210f, 0.3f, 1.17f, 0f, 0f, 0.184f, 0.793f, false, 0.05914f, 0.0914f),
            new GiftAnimationProperties(67f, 240f, 0.3f, 1.10f, 0f, 0f, 0.526f, 0.923f, false, 0.05952f, 0.0952f),
            new GiftAnimationProperties(95f, 270f, 0.3f, 0.98f, 0f, 0f, 0.293f, 0.703f, false, 0.0113f, 0.113f),
            new GiftAnimationProperties(65f, 295f, 0.3f, 0.95f, 0f, 0f, 0.184f, 0.915f, false, 0.0123f, 0.123f),
            new GiftAnimationProperties(37f, 325f, 0.3f, 0.93f, 0f, 0f, 0.313f, 0.844f, false, 0.0147f, 0.147f)
    };

    public ProfileGiftsView(Context context, int currentAccount, long dialogId, @NonNull View avatarContainer, ProfileActivity.AvatarImageView avatarImage, Theme.ResourcesProvider resourcesProvider, int drawMode, @Nullable Drawable patternDrawable) {
        super(context);
        this.currentAccount = currentAccount;
        this.dialogId = dialogId;
        this.avatarContainer = avatarContainer;
        this.avatarImage = avatarImage;
        this.resourcesProvider = resourcesProvider;
        this.drawMode = drawMode;
        this.patternDrawable = patternDrawable;
    }

    private float expandProgress;
    public void setExpandProgress(float progress) {
        if (this.expandProgress != progress) {
            this.expandProgress = progress;
            invalidate();
        }
    }

    private float actionBarProgress;
    public void setActionBarActionMode(float progress) {
        actionBarProgress = progress;
        invalidate();
    }

    private float scrollProgress = 0.0f;
    public void setScrollProgress(float progress) {
        if (this.scrollProgress != progress) {
            this.scrollProgress = progress;
            invalidate();
        }
    }

    private float avatarMiddleScale = 1.0f;
    public void setAvatarMiddleScale(float scale) {
        if (this.avatarMiddleScale != scale) {
            this.avatarMiddleScale = scale;
            invalidate();
        }
    }

    private float avatarMiddleYdp = 1.0f;
    public void setAvatarMiddleY(float y) {
        if (this.avatarMiddleYdp != y) {
            this.avatarMiddleYdp = y;
            invalidate();
        }
    }

    private float left, right, cy;
    private float expandRight, expandY;
    private boolean expandRightPad;
    private final AnimatedFloat expandRightPadAnimated = new AnimatedFloat(this, 0, 350, CubicBezierInterpolator.EASE_OUT_QUINT);
    private final AnimatedFloat rightAnimated = new AnimatedFloat(this, 0, 350, CubicBezierInterpolator.EASE_OUT_QUINT);

    public void setBounds(float left, float right, float cy, boolean animated) {
        boolean changed = Math.abs(left - this.left) > 0.1f || Math.abs(right - this.right) > 0.1f || Math.abs(cy - this.cy) > 0.1f;
        this.left = left;
        this.right = right;
        if (!animated) {
            this.rightAnimated.set(this.right, true);
        }
        this.cy = cy;
        if (changed) {
            invalidate();
        }
    }

    public void setExpandCoords(float right, boolean rightPadded, float y) {
        this.expandRight = right;
        this.expandRightPad = rightPadded;
        this.expandY = y;
        invalidate();
    }

    private float progressToInsets = 1f;
    public void setProgressToStoriesInsets(float progressToInsets) {
        if (this.progressToInsets == progressToInsets) {
            return;
        }
        this.progressToInsets = progressToInsets;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.starUserGiftsLoaded);
        for (Gift gift : gifts) {
            if (gift.emojiDrawable != null) {
                gift.emojiDrawable.addView(this);
            }
        }
        update();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.starUserGiftsLoaded);
        for (Gift gift : gifts) {
            if (gift.emojiDrawable != null) {
                gift.emojiDrawable.removeView(this);
            }
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.starUserGiftsLoaded) {
            if ((long) args[0] == dialogId) {
                update();
            }
        }
    }

    public final class Gift {

        public final long id;
        public final TLRPC.Document document;
        public final long documentId;
        public final int color;
        public final String slug;

        public Gift(TL_stars.TL_starGiftUnique gift) {
            id = gift.id;
            document = gift.getDocument();
            documentId = document == null ? 0 : document.id;
            final TL_stars.starGiftAttributeBackdrop backdrop = findAttribute(gift.attributes, TL_stars.starGiftAttributeBackdrop.class);
            color = backdrop.center_color | 0xFF000000;
            slug = gift.slug;
        }

        public Gift(TLRPC.TL_emojiStatusCollectible status) {
            id = status.collectible_id;
            document = null;
            documentId = status.document_id;
            color = status.center_color | 0xFF000000;
            slug = status.slug;
        }

        public boolean equals(Gift b) {
            return b != null && b.id == id;
        }

        public RadialGradient gradient;
        public final Matrix gradientMatrix = new Matrix();
        public Paint gradientPaint;
        public AnimatedEmojiDrawable emojiDrawable;
        public AnimatedFloat animatedFloat;

        public final RectF bounds = new RectF();
        public final ButtonBounce bounce = new ButtonBounce(ProfileGiftsView.this);

        public void copy(Gift b) {
            gradient = b.gradient;
            emojiDrawable = b.emojiDrawable;
            gradientPaint = b.gradientPaint;
            animatedFloat = b.animatedFloat;
        }

        public void draw(Canvas canvas, float cx, float cy, float ascale, float rotate, float alpha, float gradientAlpha) {
            if (alpha <= 0.0f) return;
            final float gsz = dp(45);
            bounds.set(cx - gsz / 2, cy - gsz / 2, cx + gsz / 2, cy + gsz / 2);

            canvas.save();
            canvas.translate(cx, cy);
            canvas.rotate(rotate);
            final float scale = ascale * bounce.getScale(0.1f);
            canvas.scale(scale, scale);
            if (gradientPaint != null) {
                gradientPaint.setAlpha((int) (0xFF * alpha * gradientAlpha));
                canvas.drawRect(-gsz / 2.0f, -gsz / 2.0f, gsz / 2.0f, gsz / 2.0f, gradientPaint);
            }
            if (emojiDrawable != null) {
                final int sz = dp(24);
                emojiDrawable.setBounds(-sz / 2, -sz / 2, sz / 2, sz / 2);
                emojiDrawable.setAlpha((int) (0xFF * alpha));
                emojiDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }

    private StarsController.GiftsList list;

    public final ArrayList<Gift> oldGifts = new ArrayList<>();
    public final ArrayList<Gift> gifts = new ArrayList<>();
    public final HashSet<Long> giftIds = new HashSet<>();
    public int maxCount;

    public void update() {
        if ((drawMode == DRAW_MODE_PATTERNS) || !MessagesController.getInstance(currentAccount).enableGiftsInProfile) {
            return;
        }

        maxCount = MessagesController.getInstance(currentAccount).stargiftsPinnedToTopLimit;
        oldGifts.clear();
        oldGifts.addAll(gifts);
        gifts.clear();
        giftIds.clear();

        final TLRPC.EmojiStatus emojiStatus;
        if (dialogId >= 0) {
            final TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(dialogId);
            emojiStatus = user == null ? null : user.emoji_status;
        } else {
            final TLRPC.User chat = MessagesController.getInstance(currentAccount).getUser(-dialogId);
            emojiStatus = chat == null ? null : chat.emoji_status;
        }
        if (emojiStatus instanceof TLRPC.TL_emojiStatusCollectible) {
            giftIds.add(((TLRPC.TL_emojiStatusCollectible) emojiStatus).collectible_id);
        }
        list = StarsController.getInstance(currentAccount).getProfileGiftsList(dialogId);
        if (list != null) {
            for (int i = 0; i < list.gifts.size(); i++) {
                final TL_stars.SavedStarGift savedGift = list.gifts.get(i);
                if (!savedGift.unsaved && savedGift.pinned_to_top && savedGift.gift instanceof TL_stars.TL_starGiftUnique) {
                    final Gift gift = new Gift((TL_stars.TL_starGiftUnique) savedGift.gift);
                    if (!giftIds.contains(gift.id)) {
                        gifts.add(gift);
                        giftIds.add(gift.id);
                    }
                }
            }
        }

        boolean changed = false;
        if (gifts.size() != oldGifts.size()) {
            changed = true;
        } else for (int i = 0; i < gifts.size(); i++) {
            if (!gifts.get(i).equals(oldGifts.get(i))) {
                changed = true;
                break;
            }
        }

        for (int i = 0; i < gifts.size(); i++) {
            final Gift g = gifts.get(i);
            Gift oldGift = null;
            for (int j = 0; j < oldGifts.size(); ++j) {
                if (oldGifts.get(j).id == g.id) {
                    oldGift = oldGifts.get(j);
                    break;
                }
            }

            if (oldGift != null) {
                g.copy(oldGift);
            } else {
                g.gradient = new RadialGradient(0, 0, dp(22.5f), new int[] { g.color, Theme.multAlpha(g.color, 0.0f) }, new float[] { 0, 1 }, Shader.TileMode.CLAMP);
                g.gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                g.gradientPaint.setShader(g.gradient);
                if (g.document != null) {
                    g.emojiDrawable = AnimatedEmojiDrawable.make(currentAccount, AnimatedEmojiDrawable.CACHE_TYPE_MESSAGES, g.document);
                } else {
                    g.emojiDrawable = AnimatedEmojiDrawable.make(currentAccount, AnimatedEmojiDrawable.CACHE_TYPE_MESSAGES, g.documentId);
                }
                g.animatedFloat = new AnimatedFloat(this, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
                g.animatedFloat.force(0.0f);
                if (isAttachedToWindow() && g.emojiDrawable != null) {
                    g.emojiDrawable.addView(this);
                }
            }
        }

        for (int i = 0; i < oldGifts.size(); i++) {
            final Gift g = oldGifts.get(i);
            Gift newGift = null;
            for (int j = 0; j < gifts.size(); ++j) {
                if (gifts.get(j).id == g.id) {
                    newGift = gifts.get(j);
                    break;
                }
            }
            if (newGift == null) {
                if (g.emojiDrawable != null) g.emojiDrawable.removeView(this);
                g.emojiDrawable = null;
                g.gradient = null;
            }
        }
        if (changed)
            invalidate();
    }

    public final AnimatedFloat animatedCount = new AnimatedFloat(this, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        boolean shouldDrawPatterns = (drawMode == DRAW_MODE_PATTERNS || drawMode == DRAW_MODE_BOTH) && patternDrawable != null;
        boolean shouldDrawGifts = (drawMode == DRAW_MODE_GIFTS || drawMode == DRAW_MODE_BOTH) && !gifts.isEmpty();

        if (expandProgress >= 1.0f || (!shouldDrawPatterns && !shouldDrawGifts)) {
            return;
        }

        final float ax = avatarContainer.getX();
        final float ay = avatarContainer.getY();
        final float acx = ax + avatarContainer.getWidth() / 2.0f;
        final float acy = ay + avatarContainer.getHeight() / 2.0f;
        final float cx = getWidth() / 2.0f;

        canvas.save();
        canvas.clipRect(0, 0, getWidth(), expandY);

        final float closedAlpha = Utilities.clamp01((float) (expandY - (AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight())) / dp(50));
        final float startX = acx;
        final float startY = acy;
        final float avatarScale = avatarContainer.getScaleX();
        final float endAvatarRadius = avatarContainer.getWidth() * avatarMiddleScale / 2 + (avatarScale > avatarMiddleScale ? dp(50f) * ((avatarScale - avatarMiddleScale) / avatarMiddleScale) : 0);
        final float avatarMiddleY = dp(avatarMiddleYdp) + avatarContainer.getHeight() / 2f;
        final float overallAnimationProgress = getProgressWithinThresholds(scrollProgress, 0.3f, 1f);
        final float baseAlpha = 1.0f - expandProgress;
        final float actionBarFadeMultiplier = (1.0f - actionBarProgress) * closedAlpha;

        if (shouldDrawPatterns) {
            for (GiftAnimationProperties props : STAR_ANIMATION_PATTERN) {
                final float individualScrollProgress = getProgressWithinThresholds(overallAnimationProgress, props.startThreshold, props.endThreshold);
                if (individualScrollProgress <= 0.0f) continue;

                final float currentAlpha = lerp(props.startAlpha, props.endAlpha, individualScrollProgress);
                final float finalAlpha = baseAlpha * actionBarFadeMultiplier * currentAlpha;
                if (finalAlpha <= 0) continue;

                final float scale = lerp(props.startScale, props.endScale, scrollProgress);
                final float distance = endAvatarRadius + dp(props.distanceDp);
                float[] endPos = calculatePosition(acx, avatarMiddleY, distance, props.angle);
                float endX = endPos[0];
                float endY = endPos[1];
                float currentX = lerp(startX, endX, individualScrollProgress);
                float currentY = lerp(startY, endY, individualScrollProgress);
                currentX = lerp(currentX, cx, 0.2f * actionBarProgress * individualScrollProgress);
                final float currentRotation = lerp(props.startRotation, props.endRotation, individualScrollProgress);

                canvas.save();
                canvas.translate(currentX, currentY);
                canvas.rotate(currentRotation);
                canvas.scale(scale, scale);
                int size = dp(20);
                patternDrawable.setBounds(-size / 2, -size / 2, size / 2, size / 2);
                patternDrawable.setAlpha((int) (255 * finalAlpha));
                patternDrawable.draw(canvas);
                canvas.restore();
            }
        }

        if (shouldDrawGifts) {
            for (int i = 0; i < Math.min(gifts.size(), GIFT_ANIMATION_PATTERN.length); ++i) {
                final Gift gift = gifts.get(i);
                final GiftAnimationProperties props = GIFT_ANIMATION_PATTERN[i];
                final float giftAppearProgress = gift.animatedFloat.set(1.0f);
                if (giftAppearProgress <= 0.0f) continue;

                final float individualScrollProgress = getProgressWithinThresholds(overallAnimationProgress, props.startThreshold, props.endThreshold);
                if (individualScrollProgress <= 0.0f) continue;

                final float currentAlpha = lerp(props.startAlpha, props.endAlpha, individualScrollProgress);
                final float finalAlpha;
                final float gradientAlphaMultiplier;
                if (props.resistsActionBarFade) {
                    finalAlpha = baseAlpha * giftAppearProgress * currentAlpha;
                    gradientAlphaMultiplier = actionBarFadeMultiplier;
                } else {
                    finalAlpha = baseAlpha * actionBarFadeMultiplier * giftAppearProgress * currentAlpha;
                    gradientAlphaMultiplier = 1.0f;
                }
                if (finalAlpha <= 0) continue;

                final float scale = lerp(props.startScale, props.endScale, scrollProgress);
                final float distance = endAvatarRadius + dp(props.distanceDp);
                float[] endPos = calculatePosition(acx, avatarMiddleY, distance, props.angle);
                float endX = endPos[0];
                float endY = endPos[1];
                float currentX = lerp(startX, endX, individualScrollProgress);
                float currentY = lerp(startY, endY, individualScrollProgress);
                currentX = lerp(currentX, cx, 0.2f * actionBarProgress * individualScrollProgress);
                final float currentRotation = lerp(props.startRotation, props.endRotation, individualScrollProgress);

                gift.draw(canvas, currentX, currentY, scale, currentRotation, finalAlpha, gradientAlphaMultiplier);
            }
        }
        canvas.restore();
    }

    private final float[] position = new float[2];
    private float[] calculatePosition(float centerX, float centerY, float distance, float angle) {
        double angleInRadians = Math.toRadians(angle - 90);
        position[0] = (float) (centerX + distance * Math.cos(angleInRadians));
        position[1] = (float) (centerY + distance * Math.sin(angleInRadians));
        return position;
    }

    private float getProgressWithinThresholds(float globalProgress, float startThreshold, float endThreshold) {
        if (startThreshold >= endThreshold) {
            return globalProgress >= endThreshold ? 1.0f : 0.0f;
        }
        float rawProgress = (globalProgress - startThreshold) / (endThreshold - startThreshold);
        return MathUtils.clamp(rawProgress, 0.0f, 1.0f);
    }

    public Gift getGiftUnder(float x, float y) {
        if (drawMode == DRAW_MODE_PATTERNS) return null;
        for (int i = 0; i < gifts.size(); ++i) {
            if (gifts.get(i).bounds.contains(x, y))
                return gifts.get(i);
        }
        return null;
    }

    private Gift pressedGift;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawMode == DRAW_MODE_PATTERNS) return false;
        final Gift hit = getGiftUnder(event.getX(), event.getY());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pressedGift = hit;
            if (pressedGift != null) {
                pressedGift.bounce.setPressed(true);
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (pressedGift != hit && pressedGift != null) {
                pressedGift.bounce.setPressed(false);
                pressedGift = null;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pressedGift != null) {
                onGiftClick(pressedGift);
                pressedGift.bounce.setPressed(false);
                pressedGift = null;
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (pressedGift != null) {
                pressedGift.bounce.setPressed(false);
                pressedGift = null;
            }
        }
        return pressedGift != null;
    }

    public void onGiftClick(Gift gift) {
        Browser.openUrl(getContext(), "https://t.me/nft/" + gift.slug);
    }

    private static class GiftAnimationProperties {
        final float distanceDp;
        final float angle;
        final float startScale;
        final float endScale;
        final float startRotation;
        final float endRotation;
        final float startThreshold;
        final float endThreshold;
        final boolean resistsActionBarFade;
        final float startAlpha;
        final float endAlpha;

        private GiftAnimationProperties(float distanceDp, float angle, float startScale, float endScale, float startRotation, float endRotation, float startThreshold, float endThreshold, boolean resistsActionBarFade, float startAlpha, float endAlpha) {
            this.distanceDp = distanceDp;
            this.angle = angle;
            this.startScale = startScale;
            this.endScale = endScale;
            this.startRotation = startRotation;
            this.endRotation = endRotation;
            this.startThreshold = startThreshold;
            this.endThreshold = endThreshold;
            this.resistsActionBarFade = resistsActionBarFade;
            this.startAlpha = startAlpha;
            this.endAlpha = endAlpha;
        }
    }
}