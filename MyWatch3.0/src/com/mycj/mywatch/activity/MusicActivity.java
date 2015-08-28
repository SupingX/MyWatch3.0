package com.mycj.mywatch.activity;

import java.util.ArrayList;
import java.util.List;

import com.mycj.mywatch.BaseActivity;
import com.mycj.mywatch.R;
import com.mycj.mywatch.bean.Music;
import com.mycj.mywatch.business.MusicLoader;
import com.mycj.mywatch.service.MusicService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MusicActivity extends BaseActivity implements OnClickListener {

	private int[] ids;// 保存音乐ID临时数组
	private String[] artists;
	private String[] titles;
	private Cursor cursor;
	private MusicListAdapter2 musicAdapter2;
	private List<Music> list;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context,  Intent intent) {
			String action = intent.getAction();
			if (action.equals(MusicService.MUSIC_PLAYING)) {
				runOnUiThread(new Runnable() {
					public void run() {
						Log.e("", "MUSIC_PLAYING======");
						setMusicButtonStartOrStop(musicSerivce.isPlaying());
					}
				});
			} else if (action.equals(MusicService.MUSIC_STOP)) {
				runOnUiThread(new Runnable() {
					public void run() {
						Log.e("", "MUSIC_STOP======");
						setMusicButtonStartOrStop(musicSerivce.isPlaying());
					}
				});
			} else if (action.equals(MusicService.MUSIC_PAUSE)) {
				runOnUiThread(new Runnable() {
					public void run() {
						Log.e("", "MUSIC_PAUSE=========");
						setMusicButtonStartOrStop(musicSerivce.isPlaying());
					}
				});
			}else if (action.equals(MusicService.MUSIC_COMPLETION)) {
				int pos = intent.getExtras().getInt(MusicService.MUSIC_EXTRA_PLAY_POSITION);
				
				mHandler.postDelayed( new Runnable() {
					public void run() {
						Log.e("", " wancheng l ");
						setCurrentMusicBackground(musicSerivce.getPlayingPosition());
					}
				},1000);
			}
		}
	};
	
	private Handler mHandler = new Handler(){
		
	};
	
	/**
	 * 定义查找音乐信息数组，1.标题 2.音乐时间 3.艺术家 4.音乐id 5.显示名字 6.数据
	 */
	private String[] musicInfo = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID

	};
	private MusicService musicSerivce;
	private ImageView imgPreious;
	private ImageView imgNext;
	private ImageView imgStartOrStop;
	private RelativeLayout rlMore;
	private ListView listViewMusic;


	private void initMusic() {
		MusicLoader.instance(getContentResolver());
		cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicInfo, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();
		int size = cursor.getCount();
		ids = new int[size];
		artists = new String[size];
		titles = new String[size];
		for (int i = 0; i < size; i++) {
			ids[i] = cursor.getInt(3);
			artists[i] = cursor.getString(2);
			titles[i] = cursor.getString(0);
			cursor.moveToNext();
		}

		list = MusicLoader.getMusicList();
		musicSerivce.setList(list);
		Log.d("", "_____________________________list : " + list);
		musicAdapter2 = new MusicListAdapter2(this, list);
		listViewMusic.setAdapter(musicAdapter2);
//		musicAdapter2.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		musicSerivce = getMusicService();
		initViews();
		setListener();
		registerReceiver(mReceiver, MusicService.getIntentFilter());
	}
	
	
	@Override
	protected void onPause() {
		 musicSerivce.stopForeground(true);
//		musicSerivce.setCurrentPosition();
		// musicSerivce.pause();
//		unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		musicSerivce.setCurrentPosition();
		super.onConfigurationChanged(newConfig);
	}
	@Override
	protected void onDestroy() {
//		list.clear();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// musicSerivce.seekto();//继续上次播放
		// musicSerivce.play();
		super.onResume();
		//这里延迟是为了{ listView 加载需要时间 ，要不然获取子View可能为null。 期待更好的解决方法}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setCurrentMusicBackground(musicSerivce.getPlayingPosition());
			}
		}, 500);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_music_next:
			musicSerivce.playDown();
			setCurrentMusicBackground(musicSerivce.getPlayingPosition());
			break;
		case R.id.img_music_start_or_stop:
			
			Log.e("musicSerivce.isPlaying()", "musicSerivce.isPlaying() :" +musicSerivce.isPlaying());
			if (musicSerivce.isPlaying()) {
				musicSerivce.stop();
			} else {
				if (musicSerivce.getPlayingPosition()==-1) {
					musicSerivce.setPlayingPosition(0);
				}
				musicSerivce.playByCurrentPlayingPosition();
				setCurrentMusicBackground(musicSerivce.getPlayingPosition());
			}
		
			break;
		case R.id.img_music_preious:
			musicSerivce.playUp();
			setCurrentMusicBackground(musicSerivce.getPlayingPosition());
			break;
		case R.id.rl_more:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void initViews() {
		imgPreious = (ImageView) findViewById(R.id.img_music_preious);
		imgNext = (ImageView) findViewById(R.id.img_music_next);
		imgStartOrStop = (ImageView) findViewById(R.id.img_music_start_or_stop);
		rlMore = (RelativeLayout) findViewById(R.id.rl_more);
		listViewMusic = (ListView) findViewById(R.id.lv_msc);
//		initMusic();
		list =musicSerivce.getMusicList();
		musicAdapter2 = new MusicListAdapter2(this, list);
		listViewMusic.setAdapter(musicAdapter2);
	}

	@Override
	public void setListener() {
		imgPreious.setOnClickListener(this);
		imgNext.setOnClickListener(this);
		imgStartOrStop.setOnClickListener(this);
		rlMore.setOnClickListener(this);
		listViewMusic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Music music = list.get(position);
				long musicID = music.getId();
				Uri uri = MusicLoader.getMusicUriById(musicID);
				Log.d("", "uri : " + uri);
				musicSerivce.play(uri);
				musicSerivce.setPlayingPosition(position);
				setCurrentMusicBackground(position);
				setMusicButtonStartOrStop(musicSerivce.isPlaying());
			}
		});
	}
	
	private void setMusicButtonStartOrStop(boolean start){
		if (start) {
			imgStartOrStop.setImageResource(R.drawable.ic_music_stop);
		}else{
			imgStartOrStop.setImageResource(R.drawable.ic_music_play);
		}
	}
	
	
	/**
	 * 设置选中的音乐背景;
	 */
	private void setCurrentMusicBackground(int pos) {
		Log.v("", "当前播放音乐pos :" + pos);
		if (pos>-1) {
			View view = listViewMusic.getChildAt(pos-listViewMusic.getFirstVisiblePosition());
			Log.v("", "当前view :" + view);
			clearBgColor();
			view.setBackgroundColor(getResources().getColor(R.color.grey_light));
		}
	}

	private void clearBgColor() {
		Log.v("", "listViewMusic.getCount() :" + listViewMusic.getCount());
		Log.v("", "listViewMusic.getChildCount() :" + listViewMusic.getChildCount());
		int size = listViewMusic.getCount();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				View view = listViewMusic.getChildAt(i);
				if (view != null) {
					view.setBackgroundColor(getResources().getColor(R.color.white));
				}
			}
		}
	}

	private class MusicListAdapter2 extends BaseAdapter {
		private Context myContext;
		private List<Music> list = new ArrayList<>();

		public MusicListAdapter2(Context context, List<Music> list) {
			myContext = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(myContext).inflate(R.layout.item_music, parent, false);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_music_name);
				holder.tvSinger = (TextView) convertView.findViewById(R.id.tv_music_singer);
				holder.imgView = (ImageView) convertView.findViewById(R.id.img_music);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Music music = list.get(position);
			holder.tvSinger.setText(music.getArtist());
			holder.tvTitle.setText(music.getTitle());
			holder.imgView.setImageBitmap(MusicLoader.getArt(myContext, music.getId(), music.getAlbumId(), true));
			return convertView;
		}

		class ViewHolder {
			public TextView tvTitle, tvSinger;
			public ImageView imgView;
		}
	}
}
