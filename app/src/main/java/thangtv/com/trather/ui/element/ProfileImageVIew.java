package thangtv.com.trather.ui.element;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;


/**
 * Created by Administrator on 12/Aug/15.
 */
public class ProfileImageVIew extends ImageView {
    private String mUrl;
    private int mDefaultImageId;
    private int mErrorImageId;
    private ImageLoader mImageLoader;
    private ImageLoader.ImageContainer mImageContainer;

    public ProfileImageVIew(Context context) {
        this(context, (AttributeSet)null);
    }

    public ProfileImageVIew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileImageVIew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String url, ImageLoader imageLoader) {
        this.mUrl = url;
        this.mImageLoader = imageLoader;
        this.loadImageIfNecessary(false);
    }

    public void setDefaultImageResId(int defaultImage) {
        this.mDefaultImageId = defaultImage;
    }

    public void setErrorImageResId(int errorImage) {
        this.mErrorImageId = errorImage;
    }

    void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = this.getWidth();
        int height = this.getHeight();
        boolean wrapWidth = false;
        boolean wrapHeight = false;
        if(this.getLayoutParams() != null) {
            wrapWidth = this.getLayoutParams().width == -2;
            wrapHeight = this.getLayoutParams().height == -2;
        }

        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if(width != 0 || height != 0 || isFullyWrapContent) {
            if(TextUtils.isEmpty(this.mUrl)) {
                if(this.mImageContainer != null) {
                    this.mImageContainer.cancelRequest();
                    this.mImageContainer = null;
                }

                this.setDefaultImageOrNull();
            } else {
                if(this.mImageContainer != null && this.mImageContainer.getRequestUrl() != null) {
                    if(this.mImageContainer.getRequestUrl().equals(this.mUrl)) {
                        return;
                    }

                    this.mImageContainer.cancelRequest();
                    this.setDefaultImageOrNull();
                }

                int maxWidth = wrapWidth?0:width;
                int maxHeight = wrapHeight?0:height;
                ImageLoader.ImageContainer newContainer = this.mImageLoader.get(this.mUrl, new ImageLoader.ImageListener() {
                    public void onErrorResponse(VolleyError error) {
                        if(ProfileImageVIew.this.mErrorImageId != 0) {
                            ProfileImageVIew.this.setImageResource(ProfileImageVIew.this.mErrorImageId);
                        }

                    }

                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        if(isImmediate && isInLayoutPass) {
                            ProfileImageVIew.this.post(new Runnable() {
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                        } else {
                            if(response.getBitmap() != null) {
                                ProfileImageVIew.this.setImageDrawable(new RoundedAvatarDrawable(response.getBitmap()));
                            } else if(ProfileImageVIew.this.mDefaultImageId != 0) {
                                ProfileImageVIew.this.setImageResource(ProfileImageVIew.this.mDefaultImageId);
                            }
                        }
                    }
                }, maxWidth, maxHeight);
                this.mImageContainer = newContainer;
            }
        }
    }

    private void setDefaultImageOrNull() {
        if(this.mDefaultImageId != 0) {
            this.setImageResource(this.mDefaultImageId);
        } else {
            this.setImageBitmap((Bitmap)null);
        }

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.loadImageIfNecessary(true);
    }

    protected void onDetachedFromWindow() {
        if(this.mImageContainer != null) {
            this.mImageContainer.cancelRequest();
            this.setImageBitmap((Bitmap)null);
            this.mImageContainer = null;
        }

        super.onDetachedFromWindow();
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.invalidate();
    }
}
