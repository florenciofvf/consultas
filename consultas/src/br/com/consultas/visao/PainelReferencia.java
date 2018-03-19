package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.SQL;
import br.com.consultas.util.TreeCellRenderer;
import br.com.consultas.util.Util;
import br.com.consultas.visao.comp.Arvore;
import br.com.consultas.visao.comp.CheckBox;
import br.com.consultas.visao.comp.Label;
import br.com.consultas.visao.comp.PanelBorderLayout;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.dialog.CampoDialog;
import br.com.consultas.visao.dialog.DadosDialog;
import br.com.consultas.visao.dialog.ReferenciaPropDialog;
import br.com.consultas.visao.modelo.ModeloArvore;

public class PainelReferencia extends PanelBorderLayout {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkRaizVisivel = new CheckBox("label.raiz_visivel", "consultas.raiz_visivel");
	private final CheckBox chkLinhaRaiz = new CheckBox("label.raiz_linha", "consultas.raiz_linha");
	private final CheckBox chkTopoHierarquia = new CheckBox("label.topo_hierarquia", "false");
	private final Label labelStatus = new Label(Color.BLUE);
	private final Label labelValor = new Label(Color.RED);
	private final PainelReferenciaListener listener;
	private final List<Referencia> caminhosFiltro;
	private final List<Referencia> caminhos;
	private final Popup popup = new Popup();
	private final Formulario formulario;
	private Referencia selecionado;
	private final Arvore arvore;
	private final Tabela tabela;

	public PainelReferencia(Formulario formulario, Tabela tabela, PainelReferenciaListener listener) {
		caminhos = Util.pesquisarReferencias(formulario.getReferencias(), tabela, formulario.getTabelas());
		caminhosFiltro = Util.filtrarTopo(caminhos, tabela, formulario.getTabelas());

		arvore = new Arvore(new ModeloArvore(caminhos, Util.getString("label.caminho")));
		arvore.setCellRenderer(new TreeCellRenderer());
		arvore.addMouseListener(new OuvinteArvore());
		Util.expandirRetrair(arvore, true);
		this.formulario = formulario;
		this.listener = listener;
		this.tabela = tabela;

		PanelLeft panelNorte = new PanelLeft();

		if (Util.getBooleanConfig("config_arvore")) {
			panelNorte.adicionar(chkRaizVisivel, chkLinhaRaiz);
		}

		panelNorte.adicionar(chkTopoHierarquia, labelStatus, labelValor);

		add(BorderLayout.NORTH, panelNorte);
		add(BorderLayout.CENTER, new ScrollPane(arvore));

		cfg();
	}

	public void atualizarCampoID() {
		ModeloArvore modelo = (ModeloArvore) arvore.getModel();
		List<Referencia> caminhos = modelo.getReferencias();
		Util.atualizarCampoID(caminhos, formulario.getTabelas());
		arvore.setModel(new ModeloArvore(caminhos, Util.getString("label.caminho")));
		Util.expandirRetrair(arvore, true);
		formulario.atualizarCampoIDForm();
	}

	public void setInfo(String status, String valor) {
		labelStatus.setText(status);
		labelValor.setText(valor);
	}

	private void itemPesquisaDialogoLimpo(Referencia selecionado) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
		texto(sql.select, sql.update, sql.delete, tabela, true, true);
	}

	private void itemRegistrosDialogoLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
		texto(sql.dados, sql.update, sql.delete, tabela, true, true);
	}

	private void itemRegistrosMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
		texto(sql.dados, sql.update, sql.delete, tabela, true, false);
	}

	private void itemPesquisaDialogoAliasLimpo(String aliasTemp) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);
		texto(sql.select, sql.update, sql.delete, tabela, true, true);
	}

	private void itemPesquisaMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID();
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas());
		texto(sql.select, sql.update, sql.delete, tabela, true, false);
	}

	private void cfg() {
		popup.dialogo();
		popup.addSeparator();
		popup.memoria();
		popup.addSeparator();
		popup.campos();

		if (listener != null) {
			popup.addSeparator();
			popup.calculado();
		}

		popup.addSeparator();
		popup.propriedades();

		arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
		arvore.setRootVisible(chkRaizVisivel.isSelected());

		popup.itemRegistrosDialogoLimpo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemRegistrosDialogoLimpo();
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
				itemPesquisaDialogoLimpo(selecionado);
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
				String aliasTemp = Util.getAliasTemp(PainelReferencia.this, selecionado);

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
				String aliasTemp = Util.getAliasTemp(PainelReferencia.this, selecionado);

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
				atualizarCampoID();
			}
		});

		popup.itemLimparId.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selecionado.getTabela(formulario.getTabelas()).limparID();
				atualizarCampoID();
			}
		});

		popup.itemCampos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CampoDialog(formulario, selecionado.getTabela(formulario.getTabelas()));
			}
		});

		popup.itemPropriedades.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ReferenciaPropDialog(formulario, selecionado);
			}
		});

		popup.itemAgruparTotal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listener == null) {
					Util.mensagem(PainelReferencia.this, Util.getString("msg.nao_implementado"));
				} else if (selecionado.getPai() == null) {
					Util.mensagem(PainelReferencia.this, Util.getString("msg.objeto_deve_conter_pai"));
				} else {
					Tabela tabPai = selecionado.getPai().getTabela(formulario.getTabelas());

					if (tabela.getNome().equals(tabPai.getNome())) {
						try {
							listener.calcularTotal(selecionado);
						} catch (Exception ex) {
							String msg = Util
									.getStackTrace(PainelReferencia.this.getClass().getName() + ".calcularTotal()", ex);
							Util.mensagem(PainelReferencia.this, msg);
						}
					} else {
						Util.mensagem(PainelReferencia.this,
								Util.getString("msg.selecione_tabela_pai") + " " + selecionado.getAlias() + ".");
					}
				}
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

		chkTopoHierarquia.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setModel();
			}
		});
	}

	private void setModel() {
		if (chkTopoHierarquia.isSelected()) {
			arvore.setModel(new ModeloArvore(caminhosFiltro, Util.getString("label.caminho")));
			Util.expandirRetrair(arvore, true);
		} else {
			arvore.setModel(new ModeloArvore(caminhos, Util.getString("label.caminho")));
			Util.expandirRetrair(arvore, true);
		}
	}

	private void texto(String consulta, String atualizacao, String exclusao, Tabela tabela,
			boolean chkAreaTransferencia, boolean chkAbrirDialog) {
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
				}
			}

			if (e.getClickCount() > 1 && ultimoSelecionado != null) {
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
}