package br.com.consultas.visao.comp;

import java.awt.Color;

import javax.swing.JLabel;

import br.com.consultas.util.Util;

public class Label extends JLabel {
	private static final long serialVersionUID = 1L;

	public Label() {
	}

	public Label(Color corFonte) {
		super();
		setForeground(corFonte);
	}

	public Label(String chaveRotulo) {
		super(Util.getString(chaveRotulo));
	}

	public Label(String chaveRotulo, Color corFonte) {
		super(Util.getString(chaveRotulo));
		setForeground(corFonte);
	}
}