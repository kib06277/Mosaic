import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

/**
 * �Ϥ����u��
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
			// URL�i��
			AQuery aq = new AQuery(img);
			Bitmap bt = aq.getCachedImage(url, targetWidthPx);
			if (bt == null)
			{
				//�S���w�g�w�s���Ϥ��A����O�q�{�Ϥ�
				Bitmap presetBt = null;
				if (waitingFaceResId != 0)
				{
					presetBt = getMemoryCahceBitmap(img.getContext(),waitingFaceResId);
				}
				aq.image(url, true, true, targetWidthPx, 0, presetBt,AQuery.FADE_IN);
			}
			else
			{
				//�s�b�w�s�Ϥ��A�����ϥνw�s�Ϥ��A���ݭn����ܵ��ݩΪ��q�{�Ϥ�
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
	 * ���� URL �A ImageView �O�_�O���Ī��ACallBack ���|�T�O�Q�ե�
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
	 * �K�[�@�ӵ��ݪ� ProgressBar �浹 AQuery �B�z
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

	 * �N�|�M�� View �� key �O AQuery.TAG_URL �� TAG
	 * @param img
	 * @param clearCurImgData
	 * �O�_�]���e View �����e�M��(�u�� View �O ImageView ���ɭԦ��ġA clear ����Ϥ��N����ܥ���F��)
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
	 * �����M AQuery ���[�����Y�A�èϥΨϩw���q�{�Ϥ�����
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
	 * �M���� AQuery �����Y�A�ä����� View ����S��
	 * @param view
	 */
	public static void cancelAqueryLoadRelationOnly(View view)
	{
		if (view != null)
		{
			// ??�M���O�_�u�����ĭn���uAQuery��??�өw�A���ҥH�ΤU������k?�M��AQuery�[????�t�A
			// �O�]?��?��?��?����?�z??�A���O�}���N����X�Ҧ�AQuery����

			// �o�ӲM���O�_�u�����ĭn�ھ� AQuery ����{�өw�A���ҥH�ϥΤU������k�ӲM�� AQuery �[�����p�t��
			// �]���ڭ̬ݹL���X�̪��B�z�޿�A���O���N��A�X�Ҧ� AQuery ����
			view.setTag(AQuery.TAG_URL, null);
		}
	}

	private static void fillOrClearImageView(ImageView img, int resId)
	{
		if (img != null)
		{
			if (resId == 0)
			{
				// �p�G�S���i�H��ܪ��Ϥ��A�n�M�� ImageView ���{���ĪG
				img.setImageBitmap(null);
			}
			else
			{
				// �p�G URL �i�ΡA�åB�w�s�S���ƾڪ��ɭԤ~�|�ϥε��ݹϥ�
				img.setImageResource(resId);
			}
		}
	}

	private static SparseArray<SoftReference<Bitmap>> bitmaps = new SparseArray<SoftReference<Bitmap>>();

	/**
	 * �p�G�s�b�h������^�A�_�h�|���ո�E Resource Id Ū���A�òK�[�줺�s�A�ҥH�nĵ�V�ϥΡA�קK���ݭn���w�s
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