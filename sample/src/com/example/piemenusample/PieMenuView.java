package com.example.piemenusample;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;

public class PieMenuView
	extends SurfaceView
	implements Runnable, View.OnTouchListener
{
	private PieMenuRenderer renderer;
	private SurfaceHolder surfaceHolder;
	private volatile boolean running = false;
	private Thread thread;

	public PieMenuView( Context context )
	{
		super( context );
		init( context );
	}

	public PieMenuView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( context );
	}

	public PieMenuView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( context );
	}

	public void onResume()
	{
		if( renderer == null )
			return;

		running = true;

		thread = new Thread( this );
		thread.start();
	}

	public void onPause()
	{
		running = false;

		for( boolean retry = true; retry; )
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onTouch( View v, MotionEvent e )
	{
		if( renderer != null )
			renderer.touch( e );

		return true;
	}

	@Override
	public void run()
	{
		for( boolean initialized = false; running; )
		{
			if( surfaceHolder.getSurface().isValid() )
			{
				final Canvas c = surfaceHolder.lockCanvas();

				if( !initialized )
				{
					renderer.setup( c.getWidth(), c.getHeight() );
					initialized = true;
				}

				renderer.draw( c );

				surfaceHolder.unlockCanvasAndPost( c );
			}
		}
	}

	private void init( Context context )
	{
		renderer = new PieMenuRenderer(
			context.getResources() );

		surfaceHolder = getHolder();

		setOnTouchListener( this );
	}
}
