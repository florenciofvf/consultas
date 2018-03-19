package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.SQL;
import br.com.consultas.util.TreeCellRenderer;
import br.com.consultas.util.Util;
import br.com.consultas.visao.comp.Arvore;
import br.com.consultas.visao.comp.CheckBox;
import br.com.consultas.visao.comp.PanelBorderLayout;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.SplitPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.dialog.CampoDialog;
import br.com.consultas.visao.dialog.DadosDialog;
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

		if (Util.getBooleanConfig("config_arvore")) {
			add(BorderLayout.NORTH, new PanelLeft(chkRaizVisivel, chkLinhaRaiz));
		}

		add(BorderLayout.CENTER, splitPane);

		cfg();
	}

	public void atualizarCampoID() {
		Util.atualizarCampoID(formulario.getReferencias(), formulario.getTabelas());
		Util.atualizarEstrutura(arvore, formulario.getTabelas(), true);
		arvore.repaint();
	}

	public void itemRegistrosDialogoLimpo(Referencia selecionado, Tabelas tabelas) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm();
		SQL sql = Util.criarSQL(selecionado, tabelas);
		texto(sql.dados, sql.update, sql.delete, tabela, true, true);
	}

	private void itemRegistrosMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
		texto(sql.dados, sql.update, sql.delete, tabela, true, false);
	}

	private void itemPesquisaDialogoLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
		texto(sql.select, sql.update, sql.delete, tabela, true, true);
	}

	private void itemPesquisaMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
		texto(sql.select, sql.update, sql.delete, tabela, true, false);
	}

	private void itemPesquisaDialogoAliasLimpo(String aliasTemp) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);
		texto(sql.select, sql.update, sql.delete, tabela, true, true);
	}

	private void cfg() {
		popup.dialogo();
		popup.addSeparator();
		popup.memoria();
		popup.addSeparator();
		popup.campos();
		popup.addSeparator();
		popup.dml();

		popup.itemRegistrosDialogoLimpo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemRegistrosDialogoLimpo(selecionado, formulario.getTabelas());
			}
		});

		popup.itemRegistrosDialogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
				texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(formulario.getTabelas()), true, true);
			}
		});

		popup.itemRegistrosMemoriaLimpo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemRegistrosMemoriaLimpo();
			}
		});

		popup.itemRegistrosMemoria.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
				texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(formulario.getTabelas()), true, false);
			}
		});

		popup.itemPesquisaDialogoLimpo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemPesquisaDialogoLimpo();
			}
		});

		popup.itemPesquisaDialogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
				texto(sql.select, sql.update, sql.delete, selecionado.getTabela(formulario.getTabelas()), true, true);
			}
		});

		popup.itemPesquisaDialogoAlias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aliasTemp = Util.getAliasTemp(PainelConsultas.this, selecionado);

				if (!Util.ehVazio(aliasTemp)) {
					SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);
					texto(sql.select, sql.update, sql.delete, selecionado.getTabela(formulario.getTabelas()), true,
							true);
				}
			}
		});

		popup.itemPesquisaDialogoAliasLimpo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aliasTemp = Util.getAliasTemp(PainelConsultas.this, selecionado);

				if (!Util.ehVazio(aliasTemp)) {
					itemPesquisaDialogoAliasLimpo(aliasTemp);
				}
			}
		});

		popup.itemPesquisaMemoriaLimpo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemPesquisaMemoriaLimpo();
			}
		});

		popup.itemPesquisaMemoria.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
				texto(sql.select, sql.update, sql.delete, selecionado.getTabela(formulario.getTabelas()), true, false);
			}
		});

		popup.itemLimparCampos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selecionado.getTabela(formulario.getTabelas()).limparCampos();
				formulario.atualizarCampoIDForm();
			}
		});

		popup.itemLimparId.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selecionado.getTabela(formulario.getTabelas()).limparID();
				formulario.atualizarCampoIDForm();
			}
		});

		popup.itemCampos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CampoDialog(formulario, selecionado.getTabela(formulario.getTabelas()));
			}
		});

		popup.itemUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.textArea.setText(selecionado.gerarUpdate(formulario.getTabelas()));
			}
		});

		popup.itemDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				formulario.textArea.setText(selecionado.gerarDelete(formulario.getTabelas()));
			}
		});

		chkRaizVisivel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				arvore.setRootVisible(chkRaizVisivel.isSelected());
			}
		});

		chkLinhaRaiz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			}
		});

		splitPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SplitPane splitPane = (SplitPane) evt.getSource();
				String propertyName = evt.getPropertyName();

				if (SplitPane.DIVIDER_LOCATION_PROPERTY.equals(propertyName)) {
					formulario.divisao(splitPane.getDividerLocation());
				}
			}
		});
	}

	private void texto(String consulta, String atualizacao, String exclusao, Tabela tabela,
			boolean chkAreaTransferencia, boolean chkAbrirDialog) {
		formulario.textArea.setText(consulta);

		if (chkAreaTransferencia) {
			Util.setContentTransfered(consulta);
		}

		if (chkAbrirDialog) {
			try {
				new DadosDialog(formulario, Util.getSQL(consulta), Util.getSQL(atualizacao), Util.getSQL(exclusao),
						tabela);
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

			if (e.getClickCount() > 1 && ultimoSelecionado != null) {
				itemRegistrosDialogoLimpo(ultimoSelecionado, formulario.getTabelas());
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
