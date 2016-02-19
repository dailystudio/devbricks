package com.dailystudio.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dailystudio.development.Logger;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.MeasureSpec;

public class BitmapUtils {

	public static int estimateSampleSize(String filePath, 
			int destWidth, int destHeight) {
		return estimateSampleSize(filePath, destWidth, destHeight, 0);
	}
	
	public static int estimateSampleSize(String filePath, 
			int destWidth,
			int destHeight,
			int orientation) {
		if (filePath == null) {
			return 0;
		}
		
		if (destWidth <= 0 || destHeight <= 0) {
			return 0;
		}
		
		final int tw = destWidth;
		final int th = destHeight;
		int sw = 0;
		int sh = 0;
		
		final Options opts = new Options();
		opts.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeFile(filePath, opts);
		} catch (OutOfMemoryError e) {
			Logger.debug("decode bound failure: %s", e.toString());
			sw = 0;
			sh = 0;
		}
		
/*		Logger.debug("bitmap = [%-3d x %-3d], thumb = [%-3d x %-3d]", 
				opts.outWidth,
				opts.outHeight,
				tw, th);
*/		
		sw = opts.outWidth;
		sh = opts.outHeight;
		
        if(orientation == 90 || orientation == 270){
        	sw = opts.outHeight;
            sh = opts.outWidth;
        }
		
