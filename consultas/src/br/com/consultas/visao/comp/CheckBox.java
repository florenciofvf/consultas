package br.com.consultas.visao.comp;

import javax.swing.JCheckBox;

import br.com.consultas.util.Util;

public class CheckBox extends JCheckBox {
	private static final long serialVersionUID = 1L;

	public CheckBox(String chaveRotulo, String chaveEstado) {
		super(Util.getString(chaveRotulo), Util.getBooleanConfig(chaveEstado));
	}
}