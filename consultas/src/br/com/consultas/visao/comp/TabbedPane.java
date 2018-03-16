package br.com.consultas.visao.comp;

import java.awt.Component;

import javax.swing.JTabbedPane;

import br.com.consultas.util.Util;

public class TabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	@Override
	public void addTab(String chaveRotulo, Component component) {
		super.addTab(Util.getString(chaveRotulo), component);
	}
}