/*
 *   O         ,-
 *  ° o    . -´  '     ,-
 *   °  .´        ` . ´,´
 *     ( °   ))     . (
 *      `-;_    . -´ `.`.
 *          `._'       ´
 *
 * Copyright (c) 2007-2013 Markus Fisch <mf@markusfisch.de>
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/mit-license.php
 */
package de.markusfisch.library.piemenu;

import java.util.ArrayList;

/**
 * A zooming pie menu
 */
public class PieMenu
{
	private static double DOUBLE_PI = Math.PI+Math.PI;
	private static double HALF_PI = Math.PI/2f;

	public static class Icon
	{
		public double weight;
		public double size;
		public double cellSize;
		public int x;
		public int y;
	}

	public int numberOfIcons = 0;
	public ArrayList<Icon> icons = new ArrayList<Icon>();
	public double radius = 0;
	public double twist = 0;
	public int selectedIcon = -1;
	public int centerX = 0;
	public int centerY = 0;

	/**
	 * Set up pie menu
	 *
	 * @param cx - horizontal position of pie menu
	 * @param cy - vertical position of pie menu
	 * @param r - radius of pie menu
	 */
	public void setup( int cx, int cy, int r )
	{
		centerX = cx;
		centerY = cy;
		radius = r;
	}

	/**
	 * Calculate sizes and positions of icons
	 *
	 * @param x - horizontal cursor position
	 * @param y - vertical cursor position
	 */
	public void calculate( float x, float y )
	{
		selectedIcon = -1;

		if( numberOfIcons < 1 ||
			icons == null )
			return;

		int closestIcon = 0;
		boolean cursorNearCenter = false;

		// calculate positions and sizes
		{
			double circumference = Math.PI*(radius*2f);
			double pixelsPerRadian = DOUBLE_PI/circumference;
			double centeredY = y-centerY;
			double centeredX = x-centerX;
			double cursorAngle = Math.atan2( centeredY, centeredX );
			double cellSize = DOUBLE_PI/numberOfIcons;
			double closestAngle = 0;
			double weight = 0;
			double maxIconSize = .8f*radius;
			double maxWeight;

			// calculate weight of each icon
			{
				double cursorRadius = Math.sqrt(
					(centeredY*centeredY)+
					(centeredX*centeredX) );
				double infieldRadius = radius/2f;
				double f = cursorRadius/infieldRadius;

				if( cursorRadius < infieldRadius )
				{
					double b = (circumference/numberOfIcons)*.75f;

					if( b < maxIconSize )
						maxIconSize = b+(maxIconSize-b)*f;

					cursorNearCenter = true;
				}

				// determine how close every icon is to the cursor
				{
					double closestDistance = DOUBLE_PI;
					double a = twist;
					double m = (maxIconSize*pixelsPerRadian)/cellSize;

					maxWeight = HALF_PI+Math.pow( Math.PI, m );

					for( int n = 0; n < numberOfIcons; ++n )
					{
						double d = Math.abs(
							getAngleDifference( a, cursorAngle ) );

						if( d < closestDistance )
						{
							closestDistance = d;
							closestIcon = n;
							closestAngle = a;
						}

						if( cursorRadius < infieldRadius )
							d *= f;

						Icon ic = icons.get( n );

						ic.weight =
							HALF_PI+Math.pow( Math.PI-d, m );

						weight += ic.weight;

						if( (a += cellSize) > Math.PI )
							a -= DOUBLE_PI;
					}

					if( !cursorNearCenter )
						selectedIcon = closestIcon;
				}
			}

			// calculate size of icons
			{
				double sizeUnit = circumference/weight;

				for( int n = numberOfIcons; n-- > 0; )
				{
					Icon ic = icons.get( n );

					ic.size = ic.cellSize = sizeUnit*ic.weight;
				}

				// scale icons within cell
				{
					double maxSize = sizeUnit*maxWeight;

					if( maxSize > maxIconSize )
					{
						double f = maxIconSize/maxSize;

						for( int n = numberOfIcons; n-- > 0; )
							icons.get( n ).size *= f;
					}
				}
			}

			// calculate icon positions
			{
				double difference = getAngleDifference(
					cursorAngle, closestAngle );
				double angle = getValidAngle(
					cursorAngle-(
						pixelsPerRadian*
						icons.get( closestIcon ).cellSize)/
					cellSize*difference );

				// active icon
				icons.get( closestIcon ).x = centerX+(int)Math.round(
					radius*Math.cos( angle ) );
				icons.get( closestIcon ).y = centerY+(int)Math.round(
					radius*Math.sin( angle ) );

				// calculate positions of all other icons
				{
					double leftAngle = angle;
					double rightAngle = angle;
					int left = closestIcon;
					int right = closestIcon;
					int previousRight = closestIcon;
					int previousLeft = closestIcon;

					for( int n = 0; ; ++n )
					{
						if( (--left) < 0 )
							left = numberOfIcons-1;

						// break here when number of icons is odd
						if( right == left )
							break;

						if( (++right) >= numberOfIcons )
							right = 0;

						Icon lic = icons.get( left );

						leftAngle = getValidAngle(
							leftAngle-(
								(.5f*icons.get( previousLeft ).cellSize)+
								(.5f*lic.cellSize)
							)*pixelsPerRadian );

						lic.x = centerX+(int)Math.round(
							radius*Math.cos( leftAngle ) );
						lic.y = centerY+(int)Math.round(
							radius*Math.sin( leftAngle ) );

						// break here when number of icons is even
						if( left == right )
							break;

						Icon ric = icons.get( right );

						rightAngle = getValidAngle(
							rightAngle+
							(
								(.5f*icons.get( previousRight ).cellSize)+
								(.5f*ric.cellSize)
							)*pixelsPerRadian );

						ric.x = centerX+(int)Math.round(
							radius*Math.cos( rightAngle ) );
						ric.y = centerY+(int)Math.round(
							radius*Math.sin( rightAngle ) );

						previousRight = right;
						previousLeft = left;
					}
				}
			}
		}
	}

	private static double getAngleDifference( double a, double b )
	{
		double c = a-b;
		double d = a > b ?
			a-(b+DOUBLE_PI) :
			a-(b-DOUBLE_PI);

		return Math.abs( c ) < Math.abs( d ) ? c : d;
	}

	private static double getValidAngle( double a )
	{
		if( a < -Math.PI )
			a += DOUBLE_PI;
		else if( a > Math.PI )
			a -= DOUBLE_PI;

		return a;
	}
}
