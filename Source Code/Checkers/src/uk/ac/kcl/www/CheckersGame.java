package uk.ac.kcl.www;

import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

// These are all the GridView dependencies

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Adapter;
import android.widget.Toast;
import android.widget.ImageView;

import android.view.ViewGroup.*;
import android.view.Gravity;
import android.view.LayoutInflater;


// I forgot to declare the class as public, and in result, it could not load this activity from the main menu
public class CheckersGame extends Activity{
	
	public GridLayout checkersBoardGL;
	public View inflateSquare;
	public ImageView squaresOfBoard[][];
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// I need to create a different xml layout... Actually, I will do it now.
		setContentView(R.layout.checkers_game);
		// Grabs the layout of the checkersboard
		GridLayout checkersBoardGL = (GridLayout) findViewById(R.id.checkersboard_gridlayout);
		// All the squares of the checkers board will be stored in here.
		squaresOfBoard = new ImageView[8][8];
		
		// ---- Initially Populates the Checkersboard and Adds the Events to the Squares ---- \\ 
		// For each row of the checkersboard...
		for(int x = 0;x<8;x++)
		{
			final int loopX = x;
			// Grabs the LayoutInflater, and stores it in a readily available variable
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// Used to determine what checkers' will go where
				
			for(int y = 0;y<8;y++)
			{		
				final int loopY = y;
				// Inflates the 'View' with the layout of the given XML.
				inflateSquare = inflater.inflate(R.layout.gridview_item, checkersBoardGL, false);
				// Store the ImageView specified in the parsed XML layout, into a runtime ImageView for further modification.
				ImageView imageOfSquare = (ImageView) inflateSquare.findViewById(R.id.image_square);
				
				if(x < 3)
				{
					// For the first three rows, it will print light-brown checkers pieces
					addSquares(R.drawable.light_brown_piece, x, y, imageOfSquare, checkersBoardGL,inflateSquare);
					
				}else if(x > 4 && x < 8){
					// For the last three rows, it will print dark-brown checkers pieces
					addSquares(R.drawable.dark_brown_piece, x, y, imageOfSquare, checkersBoardGL, inflateSquare);
					
				}else{
					// For the two rows in the middle of the checkers board, it will print empty squares.
					// It does not need to set any image resources so therefore, the first parameter variable is 0 as I still needed to pass
					// in an integer due to the specification of this method - This 0 is not even used.
					addSquares(0, x, y, imageOfSquare, checkersBoardGL, inflateSquare);
				}
				
				// This will add events to the 32 (dark brown) squares of the checkersboard because these are the only usuable squares on the board.
				// The other 32 sqaures are just for design.
				if(x % 2 == 0 && y % 2 == 1){
					// This will add the event to the square, that could be empty or occupying the checkers piece. 
					squaresOfBoard[loopX][loopY].setOnClickListener(new MakeMove(squaresOfBoard, loopX, loopY));
					
				}else if(x % 2 == 1 && y % 2 == 0){
					// This will add the event to the square, that could be empty or occupying the checkers piece. 
					squaresOfBoard[loopX][loopY].setOnClickListener(new MakeMove(squaresOfBoard, loopX, loopY));
				}			
			}
		}
		
		// Now, we can add the code moving the pieces, and shit.
	}	
	public void populateBoard()
	{
		// I need to copy the code 'onCreate' into here, and make modifications to it so, it can be method-friendly.
	}
	
	public void addSquares(int resourceId, int passX, int passY, ImageView passImage, GridLayout passGridLayout, View passGridSquare)
	{
		// Using non-final variables in inner classes are not permitted but, I got rid of my inner class so, it does not have to be final anymore. 
		final int x = passX, y = passY;
		
		if(x % 2 == 0 && y % 2 == 1)
		{
			// Prints on rows 0, 2, 4, 8, and columns 1, 3, 5, 7
			
			// This method used causes latency so, use the 'setImageDrawable()' method instead.
			passImage.setImageResource(resourceId);
			// Adds the square to the checkers board :)
			passGridLayout.addView(passGridSquare);
			// I NEED TO CONFIRM IF THIS ARRAY WILL ALLOW ME TO MODIFY THE IMAGEVIEWS THROUGH THIS!
			squaresOfBoard[x][y] = passImage;
					
		}else if(x % 2 == 1 && y % 2 == 0){
			// Prints on rows 1, 3, 5, 7, and columns 0, 2, 4, 8
			
			// This method used causes latency so, use the 'setImageDrawable()' method instead.
			passImage.setImageResource(resourceId);
			// Adds the square to the checkers board :)
			passGridLayout.addView(passGridSquare);
			// I NEED TO CONFIRM IF THIS ARRAY WILL ALLOW ME TO MODIFY THE IMAGEVIEWS THROUGH THIS!
			squaresOfBoard[x][y] = passImage;
			
			
		}else
		{
			// Adds an empty square to the checkers board :)
			passGridLayout.addView(passGridSquare);
			// I NEED TO CONFIRM IF THIS ARRAY WILL ALLOW ME TO MODIFY THE IMAGEVIEWS THROUGH THIS!
			squaresOfBoard[x][y] = passImage;
		}
	}
}

class MakeMove implements View.OnClickListener
{
	// Member Variables
	public int row, column;
	public ImageView[][] squaresOfBoard;
	
	// Constructor
	public MakeMove(ImageView[][] passSquares, int passX, int passY)
	{
		squaresOfBoard = passSquares;
		row = passX;
		column = passY;
	}
	
	public void onClick(View v)
	{
		// Yes!!! It changed the image of the first light brown checkers piece... It is safe to go on ahead now :)
		squaresOfBoard[row][column].setImageResource(R.drawable.ic_launcher);
		
		// Now, how would I find out the row/column of the square that called the event...
		// Well, I could try loop through all the squaresOfBoard until 'squaresOfBoard[x][y].equals(v) ;)
		//
		
		for(int x = 0;x<8;x++)
		{
			for(int y = 0;y<8;y++)
			{
				if(squaresOfBoard[x][y].equals(v))
				{
					// So, that worked.
					Log.i("The square clicked is located at ", "row " + x + " and column " + y);
				}
			}
		}
		
		
	}
}