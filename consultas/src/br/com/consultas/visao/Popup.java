package br.com.consultas.visao;

import javax.swing.JPopupMenu;

import br.com.consultas.visao.comp.Menu;
import br.com.consultas.visao.comp.MenuItem;

public class Popup extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	final MenuItem itemPesquisaDialogoAliasLimpo = new MenuItem("label.gerar_pesquisa_dialogo_alias_limpo");
	final MenuItem itemPesquisaDialogoLimpo = new MenuItem("label.gerar_pesquisa_dialogo_limpo");
	final MenuItem itemPesquisaMemoriaLimpo = new MenuItem("label.gerar_pesquisa_memoria_limpo");
	final MenuItem itemPesquisaDialogoAlias = new MenuItem("label.gerar_pesquisa_dialogo_alias");
	final MenuItem itemRegistrosMemoriaLimpo = new MenuItem("label.gerar_dados_memoria_limpo");
	final MenuItem itemRegistrosDialogoLimpo = new MenuItem("label.gerar_dados_dialogo_limpo");
	final MenuItem itemPesquisaDialogo = new MenuItem("label.gerar_pesquisa_dialogo");
	final MenuItem itemPesquisaMemoria = new MenuItem("label.gerar_pesquisa_memoria");
	final MenuItem itemRegistrosDialogo = new MenuItem("label.gerar_dados_dialogo");
	final MenuItem itemRegistrosMemoria = new MenuItem("label.gerar_dados_memoria");
	final MenuItem itemLimparCampos = new MenuItem("label.limpar_campos");
	final MenuItem itemDelete = new MenuItem("label.gerar_delete");
	final MenuItem itemUpdate = new MenuItem("label.gerar_update");
	final MenuItem itemLimparId = new MenuItem("label.limpar_id");
	final MenuItem itemCampos = new MenuItem("label.campos");
	final Menu menuDialogo = new Menu("label.dialogo");
	final Menu menuMemoria = new Menu("label.memoria");
	final Menu menuCampo = new Menu("label.campo");
	final Menu menuDML = new Menu("label.dml");

	public void dialogoMeuSQL() {
		menuDialogo.add(itemRegistrosDialogoLimpo);
		menuDialogo.add(itemRegistrosDialogo);

		add(menuDialogo);
	}

	public void memoriaMeuSQL() {
		menuMemoria.add(itemRegistrosMemoriaLimpo);
		menuMemoria.add(itemRegistrosMemoria);

		add(menuMemoria);
	}

	public void dialogo() {
		menuDialogo.add(itemPesquisaDialogoLimpo);
		menuDialogo.add(itemPesquisaDialogo);
		menuDialogo.addSeparator();
		menuDialogo.add(itemPesquisaDialogoAliasLimpo);
		menuDialogo.add(itemPesquisaDialogoAlias);
		menuDialogo.addSeparator();
		menuDialogo.add(itemRegistrosDialogoLimpo);
		menuDialogo.add(itemRegistrosDialogo);

		add(menuDialogo);
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