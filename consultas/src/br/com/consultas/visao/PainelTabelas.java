package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.tree.TreePath;

import br.com.consultas.Persistencia;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
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
import br.com.consultas.visao.dialog.CampoDialog;
import br.com.consultas.visao.dialog.DadosDialog;
import br.com.consultas.visao.modelo.ModeloArvore;
import br.com.consultas.visao.modelo.ModeloCampo;
import br.com.consultas.visao.modelo.ModeloOrdenacao;

public class PainelTabelas extends PanelBorderLayout {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkRaizVisivel = new CheckBox("label.raiz_visivel", "tabelas.raiz_visivel");
	private final Table table = new Table(new ModeloOrdenacao(new ModeloCampo(Util.criarTabela())));
	private final CheckBox chkLinhaRaiz = new CheckBox("label.raiz_linha", "tabelas.raiz_linha");
	protected final SplitPane splitPane = new SplitPane(SplitPane.HORIZONTAL_SPLIT);
	private final Button buttonAtualizar = new Button("label.atualizar");
	private final Popup popup = new Popup();
	private final Formulario formulario;
	private final boolean comRegistros;
	private Referencia selecionado;
	private final Arvore arvore;

	public PainelTabelas(Formulario formulario, boolean destaque, boolean comRegistros) {
		this.comRegistros = comRegistros;
		this.formulario = formulario;

		List<Referencia> referencias = Util.criarReferencias(formulario.getTabelas().getTabelas());

		if (destaque) {
			Util.filtrarDestaques(referencias, formulario.getTabelas());
		}

		try {
			formulario.progresso.exibir(referencias.size());
			Persistencia.atualizarTotalRegistros(referencias, formulario.getTabelas(), formulario.progresso);
			formulario.progresso.esconder();
		} catch (Exception e) {
			String msg = Util.getStackTrace(getClass().getName() + ".atualizarTotalRegistros()", e);
			Util.mensagem(this, msg);
		}

		if (comRegistros) {
			Util.filtrarRegistros(referencias, formulario.getTabelas());
		}

		Util.ordenar(referencias);

		arvore = new Arvore(new ModeloArvore(referencias, Util.getString("label.tabelas")));
		arvore.setCellRenderer(new TreeCellRenderer());
		arvore.addMouseListener(new OuvinteArvore());

		splitPane.setLeftComponent(new ScrollPane(arvore));
		splitPane.setRightComponent(new ScrollPane(table));

		PanelLeft panelNorte = new PanelLeft();

		if (Util.getBooleanConfig("config_arvore")) {
			panelNorte.adicionar(chkRaizVisivel, chkLinhaRaiz);
		}

		panelNorte.adicionar(buttonAtualizar);

		add(BorderLayout.NORTH, panelNorte);
		add(BorderLayout.CENTER, splitPane);

		cfg();
	}

	private void itemRegistrosDialogoLimpo(Referencia selecionado) {
		Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm();
		registros(selecionado, true);
	}

	private void itemRegistrosMemoriaLimpo() {
		Util.limparID(selecionado, formulario);
		formulario.atualizarCampoIDForm();
		registros(selecionado, false);
	}

	private void cfg() {
		popup.dialogoMeuSQL();
		popup.memoriaMeuSQL();
		popup.addSeparator();
		popup.campos();
		popup.addSeparator();
		popup.dml();

		popup.itemRegistrosDialogoLimpo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				itemRegistrosDialogoLimpo(selecionado);
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

		popup.itemPesquisaSelecionados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Util.pesquisaSelecionadosMemoria(selecionado, formulario.getTabelas());
			}
		});

		popup.itemLimparCampos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selecionado.getTabela(formulario.getTabelas()).limparCampos();
				formulario.atualizarCampoIDForm();
			}
		});

		popup.itemLimparId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selecionado.getTabela(formulario.getTabelas()).limparID();
				formulario.atualizarCampoIDForm();
			}
		});

		popup.itemCampos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CampoDialog(formulario, selecionado.getTabela(formulario.getTabelas()));
			}
		});

		popup.itemUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				formulario.textArea.setText(selecionado.gerarUpdate(formulario.getTabelas()));
			}
		});

		popup.itemDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				formulario.textArea.setText(selecionado.gerarDelete(formulario.getTabelas()));
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

		splitPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SplitPane splitPane = (SplitPane) evt.getSource();
				String propertyName = evt.getPropertyName();

				if (SplitPane.DIVIDER_LOCATION_PROPERTY.equals(propertyName)) {
					formulario.divisao(splitPane.getDividerLocation());
				}
			}
		});

		buttonAtualizar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (comRegistros) {
					try {
						List<Referencia> referencias = Util.criarReferencias(formulario.getTabelas().getTabelas());
						formulario.progresso.exibir(referencias.size());
						Persistencia.atualizarTotalRegistros(referencias, formulario.getTabelas(),
								formulario.progresso);
						formulario.progresso.esconder();
						Util.filtrarRegistros(referencias, formulario.getTabelas());
						Util.ordenar(referencias);
						arvore.setModel(new ModeloArvore(referencias, Util.getString("label.tabelas")));
					} catch (Exception ex) {
						String msg = Util.getStackTrace(getClass().getName() + ".atualizarTotalRegistros()", ex);
						Util.mensagem(PainelTabelas.this, msg);
					}
				} else {
					try {
						ModeloArvore modelo = (ModeloArvore) arvore.getModel();
						List<Referencia> referencias = modelo.getReferencias();
						formulario.progresso.exibir(referencias.size());
						Persistencia.atualizarTotalRegistros(referencias, formulario.getTabelas(),
								formulario.progresso);
						formulario.progresso.esconder();
						Util.atualizarEstrutura(arvore, formulario.getTabelas(), false);
						arvore.repaint();
					} catch (Exception ex) {
						String msg = Util.getStackTrace(getClass().getName() + ".atualizarTotalRegistros()", ex);
						Util.mensagem(PainelTabelas.this, msg);
					}
				}
			}
		});
	}

	private void registros(Referencia selecionado, boolean abrirDialogo) {
		pesquisa(selecionado, abrirDialogo, null);
	}

	private void pesquisa(Referencia selecionado, boolean abrirDialogo, String aliasTemp) {
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

	void windowOpened() {
		arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
		arvore.setRootVisible(chkRaizVisivel.isSelected());
		splitPane.setDividerLocation(Util.DIVISAO2);
		table.ajustar(getGraphics());
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
					table.setModel(
							new ModeloOrdenacao(new ModeloCampo(selecionado.getTabela(formulario.getTabelas()))));
					table.ajustar(getGraphics(), Util.LARGURA_ICONE_ORDENAR);
				}
			}

			if (e.getClickCount() > 1 && ultimoSelecionado != null) {
				itemRegistrosDialogoLimpo(ultimoSelecionado);
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