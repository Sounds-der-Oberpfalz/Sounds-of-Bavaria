package de.mi.soundsofbavaria;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

class Mp3Filter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".mp3"));
	}
}

class JpgFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".jpg"));
	}
}

class PngFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".png"));
	}
}

class TxtFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".txt"));
	}
}

public class MainActivity extends Activity implements
		OnBackStackChangedListener {
	private Handler mHandler = new Handler();

	private TextView track;
	private TextView time;
	private boolean running = true;
	private int duration = 0;
	private SeekBar bar;

	private ImageView next;
	private ImageView back;
	private ImageView volumeIcon;
	private ImageView minus;
	private ImageView plus;
	private ImageView stop;
	private FrameLayout container;
	private ImageView replay;
	private static final String MEDIA_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/Sounds Of Bavaria/";
	private List<String> songs = new ArrayList<String>();
	private static List<String> covers = new ArrayList<String>();
	private List<String> sleeves = new ArrayList<String>();
	private List<String> texts = new ArrayList<String>();
	private MediaPlayer mp = new MediaPlayer();
	private static int currentPosition = -1;

	private float volume = 0.5f;

	private CardFrontFragment CF;
	private CardBackFragment CB;

	private LinearLayout middle;
	private boolean paused = false;

	private boolean mShowingBack = false;

	private int timer = 0;
	private int timeout = 600;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// appendLog("START");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		if (savedInstanceState == null) {

			CF = new CardFrontFragment();

			getFragmentManager().beginTransaction()

			.add(R.id.container, CF)

			.commit();

		} else {
			mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
		}

		getFragmentManager().addOnBackStackChangedListener(this);

		plus = (ImageView) findViewById(R.id.plus);
		minus = (ImageView) findViewById(R.id.minus);
		volumeIcon = (ImageView) findViewById(R.id.volume);
		next = (ImageView) findViewById(R.id.next);
		back = (ImageView) findViewById(R.id.back);
		track = (TextView) findViewById(R.id.track);
		time = (TextView) findViewById(R.id.time);
		stop = (ImageView) findViewById(R.id.stop);
		replay = (ImageView) findViewById(R.id.replay);
		container = (FrameLayout) findViewById(R.id.container);
		middle = (LinearLayout) findViewById(R.id.middle);

		bar = (SeekBar) findViewById(R.id.progress_bar);
		setVolumeIcon();
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main);
		
		
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					mp.seekTo(progress);
					// updateTime();
				}
			}

		});
		mainLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				timer = 0;

			}
		});

		middle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				flipCard();

			}
		});

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// appendLog("next");

				nextSong();
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// appendLog("back");

				previousSong();
			}
		});

		replay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// appendLog("replay");
				playSong(MEDIA_PATH + songs.get(currentPosition), false);

			}
		});
		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// appendLog("stop");
				if (!paused) {
					pauseSong();
				} else {
					resumeSong();
				}
			}
		});
		plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				volume = volume + 0.1f;
				if (volume > 1f) {
					volume = 1f;
				}
				setVolume();
			}
		});

		minus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				volume = volume - 0.1f;
				if (volume < 0f) {
					volume = 0f;
				}
				setVolume();
			}
		});
		if(songs!=null || !songs.isEmpty()) updateSongList();

	}

	//fetching and sorting filenames
	public void updateSongList() {
		try {
			File home = new File(MEDIA_PATH);
			if (home.listFiles(new Mp3Filter()).length > 0) {
				for (File file : home.listFiles(new Mp3Filter())) {
					songs.add(file.getName());
				}
				for (File file : home.listFiles(new JpgFilter())) {
					covers.add(file.getName());
				}
				for (File file : home.listFiles(new PngFilter())) {
					sleeves.add(file.getName());
				}
				for (File file : home.listFiles(new TxtFilter())) {
					texts.add(file.getName());
				}
			}
			Collections.sort(songs);
			Collections.sort(covers);
			Collections.sort(sleeves);
			Collections.sort(texts);

		} catch (Exception e) {
			//in case there is no data in sd card
			next.setClickable(false);
			back.setClickable(false);
			stop.setClickable(false);
			replay.setClickable(false);
			container.setClickable(false);
			ErrorFragment EF = new ErrorFragment();
			EF.show(getFragmentManager(),  this.getString(R.string.missing));
		}
	}
	//create option menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, Menu.FIRST, this.getString(R.string.restart));
		menu.add(1, 2, Menu.FIRST + 1, this.getString(R.string.close));
		menu.add(1, 3, Menu.FIRST + 2, this.getString(R.string.imp));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			PendingIntent intent = PendingIntent.getActivity(
					this.getBaseContext(), 0, new Intent(getIntent()),
					getIntent().getFlags());
			AlarmManager manager = (AlarmManager) this
					.getSystemService(Context.ALARM_SERVICE);
			manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
					intent);
			System.exit(2);
			return true;
		case 2:
			PendingIntent intent2 = PendingIntent.getActivity(
					this.getBaseContext(), 0, new Intent(getIntent()),
					getIntent().getFlags());
			AlarmManager manager2 = (AlarmManager) this
					.getSystemService(Context.ALARM_SERVICE);
			manager2.set(AlarmManager.RTC, 0, intent2);
			System.exit(2);
			return true;

		case 3:
			ImpressumFragment IF = new ImpressumFragment();
			IF.show(getFragmentManager(),  this.getString(R.string.imp));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//playing the actual music
	private void playSong(String songPath, boolean changeCover) {
		try {
			track.setText(currentPosition + 1 + "/" + songs.size());
			mp.reset();
			mp.setDataSource(songPath);
			mp.prepare();
			mp.start();
			setVolume();
			duration = mp.getDuration();
			bar.removeCallbacks(onEverySecond);
			bar.setMax(duration);
			bar.postDelayed(onEverySecond, 1000);

			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer arg0) {
					nextSong();
				}
			});
			if (changeCover) {
				setCover();
			}
		} catch (IOException e) {
			Log.v(getString(R.string.app_name), e.getMessage());
		}
	}
	//pausing playback and changing pause icon
	private void pauseSong() {
		stop.setBackgroundResource(R.drawable.playb);
		paused = true;
		mp.pause();
		// bar.removeCallbacks(onEverySecond);
	}
	//resuming playback and changing pause icon
	private void resumeSong() {
		stop.setBackgroundResource(R.drawable.pauseb);
		mp.start();
		paused = false;
		// bar.postDelayed(onEverySecond, 1000);
	}
	//adjusting volume
	private void setVolume() {

		mp.setVolume(volume, volume);
		setVolumeIcon();

	}
	//changing the volume icon to according to volume value
	private void setVolumeIcon() {
		int v = (Math.round(volume * 10f));
		switch (v) {
		case 0:
			volumeIcon.setBackgroundResource(R.drawable.volume0);
			break;
		case 1:
			volumeIcon.setBackgroundResource(R.drawable.volume1);
			break;
		case 2:
			volumeIcon.setBackgroundResource(R.drawable.volume2);
			break;
		case 3:
			volumeIcon.setBackgroundResource(R.drawable.volume3);
			break;
		case 4:
			volumeIcon.setBackgroundResource(R.drawable.volume4);
			break;
		case 5:
			volumeIcon.setBackgroundResource(R.drawable.volume5);
			break;
		case 6:
			volumeIcon.setBackgroundResource(R.drawable.volume6);
			break;
		case 7:
			volumeIcon.setBackgroundResource(R.drawable.volume7);
			break;
		case 8:
			volumeIcon.setBackgroundResource(R.drawable.volume8);
			break;
		case 9:
			volumeIcon.setBackgroundResource(R.drawable.volume9);
			break;
		case 10:
			volumeIcon.setBackgroundResource(R.drawable.volume10);
			break;
		}
	}
	//update the A-Side
	private void setCover() {
		CF.setText(getName());
		CF.setBmp(getMedia());
	}
	//update the B-Side
	private void setSleeve() {
		try {
			//split description and lyrics
			String[] parts =getText().split("LYRICS\n");
			String[] results = new String[2];
			results[0]=parts[0];
			//check if lyrics are available
			if (parts.length==1) results[1]= this.getString(R.string.noLyrics);
			else results[1]= parts[1];
			
			CB.setText(results[0],results[1]);
		} catch (Exception e) {
		}
	}
	//decode cover image
	public Bitmap getMedia() {
		return BitmapFactory.decodeFile(MEDIA_PATH
				+ covers.get(currentPosition));

	}
	//retrieve song title from filename
	public String getName() {
		if (currentPosition >= 0) {
			String s = covers.get(currentPosition);

			s = s.substring(0, s.length() - 4);

			return s;
		} else

			return "XXX";
	}
	//read the textfile
	public String getText() {
		File file = new File(MEDIA_PATH + texts.get(currentPosition));

		StringBuilder text = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {

				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {

		}
		return text.toString();
	

	}

	//timer event
	private Runnable onEverySecond = new Runnable() {
		@Override
		public void run() {
			timer++;
			//check if timeout has been exceeded
			if (timer > timeout) {
				volume = 0.5f;
				setVolume();
				resumeSong();
				timer = 0;

			}
			// Log.v("bav", "timer:" + timer);
			//update progress bar and time code
			if (true == running) {
				if (bar != null) {
					bar.setProgress(mp.getCurrentPosition());
					int curTime = mp.getCurrentPosition() / 1000;
					int durTime = duration / 1000;
					int curSecs = curTime % 60;
					int curMin = curTime / 60;
					int durSecs = durTime % 60;
					int durMin = durTime / 60;
					String timeString = curMin + ":";
					if (curSecs < 10)
						timeString = timeString + "0";
					timeString = timeString + curSecs + "/" + durMin + ":";
					if (durSecs < 10)
						timeString = timeString + "0";
					timeString = timeString + durSecs;
					time.setText(timeString);
				}

				// if (mp.isPlaying()) {
				bar.postDelayed(onEverySecond, 1000);
				// updateTime();
				// }
			}
		}
	};
	//iterate to next song
	public void nextSong() {
		paused = false;
		stop.setBackgroundResource(R.drawable.pauseb);
		if (mShowingBack)
			flipCard();
		if (++currentPosition >= songs.size()) {
			currentPosition = 0;
		}
		playSong(MEDIA_PATH + songs.get(currentPosition), true);

	}
	//iterate to previous song and
	private void previousSong() {
		if (mShowingBack)
			flipCard();
		if (--currentPosition < 0) {
			currentPosition = songs.size() - 1;
		}
		playSong(MEDIA_PATH + songs.get(currentPosition), true);

	}
	//deprecated for usability test
	/*
	 * public void appendLog(String text) { File logFile = new
	 * File("sdcard/log.txt"); if (!logFile.exists()) { try {
	 * logFile.createNewFile(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } } try { / BufferedWriter buf = new
	 * BufferedWriter(new FileWriter(logFile, true)); Time now = new Time();
	 * now.minute+ now.setToNow();
	 * buf.append(now.year+"."+now.month+"."+now.MONTH_DAY
	 * +" "+now.hour+":"+now.minute+":"+now.second+" "+text); buf.newLine();
	 * buf.close(); } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
	//flip A and B Side
	void flipCard() {
		if (mShowingBack) {

			getFragmentManager().popBackStack();
			return;
		}

		mShowingBack = true;

		CB = new CardBackFragment();

		getFragmentManager()
				.beginTransaction()

				.setCustomAnimations(R.animator.card_flip_right_in,
						R.animator.card_flip_right_out,
						R.animator.card_flip_left_in,
						R.animator.card_flip_left_out)

				.add(R.id.container, CB).hide(CF)
				// .replace(R.id.container, CB)

				.addToBackStack(null)

				.commit();

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
				setSleeve();
			}
		});
	}

	@Override
	public void onBackStackChanged() {
		mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);

		invalidateOptionsMenu();
	}
}
