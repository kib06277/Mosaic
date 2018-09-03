import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

/**
 * 圖片類工具
 * 
 * @author skg
 * 
 */
public class ImageUtility
{
	public static Bitmap getCachedImage(Context context, String url,int targetWidthPx, boolean manualScale)
	{
		AQuery aq = new AQuery(context);
		if (manualScale)
		{
			targetWidthPx = (targetWidthPx * 3) / 5;
		}
		return aq.getCachedImage(url, targetWidthPx);
	}

	public static boolean loadImage(ImageView img, String url,int targetWidthPx, int waitingFaceResId, boolean manualScale)
	{
		if (manualScale)
		{
			targetWidthPx = (targetWidthPx * 3) / 5;
		}

		if (img == null)
		{
			return false;
		}

		if (url != null && url.length() > 0)
		{
			// URL可用
			AQuery aq = new AQuery(img);
			Bitmap bt = aq.getCachedImage(url, targetWidthPx);
			if (bt == null)
			{
				//沒有已經緩存的圖片，先顯是默認圖片
				Bitmap presetBt = null;
				if (waitingFaceResId != 0)
				{
					presetBt = getMemoryCahceBitmap(img.getContext(),waitingFaceResId);
				}
				aq.image(url, true, true, targetWidthPx, 0, presetBt,AQuery.FADE_IN);
			}
			else
			{
				//存在緩存圖片，直接使用緩存圖片，不需要先顯示等待或者默認圖片
				img.setImageBitmap(bt);
			}
		}
		else
		{
			fillOrClearImageView(img, waitingFaceResId);
		}
		return true;
	}

	/**
	 * 不管 URL ， ImageView 是否是有效的，CallBack 都會確保被調用
	 * @param img
	 * @param url
	 * @param targetWidthPx
	 * @param waitingFaceResId
	 * @param callback
	 */
	public static void loadImage(ImageView img, String url, int targetWidthPx,int waitingFaceResId, BitmapAjaxCallback callback,boolean manualScale)
	{
		if (manualScale)
		{
			targetWidthPx = (targetWidthPx * 3) / 5;
		}
		AQuery aq = new AQuery(img);
		aq.image(url, true, true, targetWidthPx, waitingFaceResId, callback);
	}

	/**
	 * 添加一個等待的 ProgressBar 交給 AQuery 處理
	 * @param img
	 * @param url
	 * @param targetWidthPx
	 * @param waitingFaceResId
	 * @param callback
	 * @param manualScale
	 * @param progress
	 */
	public static void loadImage(ImageView img, String url, int targetWidthPx,int waitingFaceResId, BitmapAjaxCallback callback,boolean manualScale, View progress)
	{
		if (manualScale)
		{
			targetWidthPx = (targetWidthPx * 3) / 5;
		}
		AQuery aq = new AQuery(img);
		aq = aq.progress(progress);
		aq.image(url, true, true, targetWidthPx, waitingFaceResId, callback);
	}

	public static void loadImage(ImageView img, String url, boolean memCache,boolean fileCache, int targetWidthPx, int waitingFaceResId,BitmapAjaxCallback callback, boolean manualScale)
	{
		if (manualScale)
		{
			targetWidthPx = (targetWidthPx * 3) / 5;
		}
		AQuery aq = new AQuery(img);
		aq.image(url, memCache, fileCache, targetWidthPx, waitingFaceResId,callback);
	}

	/**
	 * @see #cancelAqueryLoad(ImageView, boolean)
	 */
	public static void cancelAqueryLoad(ImageView img)
	{
		cancelAqueryLoad(img, false);
	}

	/**

	 * 將會清除 View 中 key 是 AQuery.TAG_URL 的 TAG
	 * @param img
	 * @param clearCurImgData
	 * 是否也把當前 View 的內容清除(只當 View 是 ImageView 的時候有效， clear 之後圖片將不顯示任何東西)
	 */
	public static void cancelAqueryLoad(ImageView img, boolean clearCurImgData)
	{
		if (img != null)
		{
			cancelAqueryLoadRelationOnly(img);
			if (clearCurImgData)
			{
				img.setImageDrawable(null);
			}
		}
	}

	/**
	 * 取消和 AQuery 的加載關係，並使用使定的默認圖片替換
	 * @param img
	 * @param replaceResId
	 */
	public static void cancelAqueryLoad(ImageView img, int replaceResId)
	{
		if (img != null)
		{
			cancelAqueryLoadRelationOnly(img);
			fillOrClearImageView(img, replaceResId);
		}
	}

	/**
	 * 清除跟 AQuery 的關係，並不改變 View 任何特性
	 * @param view
	 */
	public static void cancelAqueryLoadRelationOnly(View view)
	{
		if (view != null)
		{
			// ??清除是否真的有效要根据AQuery的??而定，之所以用下面的方法?清除AQuery加????系，
			// 是因?我?看?源?里的?理??，但是并不代表适合所有AQuery版本

			// 這個清除是否真的有效要根據 AQuery 的實現而定，之所以使用下面的方法來清除 AQuery 加載關聯系統
			// 因為我們看過源碼裡的處理邏輯，但是不代表適合所有 AQuery 版本
			view.setTag(AQuery.TAG_URL, null);
		}
	}

	private static void fillOrClearImageView(ImageView img, int resId)
	{
		if (img != null)
		{
			if (resId == 0)
			{
				// 如果沒有可以顯示的圖片，要清空 ImageView 的現有效果
				img.setImageBitmap(null);
			}
			else
			{
				// 如果 URL 可用，並且緩存沒有數據的時候才會使用等待圖示
				img.setImageResource(resId);
			}
		}
	}

	private static SparseArray<SoftReference<Bitmap>> bitmaps = new SparseArray<SoftReference<Bitmap>>();

	/**
	 * 如果存在則直接返回，否則會嘗試跟聚 Resource Id 讀取，並添加到內存，所以要警慎使用，避免不需要的緩存
	 * @param context
	 * @param resId
	 * @return
	 */
	private static Bitmap getMemoryCahceBitmap(Context context, int resId)
	{
		SoftReference<Bitmap> refer = bitmaps.get(resId);
		if (refer == null || refer.get() == null)
		{
			Bitmap bt = null;
			bt = BitmapFactory.decodeResource(context.getResources(), resId);
			refer = new SoftReference<Bitmap>(bt);
			bitmaps.put(resId, refer);
		}
		return refer.get();
	}

}