		return Math.min(sw / tw, sh / th);
	}

	public static Bitmap rotateBitmap(Bitmap source, int degrees) {
		if (degrees != 0 && source != null) {
			Matrix m = new Matrix();
			
			m.setRotate(degrees, (float) source.getWidth() / 2, 
					(float) source.getHeight() / 2);

			try {
				Bitmap rbitmap = Bitmap.createBitmap(
						source, 
						0, 0, 
						source.getWidth(), source.getHeight(), 
						m, true);
				if (source != rbitmap) {
					source.recycle();
					source = rbitmap;
				}
			} catch (OutOfMemoryError ex) {
				Logger.debug("rotate bimtap failure: %s", ex.toString());
			}
		}
		
		return source;
	}
	
	public static Bitmap scaleBitmap(Bitmap bitmap,
			int destWidth, int destHeight) {
		if (bitmap == null) {
			return null;
		}
		
		if (destWidth <= 0 || destHeight <= 0) {
			return bitmap;
		}
		
		Bitmap newBitmap = bitmap;

		final int owidth = bitmap.getWidth();
		final int oheight = bitmap.getHeight();
		final int nwidth = destWidth;
		final int nheight = destHeight;

		if (owidth > nwidth) {
			if (oheight > nheight) {
				float scaleWidth = ((float) nwidth / owidth);
				float scaleHeight = ((float) nheight / oheight);
				if (scaleWidth > scaleHeight) {
					Bitmap tempBitmap = createScaledBitmap(bitmap, scaleWidth,
							owidth, oheight);
					if (tempBitmap != null) {
						newBitmap = createClippedBitmap(tempBitmap, 0,
								(tempBitmap.getHeight() - nheight) / 2, nwidth,
								nheight);
					}
				} else {
					Bitmap tempBitmap = createScaledBitmap(bitmap, scaleHeight,
							owidth, oheight);
					if (tempBitmap != null) {
						newBitmap = createClippedBitmap(tempBitmap,
								(tempBitmap.getWidth() - nwidth) / 2, 0,
								nwidth, nheight);
					}
				}

			} else {
				newBitmap = createClippedBitmap(bitmap,
						(bitmap.getWidth() - nwidth) / 2, 0, nwidth, oheight);
			}

		} else if (owidth <= nwidth) {
			if (oheight > nheight) {
				newBitmap = createClippedBitmap(bitmap, 0,
						(bitmap.getHeight() - nheight) / 2, owidth, nheight);
			}
		}

		return newBitmap;
	}
	
	private static Bitmap createScaledBitmap(Bitmap bitmap, float scale, 
			int width, int height) {
		if (bitmap == null) {
			return null;
		}
		
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		
		return scaledBitmap;
	}

	private static Bitmap createClippedBitmap(Bitmap bitmap, int x, int y, 
			int width, int height) {
		if (bitmap == null) {
			return null;
		}
		
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
		
		return newBitmap;
	}
	
	public static boolean saveBitmap(Bitmap bitmap, String filename) {
		return saveBitmap(bitmap, filename, 100);
	}
	
	public static boolean saveBitmap(Bitmap bitmap, String filename, int quailty) {
		if (filename == null) {
			return false;
		}
		
		File file = new File(filename);
		
		return saveBitmap(bitmap, file, quailty);
	}
	
	public static boolean saveBitmap(Bitmap bitmap, File file) {
		return saveBitmap(bitmap, file, 100);
	}
	
	public static boolean saveBitmap(Bitmap bitmap, File file, int quailty) {
		if (bitmap == null || file == null) {
			return false;
		}
		
		boolean success = false;
		try {
			FileOutputStream out = new FileOutputStream(file);
			CompressFormat format =
					(quailty >= 100 ? Bitmap.CompressFormat.PNG
							: Bitmap.CompressFormat.JPEG);
			
			Logger.debug("save bitmap: %s, [quality: %d, format: %s]",
					file, quailty, format);
			final boolean ret = bitmap.compress(format, quailty, out);
			
			out.flush();
			out.close();
			
			success = ret;
		} catch (IOException e) {
			Logger.debug("save bitmap failure: %s", e.toString());
			
			success = false;
		}
		
		return success;
	}

	public static Bitmap createColorFiltedBitmap(Bitmap origBitmap, 
			ColorMatrix cm) {
		if (origBitmap == null || cm == null) {
			return origBitmap;
		}
		
		final int width = origBitmap.getWidth();
		final int height = origBitmap.getHeight();
		if (width <= 0 || height <= 0) {
			return origBitmap;
		}
		
		Bitmap filteredBitmap = Bitmap.createBitmap(width,
				height, Config.ARGB_8888);
		
		Canvas c = new Canvas(filteredBitmap);
	    
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);      
		
	    Paint paint = new Paint();
		paint.setColorFilter(f);
			    
	    c.drawBitmap(origBitmap, 0, 0, paint);
	    
	    return filteredBitmap;
	}
	
	public static Bitmap createGrayScaledBitmap(Bitmap origBitmap) {
		ColorMatrix cm = new ColorMatrix();      
		cm.setSaturation(0);      
		
		return createColorFiltedBitmap(origBitmap, cm);
	}

	public static Bitmap createViewSnapshot(Context context, 
			View view,
			int desireWidth, int desireHeight) {
		if (view == null) {
			return null;
		}
		
		int widthMeasureSpec;
		int heightMeasureSpec;
		
		if (desireWidth <= 0) {
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(
					desireWidth, MeasureSpec.UNSPECIFIED);
		} else {
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(
					desireWidth, MeasureSpec.EXACTLY);
		}
		
		if (desireHeight <= 0) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					desireHeight, MeasureSpec.UNSPECIFIED);
		} else {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					desireHeight, MeasureSpec.EXACTLY);
		}
		
		view.measure(widthMeasureSpec, heightMeasureSpec);
		Logger.debug("MEASURED: [%d, %d]", 
				view.getMeasuredWidth(),
				view.getMeasuredHeight());
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		
		Config config = Bitmap.Config.ARGB_8888;
		
		Bitmap bitmap = null;
		
		try {
			bitmap = Bitmap.createBitmap(
					desireWidth, desireHeight, config);
		} catch (OutOfMemoryError e) {
			Logger.warnning("create cache bitmap failure: %s",
					e.toString());
			
			bitmap = null;
		}
		
		if (bitmap == null) {
			return null;
		}
		
		Canvas canvas = new Canvas(bitmap);
		
		view.draw(canvas);
		
		return bitmap;
	}

	public static String bitmapToBase64String(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		byte[] bytes = null;
		String base64str = null;
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);  
			
			bytes = baos.toByteArray();
			
			base64str = Base64.encodeToString(bytes, Base64.DEFAULT);
		} catch (OutOfMemoryError e) {
			Logger.debug("convert bitmap failure : %s",
					e.toString());
			
			base64str = null;
		}
		
		return base64str;
	}

    public static Bitmap bitmapFromBase64String(String base64String) {
        if (TextUtils.isEmpty(base64String)) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
            if (bytes != null && bytes.length > 0) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }  catch (OutOfMemoryError e) {
            Logger.warnning("decode bitmap from Base64 string failure: %s",
                    e.toString());

            bitmap = null;
        }

        return bitmap;
    }

    public static Bitmap compositeDrawableWithMask(
			Bitmap rgbBitmap, Bitmap alphaBitmap) {
		if (rgbBitmap == null) {
			return null;
		}
		
		if (alphaBitmap == null) {
			return rgbBitmap;
		}
		
		final int rgbw = rgbBitmap.getWidth();
		final int rgbh = rgbBitmap.getHeight();
		final int alphaw = alphaBitmap.getWidth();
		final int alphah = alphaBitmap.getHeight();
		if (rgbw != alphaw
				|| rgbh != alphah) {
			Logger.warnning("mismatch bitmaps, rgb[%-3dx%-3d], alpha[%-3dx%-3d]",
					rgbw, rgbh, alphaw, alphah);
			
			return rgbBitmap;
		}

		Bitmap destBitmap = Bitmap.createBitmap(rgbw, rgbh,
				Bitmap.Config.ARGB_8888);

		int[] pixels = new int[rgbw];
		int[] alpha = new int[rgbw];
		for (int y = 0; y < rgbh; y++) {
			rgbBitmap.getPixels(pixels, 0, rgbw, 0, y, rgbw, 1);
			alphaBitmap.getPixels(alpha, 0, rgbw, 0, y, rgbw, 1);

			for (int x = 0; x < rgbw; x++) {
				// Replace the alpha channel with the r value from the bitmap.
				pixels[x] = (pixels[x] & 0x00FFFFFF)
						| ((alpha[x] << 8) & 0xFF000000);
			}
			
			destBitmap.setPixels(pixels, 0, rgbw, 0, y, rgbw, 1);
		}

		return destBitmap;
	}

	public static Bitmap compositeBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
		return compositeBitmaps(false, bitmap1, bitmap2);
	}
	
	public static Bitmap compositeBitmaps(boolean scale, Bitmap bitmap1, Bitmap bitmap2) {
		return compositeBitmaps(scale, new Bitmap[] { bitmap1, bitmap2 });
	}
	
	public static Bitmap compositeBitmaps(Bitmap... bitmaps) {
		return compositeBitmaps(false, bitmaps);
	}
	
	public static Bitmap compositeBitmaps(boolean scale, Bitmap... bitmaps) {
		if (bitmaps == null) {
			return null;
		}
		
		final int N = bitmaps.length;
		if (N == 1) {
			return bitmaps[0];
		}
		
		if (bitmaps[0] == null) {
			return bitmaps[0];
		}
		
		int bw = bitmaps[0].getWidth();
		int bh = bitmaps[0].getHeight();
		final Config config = bitmaps[0].getConfig();
		
		if (!scale) {
			int[] dimension = findMaxDimension(bitmaps);
			if (dimension != null) {
				bw = dimension[0];
				bh = dimension[1];
			}
		}
		
/*		Logger.debug("target composite dimen: %d x %d",
				bw, bh);
*/
		Bitmap finalBitmap = null;
		try {
			finalBitmap = Bitmap.createBitmap(bw, bh, config);
		} catch (OutOfMemoryError e) {
			Logger.warnning("could not create composite bitmap: %s",
					e.toString());
			
			finalBitmap = null;
		}
		
		if (finalBitmap == null) {
			return bitmaps[0];
		}
		
		Canvas canvas = new Canvas(finalBitmap);
		
		Bitmap currbmp = null;
		Rect src = new Rect();
		Rect dst = new Rect();
		int xoff = 0;
		int yoff = 0;
		for (int i = 0; i < N; i++) {
			currbmp = bitmaps[i];
			if (currbmp == null) {
				continue;
			}
			
			xoff = 0;
			yoff = 0;
			
			if (currbmp.getWidth() != bw
						|| currbmp.getHeight() != bh) {
				if (scale) {
					currbmp = BitmapUtils.scaleBitmap(currbmp, bw, bh);
				} else {
					xoff = (bw - currbmp.getWidth()) / 2;
					yoff = (bh - currbmp.getHeight()) / 2;
				}
			}
			
			src.set(0, 0, currbmp.getWidth(), currbmp.getHeight());
			dst.set(xoff, yoff, 
					xoff + currbmp.getWidth(), 
					yoff + currbmp.getHeight());

			canvas.drawBitmap(currbmp, src, dst, null);
		}
		
		return finalBitmap;
	}
	
	public static Bitmap loadAssetBitmap(Context context, String assetFile) {
		AssetManager assetManager = context.getAssets();
		if (assetManager == null) {
			return null;
		}
		
		if (TextUtils.isEmpty(assetFile)) {
			return null;
		}
		
	    InputStream istream = null;
	    Bitmap bitmap = null;
	    try {
	        istream = assetManager.open(assetFile);
	        
	        if (istream != null) {
	        	bitmap = BitmapFactory.decodeStream(istream);
	        }
	    } catch (OutOfMemoryError e) {
	    	Logger.warnning("could not decode asset bitmap: %s",
	    			assetFile,
	    			e.toString());
	    	
	        bitmap = null;
		} catch (IOException e) {
	    	Logger.warnning("could not decode asset bitmap: %s",
	    			assetFile,
	    			e.toString());
	    	
	        bitmap = null;
	    } finally {
	    	try {
		    	if (istream != null) {
		    		istream.close();
		    	}
	    	} catch (IOException e) {
			}
	    }
	    
	    return bitmap;
	}
	
	public static int[] findMaxDimension(Bitmap... bitmaps) {
		if (bitmaps == null) {
			return null;
		}
		
		int[] dimension = new int[] {0, 0};
		
		final int N = bitmaps.length;
		if (N == 1) {
			if (bitmaps[0] == null) {
				return dimension;
			} else {
				dimension[0] = bitmaps[0].getWidth();
				dimension[1] = bitmaps[0].getHeight();
				
				return dimension;
			}
		}
		
		Bitmap bitmap = null;
		for (int i = 0; i < N; i++) {
			bitmap = bitmaps[i];
			if (bitmap == null) {
				continue;
			}
			
			if (bitmap.getWidth() > dimension[0]) {
				dimension[0] = bitmap.getWidth();
			}
			
			if (bitmap.getHeight() > dimension[1]) {
				dimension[1] = bitmap.getHeight();
			}
		}
		
		return dimension;
	}

    public static Bitmap getRoundBitmap(Bitmap source, int radius) {
        Bitmap scaledBitmap;
        if(source.getWidth() != radius || source.getHeight() != radius) {
            scaledBitmap = scaleBitmap(source, radius * 2, radius * 2);
        } else {
            scaledBitmap = source;
        }

        Bitmap output = Bitmap.createBitmap(scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawCircle(scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2,
                scaledBitmap.getWidth() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);

        return output;
    }

}
