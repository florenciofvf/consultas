package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.Icones;
import br.com.consultas.util.SQL;
import br.com.consultas.util.TreeCellRenderer;
import br.com.consultas.util.Util;
import br.com.consultas.visao.comp.Arvore;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.CheckBox;
import br.com.consultas.visao.comp.PanelBorderLayout;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.SplitPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.comp.TextField;
import br.com.consultas.visao.dialog.CampoDialog;
import br.com.consultas.visao.dialog.DadosDialog;
import br.com.consultas.visao.dialog.ReferenciaPropDialog;
import br.com.consultas.visao.modelo.CampoReferencia;
import br.com.consultas.visao.modelo.ModeloArvore;
import br.com.consultas.visao.modelo.ModeloOrdenacao;
import br.com.consultas.visao.modelo.ModeloReferencia;

public class PainelConsultas extends PanelBorderLayout {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkRaizVisivel = new CheckBox("label.raiz_visivel", "consultas.raiz_visivel");
	private final CheckBox chkLinhaRaiz = new CheckBox("label.raiz_linha", "consultas.raiz_linha");
	private final Table table = new Table(new ModeloOrdenacao(new ModeloReferencia(null)));
	protected final SplitPane splitPane = new SplitPane(SplitPane.HORIZONTAL_SPLIT);
	private final TextField textFieldConsulta = new TextField();
	private final Popup popup = new Popup();
	private final Formulario formulario;
	private Referencia selecionado;
	private final Arvore arvore;

	public PainelConsultas(Formulario formulario) {
		this.formulario = formulario;

		arvore = new Arvore(new ModeloArvore(formulario.getReferencias(), Util.getString("label.consultas")));
		arvore.setCellRenderer(new TreeCellRenderer());
		arvore.addMouseListener(new OuvinteArvore());

		splitPane.setLeftComponent(new ScrollPane(arvore));
		splitPane.setRightComponent(new ScrollPane(table));

		PanelLeft panelNorte = new PanelLeft();

		if (Util.getBooleanConfig("config_arvore")) {
			panelNorte.adicionar(chkRaizVisivel, chkLinhaRaiz);
		}

		Button expandir = new Button("label.expandir", Icones.EXPANDIR);
		expandir.addActionListener(e -> Util.expandirRetrairTodos(arvore, true));

		Button retrair = new Button("label.retrair", Icones.RETRAIR);
		retrair.addActionListener(e -> Util.expandirRetrairTodos(arvore, false));

		textFieldConsulta.addActionListener(e -> {
			Util.selecionarPeloNome(formulario.getReferencias(), textFieldConsulta.getText());
			Util.atualizarTodaEstrutura(arvore);
		});

		panelNorte.adicionar(expandir, retrair, textFieldConsulta);

		add(BorderLayout.NORTH, panelNorte);
		add(BorderLayout.CENTER, splitPane);

		cfg();
	}

	public void atualizarCampoID(Tabela tabela) {
		Util.atualizarCampoID(formulario.getReferencias(), formulario.getTabelas());
		Util.atualizarEstrutura(arvore, formulario.getTabelas(), tabela);
		arvore.repaint();
	}

