package br.com.consultas.visao;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import br.com.consultas.util.Util;

public class Popup extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	final JMenuItem itemPesquisaDialogoLimpo = new JMenuItem(Util.getString("label.gerar_pesquisa_dialogo_limpo"));
	final JMenuItem itemRegistrosDialogoLimpo = new JMenuItem(Util.getString("label.gerar_dados_dialogo_limpo"));
	final JMenuItem itemPesquisaDialogo = new JMenuItem(Util.getString("label.gerar_pesquisa_dialogo"));
	final JMenuItem itemRegistrosDialogo = new JMenuItem(Util.getString("label.gerar_dados_dialogo"));
	final JMenu menuDialogo = new JMenu(Util.getString("label.dialogo"));

	final JMenuItem itemPesquisaMemoriaLimpo = new JMenuItem(Util.getString("label.gerar_pesquisa_memoria_limpo"));
	final JMenuItem itemRegistrosMemoriaLimpo = new JMenuItem(Util.getString("label.gerar_dados_memoria_limpo"));
	final JMenuItem itemPesquisaMemoria = new JMenuItem(Util.getString("label.gerar_pesquisa_memoria"));
	final JMenuItem itemRegistrosMemoria = new JMenuItem(Util.getString("label.gerar_dados_memoria"));
	final JMenu menuMemoria = new JMenu(Util.getString("label.memoria"));

	final JMenuItem itemDelete = new JMenuItem(Util.getString("label.gerar_delete"));
	final JMenuItem itemUpdate = new JMenuItem(Util.getString("label.gerar_update"));
	final JMenu menuDML = new JMenu(Util.getString("label.dml"));

	final JMenuItem itemLimparCampos = new JMenuItem(Util.getString("label.limpar_campos"));
	final JMenuItem itemLimparId = new JMenuItem(Util.getString("label.limpar_id"));
	final JMenuItem itemCampos = new JMenuItem(Util.getString("label.campos"));
	final JMenu menuCampo = new JMenu(Util.getString("label.campo"));

	public Popup() {
	}

	public void dialogoMeuSQL() {
		menuDialogo.add(itemRegistrosDialogoLimpo);
		menuDialogo.add(itemRegistrosDialogo);
		add(menuDialogo);
	}

	public void dialogo() {
		menuDialogo.add(itemPesquisaDialogoLimpo);
		menuDialogo.add(itemPesquisaDialogo);
		menuDialogo.addSeparator();
		menuDialogo.add(itemRegistrosDialogoLimpo);
		menuDialogo.add(itemRegistrosDialogo);
		add(menuDialogo);
	}

	public void memoriaMeuSQL() {
		menuMemoria.add(itemRegistrosMemoriaLimpo);
		menuMemoria.add(itemRegistrosMemoria);
		add(menuMemoria);
	}

	public void memoria() {
		menuMemoria.add(itemPesquisaMemoriaLimpo);
		menuMemoria.add(itemPesquisaMemoria);
		menuMemoria.addSeparator();
		menuMemoria.add(itemRegistrosMemoriaLimpo);
		menuMemoria.add(itemRegistrosMemoria);
		add(menuMemoria);
	}

	public void campos() {
		menuCampo.add(itemLimparCampos);
		menuCampo.add(itemLimparId);
		menuCampo.add(itemCampos);
		add(menuCampo);
	}

	public void dml() {
		menuDML.add(itemUpdate);
		menuDML.addSeparator();
		menuDML.add(itemDelete);
		add(menuDML);
	}
}