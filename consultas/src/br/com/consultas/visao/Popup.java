package br.com.consultas.visao;

import javax.swing.JPopupMenu;

import br.com.consultas.util.Icones;
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
	public final MenuItem itemCopiarComAspas = new MenuItem("label.copiar_com_aspas");
	public final MenuItem itemMostrarRegistro = new MenuItem("label.abrir_registro");
	final MenuItem itemRegistrosDialogo = new MenuItem("label.gerar_dados_dialogo");
	final MenuItem itemRegistrosMemoria = new MenuItem("label.gerar_dados_memoria");
	final MenuItem itemPesquisaSelecionados = new MenuItem("label.pesquisa_sel");
	final MenuItem itemAgruparCampoPai = new MenuItem("label.agrupar_campo_pai");
	final MenuItem itemAgruparTotalPai = new MenuItem("label.agrupar_total_pai");
	public final MenuItem itemFiltrarCampo = new MenuItem("label.filtrar_campo");
	final Menu menuCalculado = new Menu("label.calculado", Icones.CALCULADO);
	final MenuItem itemAgruparTotal = new MenuItem("label.agrupar_total");
	final MenuItem itemAgruparCampo = new MenuItem("label.agrupar_campo");
	final MenuItem itemLimparCampos = new MenuItem("label.limpar_campos");
	final MenuItem itemPropriedades = new MenuItem("label.propriedades");
	final Menu menuDialogo = new Menu("label.dialogo", Icones.DIALOGO);
	final Menu menuMemoria = new Menu("label.memoria", Icones.MEMORIA);
	public final MenuItem itemCopiar = new MenuItem("label.copiar");
	final MenuItem itemDelete = new MenuItem("label.gerar_delete");
	final MenuItem itemUpdate = new MenuItem("label.gerar_update");
	final MenuItem itemLimparId = new MenuItem("label.limpar_id");
	final Menu menuCampo = new Menu("label.campo", Icones.CAMPOS);
	final MenuItem itemCampos = new MenuItem("label.campos");
	final Menu menuDML = new Menu("label.dml", Icones.DML);
	private int tag;

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
		menuMemoria.addSeparator();
		menuMemoria.add(itemPesquisaSelecionados);

		add(menuMemoria);
	}

	public void campos() {
		menuCampo.add(itemLimparCampos);
		menuCampo.add(itemLimparId);
		menuCampo.addSeparator();
		menuCampo.add(itemCampos);

		add(menuCampo);
	}

	public void calculado() {
		menuCalculado.add(itemAgruparTotal);
		menuCalculado.add(itemAgruparCampo);
		menuCalculado.addSeparator();
		menuCalculado.add(itemAgruparTotalPai);
		menuCalculado.add(itemAgruparCampoPai);

		add(menuCalculado);
	}

	public void propriedades() {
		add(itemPropriedades);
	}

	public void copiar(boolean filtrar) {
		add(itemCopiar);
		addSeparator();
		add(itemCopiarComAspas);

		if (filtrar) {
			addSeparator();
			add(itemFiltrarCampo);
		}
	}

	public void mostrarRegistro() {
		addSeparator();
		add(itemMostrarRegistro);
	}

	public void dml() {
		menuDML.add(itemUpdate);
		menuDML.addSeparator();
		menuDML.add(itemDelete);

		add(menuDML);
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
}