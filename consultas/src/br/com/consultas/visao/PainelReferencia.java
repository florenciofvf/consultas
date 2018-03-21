package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.SQL;
import br.com.consultas.util.TreeCellRenderer;
import br.com.consultas.util.Util;
import br.com.consultas.visao.comp.Arvore;
import br.com.consultas.visao.comp.Button;
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

		Button expandir = new Button("label.expandir");
		expandir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Util.expandirRetrairTodos(arvore, true);
			}
		});

		Button retrair = new Button("label.retrair");
		retrair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Util.expandirRetrairTodos(arvore, false);
			}
		});

		panelNorte.adicionar(chkTopoHierarquia, labelStatus, labelValor, expandir, retrair);

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

	private void itemRegistrosDialogoLimpo() {
		Util.limparID(selecionado, formulario);
		atualizarCampoID();
		registros(selecionado, true);
	}

	private void itemPesquisaDialogoLimpo(Referencia selecionado) {
		Util.limparID(selecionado, formulario);
		atualizarCampoID();
		pesquisa(selecionado, true);
	}

	private void itemRegistrosMemoriaLimpo() {
		Util.limparID(selecionado, formulario);
		atualizarCampoID();
		registros(selecionado, false);
	}

	private void itemPesquisaMemoriaLimpo() {
		Util.limparID(selecionado, formulario);
		atualizarCampoID();
		pesquisa(selecionado, false);
	}

	private void itemPesquisaDialogoAliasLimpo(String aliasTemp) {
		Util.limparID(selecionado, formulario);
		atualizarCampoID();
		pesquisa(selecionado, true, aliasTemp);
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
			public void actionPerformed(ActionEvent e) {
				itemRegistrosDialogoLimpo();
			}
		});

		popup.itemRegistrosDialogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registros(selecionado, true);
			}
		});

		popup.itemRegistrosMemoriaLimpo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				itemRegistrosMemoriaLimpo();
			}
		});

		popup.itemRegistrosMemoria.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registros(selecionado, false);
			}
		});

		popup.itemPesquisaDialogoLimpo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				itemPesquisaDialogoLimpo(selecionado);
			}
		});

		popup.itemPesquisaDialogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pesquisa(selecionado, true);
			}
		});

		popup.itemPesquisaDialogoAlias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String aliasTemp = Util.getAliasTemp(PainelReferencia.this, selecionado);

				if (!Util.ehVazio(aliasTemp)) {
					pesquisa(selecionado, true, aliasTemp);
				}
			}
		});

		popup.itemPesquisaDialogoAliasLimpo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String aliasTemp = Util.getAliasTemp(PainelReferencia.this, selecionado);

				if (!Util.ehVazio(aliasTemp)) {
					itemPesquisaDialogoAliasLimpo(aliasTemp);
				}
			}
		});

		popup.itemPesquisaMemoriaLimpo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				itemPesquisaMemoriaLimpo();
			}
		});

		popup.itemPesquisaMemoria.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pesquisa(selecionado, false);
			}
		});

		popup.itemLimparCampos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selecionado.getTabela(formulario.getTabelas()).limparCampos();
				atualizarCampoID();
			}
		});

		popup.itemLimparId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selecionado.getTabela(formulario.getTabelas()).limparID();
				atualizarCampoID();
			}
		});

		popup.itemCampos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CampoDialog(formulario, selecionado.getTabela(formulario.getTabelas()));
			}
		});

		popup.itemPropriedades.addActionListener(new ActionListener() {
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

		popup.itemAgruparCampo.addActionListener(new ActionListener() {
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
							Tabela tabela = selecionado.getTabela(formulario.getTabelas());
							String nomeCampo = Util.getNomeCampo(PainelReferencia.this, tabela);
							if (!Util.ehVazio(nomeCampo)) {
								Campo campo = tabela.get(nomeCampo);
								listener.agruparColuna(selecionado, campo);
							}
						} catch (Exception ex) {
							String msg = Util
									.getStackTrace(PainelReferencia.this.getClass().getName() + ".agruparColuna()", ex);
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
			public void actionPerformed(ActionEvent e) {
				arvore.setRootVisible(chkRaizVisivel.isSelected());
			}
		});

		chkLinhaRaiz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			}
		});

		chkTopoHierarquia.addActionListener(new ActionListener() {
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

	private void registros(Referencia selecionado, boolean abrirDialogo) {
		registros(selecionado, abrirDialogo, null);
	}

	private void registros(Referencia selecionado, boolean abrirDialogo, String aliasTemp) {
		SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);

		Tabela tabela = selecionado.getTabela(formulario.getTabelas());
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