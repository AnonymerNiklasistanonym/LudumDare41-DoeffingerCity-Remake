package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.mygdx.game.MainGame;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;

public class ControllerCallbackMenuState implements ControllerListener {

	private final IControllerCallbackMenuState controllerCallbackClass;

	public ControllerCallbackMenuState(IControllerCallbackMenuState controllerCallbackClass) {
		Gdx.app.debug("controller_callback_menu_state:constructor", MainGame.getCurrentTimeStampLogString());
		this.controllerCallbackClass = controllerCallbackClass;
	}

	@Override
	public void connected(Controller controller) {
		Gdx.app.debug("controller_callback_menu_state:connected", MainGame.getCurrentTimeStampLogString() + "controller connected with the id \"" + controller.getName() + "\"");
	}

	@Override
	public void disconnected(Controller controller) {
		Gdx.app.debug("controller_callback_menu_state:disconnected", MainGame.getCurrentTimeStampLogString() + "controller disconnected with the id \"" + controller.getName() + "\"");
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		// The button pressed reaction is donne in the buttonPressed method
		buttonPressed(controller, buttonCode, true);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		// The button pressed reaction is donne in the buttonPressed method
		buttonPressed(controller, buttonCode, false);
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		Gdx.app.debug("controller_callback_menu_state:axisMoved", MainGame.getCurrentTimeStampLogString() + "controller axis moved \"" + axisCode + "\" with the value " + value);

		// TODO Left, Right, Up, Down input handling is missing
		// final boolean isXAxis = axisCode == ControllerWiki.AXIS_LEFT_X || axisCode == ControllerWiki.AXIS_RIGHT_X;
		// controllerMenuCallbackInterface.controllerCallbackStickMoved(isXAxis, value);
		return false;
	}

	private void buttonPressed(Controller controller, int buttonId, boolean pressed) {
		Gdx.app.debug("controller_callback_menu_state:buttonPressed", MainGame.getCurrentTimeStampLogString() + "controller button" + (pressed ? "" : " not") + " pressed \"" + buttonId + "\"");

		if (pressed) {
			switch (ControllerInputMapping.getControllerButton(controller, buttonId)) {
				case BUTTON_A:
				case BUTTON_X:
				case BUTTON_Y:
					controllerCallbackClass.controllerCallbackClickMenuButton();
					break;
				case BUTTON_B:
				case BUTTON_BACK:
						controllerCallbackClass.controllerCallbackClickExitButton();
					break;
				case BUTTON_START:
						controllerCallbackClass.controllerCallbackClickStartMenuButton();
					break;
				case BUTTON_LB:
				case BUTTON_RB:
						controllerCallbackClass.controllerCallbackToggleFullScreen();
					break;
				case BUTTON_UP:
					controllerCallbackClass.controllerCallbackSelectAboveMenuButton();
					break;
				case BUTTON_DOWN:
					controllerCallbackClass.controllerCallbackSelectBelowMenuButton();
					break;
				case BUTTON_LEFT:
					controllerCallbackClass.controllerCallbackSelectLeftMenuButton();
					break;
				case BUTTON_RIGHT:
					controllerCallbackClass.controllerCallbackSelectRightMenuButton();
					break;
				default:
					// not important
			}
		}
	}

}
