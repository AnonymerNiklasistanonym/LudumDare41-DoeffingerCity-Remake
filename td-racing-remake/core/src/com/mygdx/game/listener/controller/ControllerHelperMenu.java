package com.mygdx.game.listener.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
//import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;

public class ControllerHelperMenu implements ControllerListener {

	private final ControllerMenuCallbackInterface controllerMenuCallbackInterface;

	public ControllerHelperMenu(ControllerMenuCallbackInterface controllerMenuCallbackInterface) {
		Gdx.app.debug("controller_helper_menu:constructor", MainGame.getCurrentTimeStampLogString());

		this.controllerMenuCallbackInterface = controllerMenuCallbackInterface;
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		Gdx.app.debug("controller_helper_menu:connected", MainGame.getCurrentTimeStampLogString() + "controller connected with the id \"" + controller.getName() + "\"");
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		Gdx.app.debug("controller_helper_menu:disconnected", MainGame.getCurrentTimeStampLogString() + "controller disconnected with the id \"" + controller.getName() + "\"");
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		buttonPressed(buttonCode);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		buttonPressed(buttonCode, false);
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		Gdx.app.debug("controller_helper_menu:axisMoved", MainGame.getCurrentTimeStampLogString() + "controller axis moved \"" + axisCode + "\" with the value " + value);

		final boolean isXAxis = axisCode == ControllerWiki.AXIS_LEFT_X || axisCode == ControllerWiki.AXIS_RIGHT_X;
		controllerMenuCallbackInterface.controllerCallbackStickMoved(isXAxis, value);
		return false;
	}

	private void buttonPressed(int buttonId) {
		buttonPressed(buttonId, true);
	}

	private void buttonPressed(int buttonId, boolean pressed) {
		Gdx.app.debug("controller_helper_menu:buttonPressed", MainGame.getCurrentTimeStampLogString() + "controller button pressed \"" + buttonId + "\"");

		switch (buttonId) {
		case ControllerWiki.BUTTON_A:
			if (pressed)
				controllerMenuCallbackInterface.controllerCallbackButtonPressed(buttonId);
			break;
		case ControllerWiki.BUTTON_B:
			if (pressed)
				controllerMenuCallbackInterface.controllerCallbackBackPressed();
			break;
		case ControllerWiki.BUTTON_X:
			if (pressed)
				controllerMenuCallbackInterface.controllerCallbackButtonPressed(buttonId);
			break;
		case ControllerWiki.BUTTON_Y:
			if (pressed)
				controllerMenuCallbackInterface.controllerCallbackButtonPressed(buttonId);
			break;
		case ControllerWiki.BUTTON_BACK:
			if (pressed)
				controllerMenuCallbackInterface.controllerCallbackBackPressed();
			break;
		case ControllerWiki.BUTTON_START:
			if (pressed)
				controllerMenuCallbackInterface.controllerCallbackButtonPressed(buttonId);
			break;
		default:
			// not important
		}
	}

}
