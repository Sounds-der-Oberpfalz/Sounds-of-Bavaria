package de.mi.soundsofbavaria;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class CardFrontFragment  extends Fragment {
	    TextView text1;  
	    ImageView square;
	
		
		 
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_card_front, container, false);
	        text1= (TextView ) view.findViewById(R.id.text1);
	        square= (ImageView ) view.findViewById(R.id.square);

	        return view;
	    }
	    

	    
	    public void setText(String text ){
	    	String[] split = text.split(" - ");
	        text1.setText(split[0]+"\n"+split[1]);
	    }
	    public void setBmp(Bitmap bmp ){
	    	square.setImageBitmap(bmp);
	    }

	
 
          
      
      	  
           	  
          
    

       
    }