package br.com.consultas.visao.comp;

import javax.swing.JMenuItem;

import br.com.consultas.util.Util;

public class MenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;

	public MenuItem(String chaveRotulo) {
		super(Util.getString(chaveRotulo));
	}
}
