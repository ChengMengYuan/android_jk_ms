package com.grgbanking.ct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.database.PersonTableHelper;
import com.grgbanking.ct.http.FileLoadUtils;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.page.CaptureActivity;

@SuppressLint("NewApi")
public class DetailActivity2 extends Activity {

	private Context context;
	private EditText remarkEditView;
	TextView positionTextView = null;
	TextView branchNameTextView = null;
	Button commitYesButton = null;
	Button commitNoButton = null;
	
	Button connDeviceButton = null;
	Button startDeviceButton = null;
	TextView person1TextView = null;
	TextView person2TextView = null;
	ListView deviceListView;
	SimpleAdapter listItemAdapter;
	ArrayList<HashMap<String, Object>> listItem;

	private String branchCode = null;
	private String branchId = null;
	private String imageUrl = null;// 上传成功后的图片URL

	private String address;
	private double latitude;
	private double longitude;

	private boolean uploadFlag = false;
	private ProgressDialog pd = null;
	private Person person = null;

	private void showWaitDialog(String msg) {
		if (pd == null) {
			pd = new ProgressDialog(this);
		}
		pd.setCancelable(false);
		pd.setMessage(msg);
		pd.show();
	}

	private void hideWaitDialog() {
		if (pd != null) {
			pd.cancel();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail);
		context = getApplicationContext();
		remarkEditView = (EditText) findViewById(R.id.peixiangdetail_remark);
		commitNoButton = (Button) findViewById(R.id.peixiangdetail_btn_commit);
		branchNameTextView = (TextView) findViewById(R.id.detail_branch_name);
        
		connDeviceButton = (Button) findViewById(R.id.peixiangdetail_btn_lianjie);
		startDeviceButton = (Button) findViewById(R.id.peixiangdetail_btn_saomiao);
		person1TextView = (TextView) findViewById(R.id.peixiangdetail_tv_name);
		person2TextView = (TextView) findViewById(R.id.peixiangdetail_tv_num);
		deviceListView = (ListView) findViewById(R.id.ListView_boxs);
		
		// 生成动态数组，加入数据
		listItem = new ArrayList<HashMap<String, Object>>();
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this,listItem,R.layout.main_list_item , new String[] { "list_img", "list_title", "list_position","list_worktime" } , new int[] { R.id.list_img, R.id.list_title, R.id.list_position ,R.id.list_worktime});
		// 添加并且显示
		deviceListView.setAdapter(listItemAdapter);
		
//        remarkEditView.setText(count + ", " + world);
		initLocation(context, this);

		showWaitDialog("正在加载中，请稍后...");

		person = PersonTableHelper.queryEntity(this);

		// 加载网点信息
		branchCode = getIntent().getStringExtra(
				CaptureActivity.CAPTURE_INTENT_VALUE);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("branchCode", branchCode));
//		new HttpPostUtils(Constants.URL_GET_BRANCH, params,
//				new UICallBackDao() {
//					@Override
//					public void callBack(ResultInfo resultInfo) {
//						if (resultInfo == null || "2".equals(resultInfo.getCode())) {
//							
//							new AlertDialog.Builder(DetailActivity.this)
//									.setTitle("错误提示")
//									.setMessage("系统或网络出错，请重试！"+resultInfo.getMessage())
//									.setPositiveButton("确定",
//											new DialogInterface.OnClickListener() {// 设置确定按钮
//												@Override
//												// 处理确定按钮点击事件
//												public void onClick(DialogInterface dialog,
//														int which) {
//													backListPage();
//												}
//											}).show();
//							return;
//						}
//						JSONObject jsonObject = resultInfo.getJsonObject();
//						try {
//							branchNameTextView.setText(jsonObject
//									.getString("branchName"));
//							branchAddressTextView.setText(jsonObject
//									.getString("branchAddress"));
//							branchId = jsonObject.getString("branchId");
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						hideWaitDialog();
//					}
//				}).execute();

		hideWaitDialog();
		// 点击返回按钮操作内容
		findViewById(R.id.detail_btn_back).setOnClickListener(click);

		// 点击提交按钮操作内容
