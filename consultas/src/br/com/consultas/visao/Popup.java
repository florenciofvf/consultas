package br.com.consultas.visao;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import br.com.consultas.util.Util;

public class Popup extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	final JMenuItem itemMeuSQLDialogo = new JMenuItem(Util.getString("label.gerar_dados_dialogo"));
	final JMenuItem itemSQLDialogo = new JMenuItem(Util.getString("label.gerar_sql_dialogo"));
	final JMenu menuDialogo = new JMenu(Util.getString("label.dialogo"));

	final JMenuItem itemMeuSQLMemoria = new JMenuItem(Util.getString("label.gerar_dados_memoria"));
	final JMenuItem itemSQLMemoria = new JMenuItem(Util.getString("label.gerar_sql_memoria"));
	final JMenu menuMemoria = new JMenu(Util.getString("label.memoria"));

	final JMenuItem itemDelete = new JMenuItem(Util.getString("label.gerar_delete"));
	final JMenuItem itemUpdate = new JMenuItem(Util.getString("label.gerar_update"));
	final JMenuItem itemCampos = new JMenuItem(Util.getString("label.campos"));
	final JMenu menuDML = new JMenu(Util.getString("label.dml"));

	public Popup() {
	}

	public void dialogoMeuSQL() {
		menuDialogo.add(itemMeuSQLDialogo);
		add(menuDialogo);
	}

	public void dialogo() {
		menuDialogo.add(itemMeuSQLDialogo);
		menuDialogo.addSeparator();
		menuDialogo.add(itemSQLDialogo);
		add(menuDialogo);
	}

	public void memoriaMeuSQL() {
		menuMemoria.add(itemMeuSQLMemoria);
		add(menuMemoria);
	}

	public void memoria() {
		menuMemoria.add(itemMeuSQLMemoria);
		menuMemoria.addSeparator();
		menuMemoria.add(itemSQLMemoria);
		add(menuMemoria);
	}

	public void campos() {
		add(itemCampos);
	}

	public void dml() {
		menuDML.add(itemUpdate);
		menuDML.addSeparator();
		menuDML.add(itemDelete);
		add(menuDML);
	}
}