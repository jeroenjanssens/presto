/**
 * Copyright 2009 Tilburg University. All rights reserved.
 * 
 * This file is part of Presto.
 *
 * Presto is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Presto is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Presto.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jeroenjanssens.presto.views.scenario.timeline;

import org.eclipse.swt.widgets.Display;


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class Animator {

	private TimeLine timeLine;
	
	private boolean isAnimating;
	private boolean isPlaying;
	private boolean isReverse;
	private boolean isSyncing;
	
	private int speed; //number of milliseconds in real time per frame
	private int frameRate; //number of frames per second
	
	private Runnable timer;
	
	private Display display;
	
	public Animator(final TimeLine timeLine) {
		this.timeLine = timeLine;
			
		frameRate = 10;
		setSpeed(100);
		
		isPlaying = false;
		isAnimating = false;

		display = timeLine.getDisplay();
	
		timer = new Runnable() {
			public void run() {
				display.timerExec(1000 / frameRate, timer);
				if(isPlaying) {
					if(isReverse) {
						timeLine.increaseTime(-speed);
					} else {
						timeLine.increaseTime(speed);
					}
				}
				
				if(isAnimating) {
					timeLine.doRedraw();
					timeLine.getScenarioEditor().getEarthView().getWWD().redraw();
				}
			}
		};
	}
	
	public void stop() {
		isPlaying = false;
		isReverse = false;
		isAnimating = false;
		display.timerExec(-1, timer);
	}
	
	public void stopAnimate() {
		if(isAnimating && !isPlaying) {
			isPlaying = false;
			isReverse = false;
			isAnimating = false;
			display.timerExec(-1, timer);
		}
	}
	
	public void startAnimate() {
		if(!isAnimating) {
			isAnimating = true;
			display.timerExec(1000 / frameRate, timer);
		}
	}
	
	public void play() {
		isReverse = false;
		if(!isPlaying) {
			isPlaying = true;
			isAnimating = true;
			display.timerExec(1000 / frameRate, timer);
		}
	}

	public void playReverse() {
		isReverse = true;
		if(!isPlaying) {
			isPlaying = true;
			isAnimating = true;
			display.timerExec(1000 / frameRate, timer);
		}
	}
	
	public void setSpeed(int percent) {
		//double p = percent / 100.0;
		
		speed = (10 * percent) / frameRate;
	}
	
	public boolean isPlaying() {
		return this.isPlaying && !this.isReverse;
	}
	
	public boolean isPlayingReverse() {
		return this.isPlaying && this.isReverse;
	}
	
	public boolean isStopped() {
		return !this.isPlaying;
	}

	public void prevFrame() {
		timeLine.increaseTime(-speed);
		timeLine.doRedraw();
		timeLine.getScenarioEditor().getEarthView().getWWD().redraw();
	}
	
	public void nextFrame() {
		timeLine.increaseTime(speed);
		timeLine.doRedraw();
		timeLine.getScenarioEditor().getEarthView().getWWD().redraw();
		
	}
}
