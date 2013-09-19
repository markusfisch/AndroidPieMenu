package com.example.piemenusample;

import de.markusfisch.library.piemenu.PieMenu;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

public class PieMenuRenderer
{
	private static final int MAX_ICON_SIZE = 128;
	private static final int DRAWABLES[] = {
		R.drawable.icon,
		R.drawable.icon,
		R.drawable.icon,
		R.drawable.icon,
		R.drawable.icon,
		R.drawable.icon,
		R.drawable.icon,
		R.drawable.icon,
		R.drawable.icon };

	private final PieMenu pieMenu = new PieMenu();
	private int width = 0;
	private int height = 0;
	private float touchX = -1f;
	private float touchY = -1f;
	private float lastTouchX = -1f;
	private float lastTouchY = -1f;

	public PieMenuRenderer( Resources res )
	{
		for( int n = DRAWABLES.length; n-- > 0; )
			pieMenu.icons.add( new Icon(
				res.getDrawable( DRAWABLES[n] ) ) );

		pieMenu.numberOfIcons = pieMenu.icons.size();
	}

	public void setup( int w, int h )
	{
		int radius;
		int min = Math.min( w, h );

		if( Math.floor( min*.28f ) > MAX_ICON_SIZE )
			min = Math.round( MAX_ICON_SIZE/.28f );

		radius = Math.round( min*.4f );

		width = w;
		height = h;
		touchX = w/2f;
		touchY = h/2f;

		pieMenu.setup(
			(int)touchX,
			(int)touchY,
			radius );
	}

	public void draw( Canvas c )
	{
		if( width < 1 )
			return;

		c.drawColor( 0xff000000 );

		if( touchX != lastTouchX ||
			touchY != lastTouchY )
		{
			pieMenu.calculate( touchX, touchY );

			lastTouchX = touchX;
			lastTouchY = touchY;
		}

		for( int n = pieMenu.numberOfIcons; n-- > 0; )
			((Icon)pieMenu.icons.get( n )).draw( c );
	}

	public void touch( MotionEvent e )
	{
		touchX = e.getX();
		touchY = e.getY();

		if( e.getActionMasked() != MotionEvent.ACTION_UP )
			return;

		if( pieMenu.selectedIcon > -1 )
			((Icon)pieMenu.icons.get( pieMenu.selectedIcon )).launch();
	}

	private class Icon extends PieMenu.Icon
	{
		private Drawable icon;

		public Icon( Drawable drawable )
		{
			icon = drawable;
		}

		public void launch()
		{
		}

		public void draw( Canvas c )
		{
			int s = ((int)size)>>1<<1;

			if( s < 1 )
				return;

			int half = s/2;
			int left = x-half;
			int top = y-half;

			icon.setBounds( left, top, left+s, top+s );
			icon.draw( c );
		}
	}
}
