package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import br.com.consultas.Campo;
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
import br.com.consultas.visao.dialog.CampoDialog;
import br.com.consultas.visao.dialog.DadosDialog;
import br.com.consultas.visao.dialog.ReferenciaPropDialog;
import br.com.consultas.visao.modelo.ModeloArvore;

public class PainelReferencia extends PanelBorderLayout {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkRaizVisivel = new CheckBox("label.raiz_visivel", "consultas.raiz_visivel");
	private final CheckBox chkLinhaRaiz = new CheckBox("label.raiz_linha", "consultas.raiz_linha");
	private final CheckBox chkTopoHierarquia = new CheckBox("label.topo_hierarquia", "false");
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
		boolean filtro = !caminhosFiltro.isEmpty();

		arvore = new Arvore(new ModeloArvore(filtro ? caminhosFiltro : caminhos, Util.getString("label.caminho")));
		arvore.setCellRenderer(new TreeCellRenderer());
		arvore.addMouseListener(new OuvinteArvore());
		chkTopoHierarquia.setSelected(filtro);

		this.formulario = formulario;
		this.listener = listener;
		this.tabela = tabela;

		PanelLeft panelNorte = new PanelLeft();

		if (Util.getBooleanConfig("config_arvore")) {
			panelNorte.adicionar(chkRaizVisivel, chkLinhaRaiz);
		}

		Button expandir = new Button("label.expandir", Icones.EXPANDIR);
		expandir.addActionListener(e -> Util.expandirRetrairTodos(arvore, true));

		Button retrair = new Button("label.retrair", Icones.RETRAIR);
		retrair.addActionListener(e -> Util.expandirRetrairTodos(arvore, false));

		panelNorte.adicionar(chkTopoHierarquia, expandir, retrair);

		add(BorderLayout.NORTH, panelNorte);
		add(BorderLayout.CENTER, new ScrollPane(arvore));

