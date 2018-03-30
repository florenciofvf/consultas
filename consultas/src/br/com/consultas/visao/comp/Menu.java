package br.com.consultas.visao.comp;

import javax.swing.Icon;
import javax.swing.JMenu;

import br.com.consultas.util.Util;

public class Menu extends JMenu {
	private static final long serialVersionUID = 1L;

	public Menu(String chaveRotulo) {
		super(Util.getString(chaveRotulo));
	}

	public Menu(String chaveRotulo, Icon icon) {
		super(Util.getString(chaveRotulo));
		setIcon(icon);
	}
}