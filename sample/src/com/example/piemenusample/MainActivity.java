package com.example.piemenusample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity
{
	private PieMenuView surfaceView;

	@Override
	public void onCreate( Bundle state )
	{
		super.onCreate( state );

		surfaceView = new PieMenuView( this );

		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( surfaceView );
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		surfaceView.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		surfaceView.onPause();
	}
}
