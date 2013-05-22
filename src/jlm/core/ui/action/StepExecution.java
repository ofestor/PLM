package jlm.core.ui.action;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.ImageIcon;

import jlm.core.HumanLangChangesListener;
import jlm.core.model.Game;
import jlm.core.model.GameState;


public class StepExecution extends AbstractGameAction implements HumanLangChangesListener {

	private static final long serialVersionUID = 930451111824072175L;

	public StepExecution(Game game, String text, ImageIcon icon) {
		super(game, text, icon);
		game.addHumanLangListener(this);
	}
	@Override
	public void currentHumanLanguageHasChanged(Locale newLang) {
		setDescription(i18n.tr("Execute one step of your code"), 
				i18n.tr("Impossible to step your code now. Need to stop the execution first?"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (game.getState().equals(GameState.EXECUTION_STARTED))
			game.allowOneStep();
		else			
			game.startExerciseStepExecution();		
	}
}
