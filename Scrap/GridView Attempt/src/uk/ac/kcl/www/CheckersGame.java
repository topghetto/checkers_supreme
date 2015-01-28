package uk.ac.kcl.www;

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

import android.view.ViewGroup.*;
import android.widget.GridView;
import android.view.Gravity;


// I forgot to declare the class as public, and in result, it could not load this activity from the main menu
public class CheckersGame extends Activity{
	
	public GridLayout checkersBoardGL;
	public ImageView testImage;
	public GridLayout.LayoutParams params;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// I need to create a different xml layout... Actually, I will do it now.
		setContentView(R.layout.checkers_game);
		
		GridView checkersBoardGV = (GridView) findViewById(R.id.checkers_board_gridview);
		checkersBoardGV.setAdapter(new CheckersBoardAdapter(this));
		
		
	}
	
}