//		commitYesButton.setOnClickListener(click);
		commitNoButton.setOnClickListener(click);
		connDeviceButton.setOnClickListener(click);
		startDeviceButton.setOnClickListener(click);

		// 点击添加照片按钮操作内容
//		findViewById(R.id.add_photo).setOnClickListener(click);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	OnClickListener click = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.detail_btn_back:
				backListPage();
				break;
//			case R.id.detail_btn_commit_y:
//				doCommit("Y");
//				break;
			case R.id.peixiangdetail_btn_commit:
//				if (uploadFlag) {
//					doCommit("N");
//				} else {
//					Toast.makeText(DetailActivity.this, "图片正在上传中，请稍等。", 5000)
//							.show();
//				}
				// 得到跳转到该Activity的Intent对象
		        Intent intent = getIntent();
		        Bundle bundle = intent.getBundleExtra("bundle");
		        int count = bundle.getInt("count");
		        if (DataCach.taskMap.get(count+ "") != null) {
		        	HashMap<String, Object> map = DataCach.taskMap.get(count+ "");
		        	map.put("list_img", R.drawable.task_1);// 图像资源的ID
		        	map.put("list_worktime","已完成");
		        }
		        backListPage();
				break;
//			case R.id.add_photo:
//				doAddPhoto();
//				break;
				
			case R.id.peixiangdetail_btn_lianjie:
				//连接设备
				person1TextView.setText("陈芳乐");
				break;
			case R.id.peixiangdetail_btn_saomiao:
				//启动设备
				person2TextView.setText("刘杨");
				break;
			default:
				break;
			}
		}
	};

	void backListPage() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	void doCommit(String flag) {
		
		if(address==null||"".equals(address)){
			Toast.makeText(DetailActivity2.this, "错误提示：无法获取您当前的地理位置，请返回重新扫描二维码。", 5000)
			.show();
			return;
		}
		
		showWaitDialog("正在处理中...");
		String remark = remarkEditView.getText().toString();
		String status = flag;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", person.getUser_id()));
		params.add(new BasicNameValuePair("name", person.getUser_name()));

		params.add(new BasicNameValuePair("branchId", branchId));
		params.add(new BasicNameValuePair("remark", remark));
		params.add(new BasicNameValuePair("status", status));
		params.add(new BasicNameValuePair("imageUrl", imageUrl));
		params.add(new BasicNameValuePair("longitude", longitude + ""));
		params.add(new BasicNameValuePair("latitude", latitude + ""));
		params.add(new BasicNameValuePair("address", address));
		new HttpPostUtils(Constants.URL_SAVE_TASK, params, new UICallBackDao() {
			@Override
			public void callBack(ResultInfo resultInfo) {
				hideWaitDialog();
				
				if("1".equals(resultInfo.getCode())){
					new AlertDialog.Builder(DetailActivity2.this)
					.setTitle("消息")
					.setMessage("提交成功！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {// 设置确定按钮
								@Override
								// 处理确定按钮点击事件
								public void onClick(DialogInterface dialog,
										int which) {
									backListPage();
								}
							}).show();
				}else{
					Toast.makeText(DetailActivity2.this, resultInfo.getMessage(), 5*1000).show();
					
				}
				
				
			}
		}).execute();
	}

	void doAddPhoto() {
		final CharSequence[] items = { "相册", "拍照" };
		AlertDialog dlg = new AlertDialog.Builder(DetailActivity2.this)
				.setTitle("选择照片")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// 这里item是根据选择的方式， 在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
						if (which == 1) {
							takePhoto();
						} else {
							pickPhoto();
						}
					}
				}).create();
		dlg.show();
	}

	private static final int IMAGE_REQUEST_CODE = 0; // 选择本地图片
	private static final int CAMERA_REQUEST_CODE = 1; // 拍照
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/**
	 * 拍照
	 */
	private void takePhoto() {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可以用，可用进行存�?
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
		}
		startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
	}

	/**
	 * 选择本地图片
	 */
	private void pickPhoto() {
		Intent intentFromGallery = new Intent();
		intentFromGallery.setType("image/*"); // 设置文件类型
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
	}

	public  String uri2filePath(Uri uri) {
		String path = "";
		if (DocumentsContract.isDocumentUri(this, uri)) {
			String wholeID = DocumentsContract.getDocumentId(uri);
			String id = wholeID.split(":")[1];
			String[] column = { MediaStore.Images.Media.DATA };
			String sel = MediaStore.Images.Media._ID + "=?";
			Cursor cursor = this.getContentResolver().query(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
			new String[] { id }, null);
			int columnIndex = cursor.getColumnIndex(column[0]);
			if (cursor.moveToFirst()) {
				path = cursor.getString(columnIndex);
			}
			cursor.close();
		} else {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = this.getContentResolver().query(uri,
			projection, null, null, null);
			int column_index = cursor
			.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
		}
		return path;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CAMERA_REQUEST_CODE:
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					Bitmap bitmap = compressImage(getSmallBitmap(Environment
							.getExternalStorageDirectory()
							+ "/"
							+ IMAGE_FILE_NAME));

//					addPhotoImageView.setImageBitmap(bitmap);
//					uploadStatusTextView.setText("图片上传中...");

					new FileLoadUtils(Constants.URL_FILE_UPLOAD, new File(
							Environment.getExternalStorageDirectory()
									+ "/faceImage1.jpg"), new UICallBackDao() {
						@Override
						public void callBack(ResultInfo resultInfo) {
							if (resultInfo != null
									&& "200".equals(resultInfo.getCode())) {
								Toast.makeText(DetailActivity2.this, "上传成功",
										5000).show();
//								uploadStatusTextView.setText("上传成功。");
//								imageUrl = resultInfo.getMessage();
								uploadFlag = true;
							}
						}
					}).execute();
				} else {
					Toast.makeText(this, getString(R.string.sdcard_unfound),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case IMAGE_REQUEST_CODE:
				try {
					String path = uri2filePath(data.getData());
					Bitmap bitmap = compressImage(getSmallBitmap(path));
//					addPhotoImageView.setImageBitmap(bitmap);
//					uploadStatusTextView.setText("图片上传中...");
					new FileLoadUtils(Constants.URL_FILE_UPLOAD,
							new File(path), new UICallBackDao() {

								@Override
								public void callBack(ResultInfo resultInfo) {
									if (resultInfo != null
											&& "200".equals(resultInfo
													.getCode())) {
										Toast.makeText(DetailActivity2.this,
												"上传成功", 5000).show();
//										uploadStatusTextView.setText("上传成功。");
										imageUrl = resultInfo.getMessage();
										uploadFlag = true;
									}
								}
							}).execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/**
	 * 图片压缩方法实现
	 * 
	 * @param srcPath
	 * @return
	 */
	private Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		int hh = 800;// 这里设置高度为800f
		int ww = 480;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据高度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 质量压缩
	 * 
	 * @param image
	 * @return
	 */
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length * 3 / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		try {
			FileOutputStream out = new FileOutputStream(
					Environment.getExternalStorageDirectory()
							+ "/faceImage1.jpg");
			bitmap.compress(Bitmap.CompressFormat.PNG, 40, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public void initLocation(final Context context,
			final DetailActivity2 detailActivity) {
		LocationClient locationClient = new LocationClient(context);
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 是否打开GPS
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setScanSpan(0);// 设置定时定位的时间间隔。单位毫秒
		option.setAddrType("all");
		option.setCoorType("bd09ll");
		locationClient.setLocOption(option);
		// 注册位置监听器
		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
//				gpsPositionTextView = (TextView) detailActivity
//						.findViewById(R.id.gps_position);
//				gpsPositionTextView.setText("我的位置：" + location.getAddrStr());
				address = location.getAddrStr();
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		});
		locationClient.start();
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @param imagesrc
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
}
