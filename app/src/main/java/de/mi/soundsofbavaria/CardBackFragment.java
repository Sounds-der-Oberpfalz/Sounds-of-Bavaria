package de.mi.soundsofbavaria;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CardBackFragment extends Fragment {
	TextView text2;
	TextView lyr;
	TextView desc;
	TextView cover;
	String description;
	String lyrics;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_card_back, container,
				false);
		text2 = (TextView) view.findViewById(R.id.text2);
		lyr = (TextView) view.findViewById(R.id.lyrics);
		desc = (TextView) view.findViewById(R.id.desc);
		
		desc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTextField(description);
				lyr.setBackgroundResource(R.drawable.whiteborder);
				desc.setBackgroundResource(R.drawable.blueborder);

				desc.setTextColor(getResources().getColor(R.color.blue));
				lyr.setTextColor(getResources().getColor(R.color.white));

			}
		});
		lyr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTextField(lyrics);
				desc.setBackgroundResource(R.drawable.whiteborder);
				lyr.setBackgroundResource(R.drawable.blueborder);
				lyr.setTextColor(getResources().getColor(R.color.blue));
				desc.setTextColor(getResources().getColor(R.color.white));
			}
		});
		return view;
	}

	public void setText(String t1 ,String t2) {
		description = t1;
		lyrics = t2;
		setTextField(description);
		
	}
	

	private void setTextField(String text) {
		text2.setText(text);
	}

}