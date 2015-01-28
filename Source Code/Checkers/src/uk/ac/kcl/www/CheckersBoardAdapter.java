package uk.ac.kcl.www;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Adapter;


public class CheckersBoardAdapter extends BaseAdapter{
    private Context mContext;
		private int isEven;

    public CheckersBoardAdapter(Context c) {
        mContext = c;
				isEven = 1;
				// Let's assume that 'getView' is the loop body ;)
				// position % 2 == value, and value will change back and forth from 0 to 1, vice-versa, every 8 times
				// and... (position % 8) >= 0 && (position % 8) < 8 && position % 2 == value)
				
    }

    public int getCount() {
        // Depending on the return value of this method, 'getView()' will be called that many times e.g.
				// If we are return 64, then getView() will be called 64 times i.e. 64 views will initially be created ;)
				return 64;
				//return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }
		
		// Should return the rowid of the item
    public long getItemId(int position) {
        return 0;
    }

		// override getView() to return the type of view you want for each item. In this case, I want to return an ImageView
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        
				View gridviewSquare = convertView;
        
				if(convertView == null){	// If the 'convertView' is not recycled, perform the following...
						// Grabs the LayoutInflater, and stores it in a readily available variable
						LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						// Inflates the 'View' with the layout of the given XML.
						gridviewSquare = inflater.inflate(R.layout.gridview_item, parent, false);
						// Store the ImageView specified in the parsed XML layout, into a runtime ImageView for further modification.
						ImageView imageSquare = (ImageView) gridviewSquare.findViewById(R.id.image_square);
						
						if(position % 8 >= 0 && position % 8 < 8 && position % 2 == isEven)
						{
								// This method used causes latency so, use the 'setImageDrawable()' method instead.
								imageSquare.setImageResource(R.drawable.light_brown_piece);
								
								if(position % 7 == 0)
								{
										isEven = 0;
								}
								
						}
						
						
						
						/*if(position % 2 == 0){ 
								// This method used causes latency so, use the 'setImageDrawable()' method instead.
								imageSquare.setImageResource(R.drawable.dark_brown_piece);
						}else{
								// This method used causes latency so, use the 'setImageDrawable()' method instead.
								imageSquare.setImageResource(R.drawable.light_brown_piece);
						}*/
						
						
				}
				
				/*if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
						// As the parent ViewGroup of these ImageViews is a 'GridView', the LayoutParams will be of that class.
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;*/
				
				return gridviewSquare;
    }

    // references to our images
		
    private Integer[] mThumbIds = {
        R.drawable.dark_brown_piece, R.drawable.light_brown_piece, R.drawable.dark_brown_piece, R.drawable.light_brown_piece 
    };
}