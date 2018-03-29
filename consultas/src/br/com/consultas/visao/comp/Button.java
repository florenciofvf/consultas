package br.com.consultas.visao.comp;

import javax.swing.Icon;
import javax.swing.JButton;

import br.com.consultas.util.Util;

public class Button extends JButton {
	private static final long serialVersionUID = 1L;

	public Button(String chaveRotulo) {
		super(Util.getString(chaveRotulo));
	}

	public Button(String chaveRotulo, Icon icon) {
		super(Util.getString(chaveRotulo), icon);
	}
}