	public void itemRegistrosDialogoLimpo(Referencia selecionado) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm(tabela);
		registros(selecionado, true);
	}

	private void itemPesquisaDialogoLimpo(Referencia selecionado) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm(tabela);
		pesquisa(selecionado, true);
	}

	private void itemRegistrosMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm(tabela);
		registros(selecionado, false);
	}

	private void itemPesquisaMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm(tabela);
		pesquisa(selecionado, false);
	}

	private void itemPesquisaDialogoAliasLimpo(String aliasTemp) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm(tabela);
		pesquisa(selecionado, true, aliasTemp);
	}

	private void cfg() {
		popup.dialogo();
		popup.addSeparator();
		popup.memoria();
		popup.addSeparator();
		popup.campos();
		popup.addSeparator();
		popup.dml();

		popup.addSeparator();
		popup.propriedades();

		popup.itemRegistrosDialogoLimpo.addActionListener(e -> itemRegistrosDialogoLimpo(selecionado));

		popup.itemRegistrosDialogo.addActionListener(e -> registros(selecionado, true));

		popup.itemRegistrosMemoriaLimpo.addActionListener(e -> itemRegistrosMemoriaLimpo());

		popup.itemRegistrosMemoria.addActionListener(e -> registros(selecionado, false));

		popup.itemPesquisaSelecionados
				.addActionListener(e -> Util.pesquisaSelecionadosMemoria(selecionado, formulario.getTabelas()));

		popup.itemPesquisaDialogoLimpo.addActionListener(e -> itemPesquisaDialogoLimpo(selecionado));

		popup.itemPesquisaDialogo.addActionListener(e -> pesquisa(selecionado, true));

		popup.itemPesquisaDialogoAlias.addActionListener(e -> {
			String aliasTemp = Util.getAliasTemp(PainelConsultas.this, selecionado);

			if (!Util.ehVazio(aliasTemp)) {
				pesquisa(selecionado, true, aliasTemp);
			}
		});

		popup.itemPesquisaDialogoAliasLimpo.addActionListener(e -> {
			String aliasTemp = Util.getAliasTemp(PainelConsultas.this, selecionado);

			if (!Util.ehVazio(aliasTemp)) {
				itemPesquisaDialogoAliasLimpo(aliasTemp);
			}
		});

		popup.itemPesquisaMemoriaLimpo.addActionListener(e -> itemPesquisaMemoriaLimpo());

		popup.itemPesquisaMemoria.addActionListener(e -> pesquisa(selecionado, false));

		popup.itemLimparCampos.addActionListener(e -> {
			Tabela tabela = selecionado.getTabela(formulario.getTabelas());
			tabela.limparCampos();
			formulario.atualizarCampoIDForm(tabela);
		});

		popup.itemLimparId.addActionListener(e -> {
			Tabela tabela = selecionado.getTabela(formulario.getTabelas());
			tabela.limparID();
			formulario.atualizarCampoIDForm(tabela);
		});

		popup.itemCampos
				.addActionListener(e -> new CampoDialog(formulario, selecionado.getTabela(formulario.getTabelas())));

		popup.itemPropriedades.addActionListener(e -> new ReferenciaPropDialog(formulario, selecionado));

		popup.itemUpdate
				.addActionListener(e -> formulario.textArea.setText(selecionado.gerarUpdate(formulario.getTabelas())));

		popup.itemDelete
				.addActionListener(e -> formulario.textArea.setText(selecionado.gerarDelete(formulario.getTabelas())));

		chkRaizVisivel.addActionListener(e -> arvore.setRootVisible(chkRaizVisivel.isSelected()));

		chkLinhaRaiz.addActionListener(e -> arvore.setShowsRootHandles(chkLinhaRaiz.isSelected()));

		splitPane.addPropertyChangeListener(evt -> {
			SplitPane splitPane = (SplitPane) evt.getSource();
			String propertyName = evt.getPropertyName();

			if (SplitPane.DIVIDER_LOCATION_PROPERTY.equals(propertyName)) {
				formulario.divisao(splitPane.getDividerLocation());
			}
		});
	}

	private void registros(Referencia selecionado, boolean abrirDialogo) {
		registros(selecionado, abrirDialogo, null);
	}

	private void registros(Referencia selecionado, boolean abrirDialogo, String aliasTemp) {
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);

		Tabela tabela = selecionado.getTabela(formulario.getTabelas());
		formulario.textArea.setText(sql.dados);
		Util.setContentTransfered(sql.dados);

		if (abrirDialogo) {
			try {
				new DadosDialog(formulario, selecionado, tabela, false, null, aliasTemp);
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".texto()", e);
				Util.mensagem(this, msg);
			}
		}
	}

	private void pesquisa(Referencia selecionado, boolean abrirDialogo) {
		pesquisa(selecionado, abrirDialogo, null);
	}

	private void pesquisa(Referencia selecionado, boolean abrirDialogo, String aliasTemp) {
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);

		Tabela tabela = selecionado.getTabela(formulario.getTabelas());
		formulario.textArea.setText(sql.select);
		Util.setContentTransfered(sql.select);

		if (abrirDialogo) {
			try {
				new DadosDialog(formulario, selecionado, tabela, true, null, aliasTemp);
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".texto()", e);
				Util.mensagem(this, msg);
			}
		}
	}

	void windowOpened() {
		arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
		arvore.setRootVisible(chkRaizVisivel.isSelected());
		splitPane.setDividerLocation(Util.DIVISAO2);
	}

	private class OuvinteArvore extends MouseAdapter {
		Referencia ultimoSelecionado;

		@Override
		public void mouseClicked(MouseEvent e) {
			TreePath path = arvore.getSelectionPath();

			if (path == null) {
				return;
			}

			if (path.getLastPathComponent() instanceof Referencia) {
				selecionado = (Referencia) path.getLastPathComponent();
				if (ultimoSelecionado != selecionado) {
					ultimoSelecionado = selecionado;
					table.setModel(new ModeloOrdenacao(new ModeloReferencia(selecionado)));
					table.getColumnModel().getColumn(ModeloReferencia.COLUNAS.length - 1)
							.setCellRenderer(new CellRenderer());
					table.ajustar(getGraphics());
				}
			}

			if (e.getClickCount() >= Util.DOIS && ultimoSelecionado != null) {
				itemPesquisaDialogoLimpo(ultimoSelecionado);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			if (!e.isPopupTrigger()) {
				return;
			}

			TreePath path = arvore.getSelectionPath();
			if (path == null) {
				return;
			}

			if (path.getLastPathComponent() instanceof Referencia) {
				selecionado = (Referencia) path.getLastPathComponent();
				popup.show(arvore, e.getX(), e.getY());
			}
		}
	}

	public static class CellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			ModeloOrdenacao modeloO = (ModeloOrdenacao) table.getModel();
			ModeloReferencia modelo = (ModeloReferencia) modeloO.getModel();

			CampoReferencia campoRef = modelo.getCampoReferencia(row);
			setBackground(campoRef.editavel ? Color.WHITE : Color.LIGHT_GRAY);
			setForeground(table.getForeground());

			return this;
		}
	}
}