		cfg();
	}

	public void atualizarCampoID(boolean form, Tabela tabela) {
		ModeloArvore modelo = (ModeloArvore) arvore.getModel();
		List<Referencia> caminhos = modelo.getReferencias();

		Util.atualizarCampoID(caminhos, formulario.getTabelas());
		Util.atualizarEstrutura(arvore, formulario.getTabelas(), tabela);

		if (form) {
			formulario.atualizarCampoIDForm(tabela);
		}
	}

	private void itemRegistrosDialogoLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID(true, tabela);
		registros(selecionado, true);
	}

	private void itemPesquisaDialogoLimpo(Referencia selecionado) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID(true, tabela);
		pesquisa(selecionado, true);
	}

	private void itemRegistrosMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID(true, tabela);
		registros(selecionado, false);
	}

	private void itemPesquisaMemoriaLimpo() {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID(true, tabela);
		pesquisa(selecionado, false);
	}

	private void itemPesquisaDialogoAliasLimpo(String aliasTemp) {
		Tabela tabela = Util.limparID(selecionado, formulario);
		atualizarCampoID(true, tabela);
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

		popup.itemRegistrosDialogoLimpo.addActionListener(e -> itemRegistrosDialogoLimpo());

		popup.itemRegistrosDialogo.addActionListener(e -> registros(selecionado, true));

		popup.itemRegistrosMemoriaLimpo.addActionListener(e -> itemRegistrosMemoriaLimpo());

		popup.itemRegistrosMemoria.addActionListener(e -> registros(selecionado, false));

		popup.itemPesquisaSelecionados
				.addActionListener(e -> Util.pesquisaSelecionadosMemoria(selecionado, formulario.getTabelas()));

		popup.itemPesquisaDialogoLimpo.addActionListener(e -> itemPesquisaDialogoLimpo(selecionado));

		popup.itemPesquisaDialogo.addActionListener(e -> pesquisa(selecionado, true));

		popup.itemPesquisaDialogoAlias.addActionListener(e -> {
			String aliasTemp = Util.getAliasTemp(PainelReferencia.this, selecionado);

			if (!Util.ehVazio(aliasTemp)) {
				pesquisa(selecionado, true, aliasTemp);
			}
		});

		popup.itemPesquisaDialogoAliasLimpo.addActionListener(e -> {
			String aliasTemp = Util.getAliasTemp(PainelReferencia.this, selecionado);

			if (!Util.ehVazio(aliasTemp)) {
				itemPesquisaDialogoAliasLimpo(aliasTemp);
			}
		});

		popup.itemPesquisaMemoriaLimpo.addActionListener(e -> itemPesquisaMemoriaLimpo());

		popup.itemPesquisaMemoria.addActionListener(e -> pesquisa(selecionado, false));

		popup.itemLimparCampos.addActionListener(e -> {
			Tabela tabela = selecionado.getTabela(formulario.getTabelas());
			tabela.limparCampos();
			atualizarCampoID(true, tabela);

			if (listener != null) {
				listener.limparCampos(tabela);
			}
		});

		popup.itemLimparId.addActionListener(e -> {
			Tabela tabela = selecionado.getTabela(formulario.getTabelas());
			tabela.limparID();
			atualizarCampoID(true, tabela);

			if (listener != null) {
				listener.limparID(tabela);
			}
		});

		popup.itemCampos
				.addActionListener(e -> new CampoDialog(formulario, selecionado.getTabela(formulario.getTabelas())));

		popup.itemPropriedades.addActionListener(e -> new ReferenciaPropDialog(formulario, selecionado));

		popup.itemAgruparTotal.addActionListener(e -> {
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
						String msg = Util.getStackTrace(PainelReferencia.this.getClass().getName() + ".calcularTotal()",
								ex);
						Util.mensagem(PainelReferencia.this, msg);
					}
				} else {
					Util.mensagem(PainelReferencia.this,
							Util.getString("msg.selecione_tabela_pai") + " [" + selecionado.getAlias() + "]");
				}
			}
		});

		popup.itemAgruparCampo.addActionListener(e -> {
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
						String msg = Util.getStackTrace(PainelReferencia.this.getClass().getName() + ".agruparColuna()",
								ex);
						Util.mensagem(PainelReferencia.this, msg);
					}
				} else {
					Util.mensagem(PainelReferencia.this,
							Util.getString("msg.selecione_tabela_pai") + " [" + selecionado.getAlias() + "]");
				}
			}
		});

		popup.itemAgruparCampoPai.addActionListener(e -> {
			if (listener == null) {
				Util.mensagem(PainelReferencia.this, Util.getString("msg.nao_implementado"));
			} else if (selecionado.getPai() == null) {
				Util.mensagem(PainelReferencia.this, Util.getString("msg.objeto_deve_conter_pai"));
			} else {
				Referencia pai = Util.getPai(PainelReferencia.this, selecionado);

				if (pai != null) {
					try {
						Tabela tabela = selecionado.getTabela(formulario.getTabelas());
						String nomeCampo = Util.getNomeCampo(PainelReferencia.this, tabela);

						if (!Util.ehVazio(nomeCampo)) {
							Campo campo = tabela.get(nomeCampo);
							listener.agruparColuna(selecionado, pai, campo);
						}
					} catch (Exception ex) {
						String msg = Util.getStackTrace(PainelReferencia.this.getClass().getName() + ".agruparColuna()",
								ex);
						Util.mensagem(PainelReferencia.this, msg);
					}
				}
			}
		});

		popup.itemAgruparTotalPai.addActionListener(e -> {
			if (listener == null) {
				Util.mensagem(PainelReferencia.this, Util.getString("msg.nao_implementado"));
			} else if (selecionado.getPai() == null) {
				Util.mensagem(PainelReferencia.this, Util.getString("msg.objeto_deve_conter_pai"));
			} else {
				Referencia pai = Util.getPai(PainelReferencia.this, selecionado);

				if (pai != null) {
					try {
						Tabela tabela = selecionado.getTabela(formulario.getTabelas());
						String nomeCampo = Util.getNomeCampo(PainelReferencia.this, tabela);

						if (!Util.ehVazio(nomeCampo)) {
							Campo campo = tabela.get(nomeCampo);
							listener.calcularTotal(selecionado, pai, campo);
						}
					} catch (Exception ex) {
						String msg = Util.getStackTrace(PainelReferencia.this.getClass().getName() + ".agruparColuna()",
								ex);
						Util.mensagem(PainelReferencia.this, msg);
					}
				}
			}
		});

		chkRaizVisivel.addActionListener(e -> arvore.setRootVisible(chkRaizVisivel.isSelected()));

		chkLinhaRaiz.addActionListener(e -> arvore.setShowsRootHandles(chkLinhaRaiz.isSelected()));

		chkTopoHierarquia.addActionListener(e -> setModel());
	}

	private void setModel() {
		if (chkTopoHierarquia.isSelected()) {
			arvore.setModel(new ModeloArvore(caminhosFiltro, Util.getString("label.caminho")));
		} else {
			arvore.setModel(new ModeloArvore(caminhos, Util.getString("label.caminho")));
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
}