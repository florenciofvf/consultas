package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Util;
import br.com.consultas.visao.modelo.CampoReferencia;
import br.com.consultas.visao.modelo.ModeloArvore;
import br.com.consultas.visao.modelo.ModeloBundle;
import br.com.consultas.visao.modelo.ModeloCampo;
import br.com.consultas.visao.modelo.ModeloReferencia;
import br.com.consultas.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JMenuItem itemLimparCampos = new JMenuItem(Util.getString("label.limpar_campos"));
	private final JMenuItem itemLimparIds = new JMenuItem(Util.getString("label.limpar_ids"));
	private final JMenuItem itemFechar = new JMenuItem(Util.getString("label.fechar"));
	private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private final JMenu menuArquivo = new JMenu(Util.getString("label.arquivo"));
	protected final List<Referencia> referencias = new ArrayList<>();
	private final ProgressoDialog progresso = new ProgressoDialog();
	private final JTabbedPane fichario = new JTabbedPane();
	private final JTextArea textArea = new JTextArea();
	protected final Tabelas tabelas = new Tabelas();
	private final JMenuBar menuBar = new JMenuBar();
	private final PainelTabelas painelRegistros;
	private final PainelTabelas painelDestaques;
	private final PainelConsultas abaConsultas;
	private final PainelTabelas painelTabelas;
	private final double DIVISAO2 = 0.6;
	private final double DIVISAO = 0.8;
	private int janelas;

	public Formulario(File file) throws Exception {
		setExtendedState(Formulario.MAXIMIZED_BOTH);
		XML.processar(file, tabelas, referencias);
		painelRegistros = new PainelTabelas(false, true);
		painelDestaques = new PainelTabelas(true, false);
		painelTabelas = new PainelTabelas(false, false);
		abaConsultas = new PainelConsultas();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setSize(500, 500);
		montarLayout();

		if (System.getProperty("os.name").startsWith("Mac OS")) {
			try {
				Class<?> classe = Class.forName("com.apple.eawt.FullScreenUtilities");
				Method method = classe.getMethod("setWindowCanFullScreen", Window.class, Boolean.TYPE);
				method.invoke(classe, this, true);
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".setWindowCanFullScreen()", e);
				Util.mensagem(this, msg);
			}
		}

		setVisible(true);
	}

	public void atualizarCampoIDForm() {
		abaConsultas.atualizarCampoID();
	}

	private void montarLayout() {
		setLayout(new BorderLayout());

		fichario.addTab(Util.getString("label.destaques"), painelDestaques);
		fichario.addTab(Util.getString("label.tabRegtros"), painelRegistros);
		fichario.addTab(Util.getString("label.tabelas"), painelTabelas);
		fichario.addTab(Util.getString("label.consultas"), abaConsultas);
		fichario.addTab(Util.getString("label.config"),
				new JScrollPane(new JTable(new ModeloBundle(Util.bundleConfig))));
		fichario.addTab(Util.getString("label.mensagens"),
				new JScrollPane(new JTable(new ModeloBundle(Util.bundleMsg))));

		splitPane.setLeftComponent(fichario);
		splitPane.setRightComponent(new JScrollPane(textArea));

		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		add(BorderLayout.CENTER, splitPane);

		add(BorderLayout.SOUTH, new PainelControle());

		setJMenuBar(menuBar);
		menuBar.add(menuArquivo);
		menuArquivo.add(itemLimparIds);
		menuArquivo.addSeparator();
		menuArquivo.add(itemLimparCampos);
		menuArquivo.addSeparator();
		menuArquivo.add(itemFechar);

		itemLimparCampos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Tabela t : tabelas.getTabelas()) {
					t.limparCampos();
				}
				Util.setEspecial(referencias);
				atualizarCampoIDForm();
			}
		});

		itemLimparIds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Tabela t : tabelas.getTabelas()) {
					t.limparID();
				}
				Util.setEspecial(referencias);
				atualizarCampoIDForm();
			}
		});

		itemFechar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				painelRegistros.windowOpened();
				painelDestaques.windowOpened();
				abaConsultas.windowOpened();
				painelTabelas.windowOpened();
			}
		});

		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if ((e.getNewState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							splitPane.setDividerLocation((int) (getHeight() * DIVISAO));
							painelRegistros.windowOpened();
							painelDestaques.windowOpened();
							abaConsultas.windowOpened();
							painelTabelas.windowOpened();
						}
					});
				}
			}
		});
	}

	void divisao(int i) {
		painelRegistros.splitPane.setDividerLocation(i);
		painelDestaques.splitPane.setDividerLocation(i);
		abaConsultas.splitPane.setDividerLocation(i);
		painelTabelas.splitPane.setDividerLocation(i);
	}

	void executeUpdate() {
		String string = Util.getSQL(textArea.getText());

		if (string == null) {
			return;
		}

		if (Util.confirmarUpdate(this)) {
			try {
				DadosDialog.executeUpdate(string);
				Util.mensagem(this, Util.getString("label.sucesso"));
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".executeUpdate()", e);
				Util.mensagem(this, msg);
			}
		}
	}

	void executeQuery() {
		String string = Util.getSQL(textArea.getText());

		if (string == null) {
			return;
		}

		try {
			new DadosDialog(this, string, null, null, null);
		} catch (Exception e) {
			String msg = Util.getStackTrace(getClass().getName() + ".executeQuery()", e);
			Util.mensagem(this, msg);
		}
	}

	void limpar() {
		textArea.setText("");
		textArea.requestFocus();
	}

	public void abrirJanela() {
		janelas++;
		setAlwaysOnTop(janelas <= 0);
	}

	public void fecharJanela() {
		janelas--;
		setAlwaysOnTop(janelas <= 0);
		if (janelas < 0) {
			janelas = 0;
		}
	}

	class PainelConsultas extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JCheckBox chkRaizVisivel = new JCheckBox(Util.getString("label.raiz_visivel"),
				Util.getBooleanConfig("consultas.raiz_visivel"));
		private final JCheckBox chkLinhaRaiz = new JCheckBox(Util.getString("label.raiz_linha"),
				Util.getBooleanConfig("consultas.raiz_linha"));
		private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		private final JTable tableCampos = new JTable(new ModeloReferencia(null));
		private final Popup popup = new Popup();
		private final JTree arvore;
		Referencia selecionado;

		PainelConsultas() {
			arvore = new JTree(new ModeloArvore(referencias, Util.getString("label.consultas")));
			arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arvore.setCellRenderer(new TreeCellRenderer());
			arvore.addMouseListener(new OuvinteArvore());
			setLayout(new BorderLayout());

			JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
			painelNorte.add(chkRaizVisivel);
			painelNorte.add(chkLinhaRaiz);
			add(BorderLayout.NORTH, painelNorte);

			splitPane.setLeftComponent(new JScrollPane(arvore));
			splitPane.setRightComponent(new JScrollPane(tableCampos));

			splitPane.setOneTouchExpandable(true);
			splitPane.setContinuousLayout(true);
			add(BorderLayout.CENTER, splitPane);
			config();
		}

		public void atualizarCampoID() {
			Util.atualizarCampoID(referencias, tabelas);
			/*
			 * Util.expandirRetrairID(arvore, true, tabelas);
			 * arvore.setModel(new ModeloArvore(referencias,
			 * Util.getString("label.consultas")));
			 */
			arvore.revalidate();
			arvore.repaint();
		}

		private void config() {
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
					Tabela tabela = selecionado.getTabela(tabelas);
					tabela.limparID();
					atualizarCampoIDForm();
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, tabela, true, true);
				}
			});

			popup.itemRegistrosDialogo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(tabelas), true, true);
				}
			});

			popup.itemRegistrosMemoriaLimpo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Tabela tabela = selecionado.getTabela(tabelas);
					tabela.limparID();
					atualizarCampoIDForm();
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, tabela, true, false);
				}
			});

			popup.itemRegistrosMemoria.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(tabelas), true, false);
				}
			});

			popup.itemPesquisaDialogoLimpo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Tabela tabela = selecionado.getTabela(tabelas);
					tabela.limparID();
					atualizarCampoIDForm();
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.select, sql.update, sql.delete, tabela, true, true);
				}
			});

			popup.itemPesquisaDialogo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.select, sql.update, sql.delete, selecionado.getTabela(tabelas), true, true);
				}
			});

			popup.itemPesquisaDialogoAlias.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas, Util.getAliasTemp(PainelConsultas.this, selecionado));
					texto(sql.select, sql.update, sql.delete, selecionado.getTabela(tabelas), true, true);
				}
			});

			popup.itemPesquisaDialogoAliasLimpo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Tabela tabela = selecionado.getTabela(tabelas);
					tabela.limparID();
					atualizarCampoIDForm();
					SQL sql = Util.criarSQL(selecionado, tabelas, Util.getAliasTemp(PainelConsultas.this, selecionado));
					texto(sql.select, sql.update, sql.delete, tabela, true, true);
				}
			});

			popup.itemPesquisaMemoriaLimpo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Tabela tabela = selecionado.getTabela(tabelas);
					tabela.limparID();
					atualizarCampoIDForm();
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.select, sql.update, sql.delete, tabela, true, false);
				}
			});

			popup.itemPesquisaMemoria.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.select, sql.update, sql.delete, selecionado.getTabela(tabelas), true, false);
				}
			});

			popup.itemLimparCampos.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selecionado.getTabela(tabelas).limparCampos();
					atualizarCampoIDForm();
				}
			});

			popup.itemLimparId.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selecionado.getTabela(tabelas).limparID();
					atualizarCampoIDForm();
				}
			});

			popup.itemCampos.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new CampoDialog(Formulario.this, selecionado.getTabela(tabelas));
				}
			});

			popup.itemUpdate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.setText(selecionado.gerarUpdate(tabelas));
				}
			});

			popup.itemDelete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.setText(selecionado.gerarDelete(tabelas));
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
					JSplitPane splitPane = (JSplitPane) evt.getSource();
					String propertyName = evt.getPropertyName();
					if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(propertyName)) {
						divisao(splitPane.getDividerLocation());
					}
				}
			});
		}

		void texto(String consulta, String atualizacao, String exclusao, Tabela tabela, boolean chkAreaTransferencia,
				boolean chkAbrirDialog) {
			textArea.setText(consulta);

			if (chkAreaTransferencia) {
				Util.setContentTransfered(consulta);
			}

			if (chkAbrirDialog) {
				try {
					new DadosDialog(Formulario.this, Util.getSQL(consulta), Util.getSQL(atualizacao),
							Util.getSQL(exclusao), tabela);
				} catch (Exception e) {
					String msg = Util.getStackTrace(getClass().getName() + ".texto()", e);
					Util.mensagem(this, msg);
				}
			}
		}

		void windowOpened() {
			arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			arvore.setRootVisible(chkRaizVisivel.isSelected());
			splitPane.setDividerLocation(DIVISAO2);
		}

		class OuvinteArvore extends MouseAdapter {
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
						tableCampos.setModel(new ModeloReferencia(selecionado));
						tableCampos.getColumnModel().getColumn(ModeloReferencia.COLUNAS.length - 1)
								.setCellRenderer(new CellRenderer());
					}
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

		class CellRenderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				ModeloReferencia modelo = (ModeloReferencia) table.getModel();
				CampoReferencia campoRef = modelo.getCampoReferencia(row);
				setBackground(campoRef.editavel ? Color.WHITE : Color.LIGHT_GRAY);
				setForeground(table.getForeground());

				return this;
			}
		}
	}

	class PainelTabelas extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JCheckBox chkRaizVisivel = new JCheckBox(Util.getString("label.raiz_visivel"),
				Util.getBooleanConfig("tabelas.raiz_visivel"));
		private final JCheckBox chkLinhaRaiz = new JCheckBox(Util.getString("label.raiz_linha"),
				Util.getBooleanConfig("tabelas.raiz_linha"));
		private final JTable tableCampos = new JTable(new ModeloCampo(Util.criarTabela()));
		private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		private final Popup popup = new Popup();
		private final JTree arvore;
		Referencia selecionado;

		PainelTabelas(boolean destaque, boolean comRegistros) {
			List<Referencia> referencias = Util.criarReferencias(tabelas.getTabelas());

			if (destaque) {
				Util.filtrarDestaques(referencias, tabelas);
			}

			try {
				progresso.exibir(referencias.size());
				DadosDialog.atualizarTotalRegistros(referencias, tabelas, progresso);
				progresso.esconder();
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".atualizarTotalRegistros()", e);
				Util.mensagem(this, msg);
			}

			if (comRegistros) {
				Util.filtrarRegistros(referencias, tabelas);
			}

			Util.ordenar(referencias);
			arvore = new JTree(new ModeloArvore(referencias, Util.getString("label.tabelas")));
			arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arvore.addMouseListener(new OuvinteArvore());
			setLayout(new BorderLayout());

			JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
			painelNorte.add(chkRaizVisivel);
			painelNorte.add(chkLinhaRaiz);
			add(BorderLayout.NORTH, painelNorte);

			splitPane.setLeftComponent(new JScrollPane(arvore));
			splitPane.setRightComponent(new JScrollPane(tableCampos));

			splitPane.setOneTouchExpandable(true);
			splitPane.setContinuousLayout(true);
			add(BorderLayout.CENTER, splitPane);
			config();
		}

		private void config() {
			popup.dialogoMeuSQL();
			popup.memoriaMeuSQL();
			popup.addSeparator();
			popup.campos();
			popup.addSeparator();
			popup.dml();

			popup.itemRegistrosDialogoLimpo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Tabela tabela = selecionado.getTabela(tabelas);
					tabela.limparID();
					atualizarCampoIDForm();
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, tabela, true, true);
				}
			});

			popup.itemRegistrosDialogo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(tabelas), true, true);
				}
			});

			popup.itemRegistrosMemoriaLimpo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Tabela tabela = selecionado.getTabela(tabelas);
					tabela.limparID();
					atualizarCampoIDForm();
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, tabela, true, false);
				}
			});

			popup.itemRegistrosMemoria.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(tabelas), true, false);
				}
			});

			popup.itemLimparCampos.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selecionado.getTabela(tabelas).limparCampos();
					atualizarCampoIDForm();
				}
			});

			popup.itemLimparId.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selecionado.getTabela(tabelas).limparID();
					atualizarCampoIDForm();
				}
			});

			popup.itemCampos.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new CampoDialog(Formulario.this, selecionado.getTabela(tabelas));
				}
			});

			popup.itemUpdate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.setText(selecionado.gerarUpdate(tabelas));
				}
			});

			popup.itemDelete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.setText(selecionado.gerarDelete(tabelas));
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
					JSplitPane splitPane = (JSplitPane) evt.getSource();
					String propertyName = evt.getPropertyName();
					if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(propertyName)) {
						divisao(splitPane.getDividerLocation());
					}
				}
			});
		}

		void texto(String consulta, String atualizacao, String exclusao, Tabela tabela, boolean chkAreaTransferencia,
				boolean chkAbrirDialog) {
			textArea.setText(consulta);

			if (chkAreaTransferencia) {
				Util.setContentTransfered(consulta);
			}

			if (chkAbrirDialog) {
				try {
					new DadosDialog(Formulario.this, Util.getSQL(consulta), Util.getSQL(atualizacao),
							Util.getSQL(exclusao), tabela);
				} catch (Exception e) {
					String msg = Util.getStackTrace(getClass().getName() + ".texto()", e);
					Util.mensagem(this, msg);
				}
			}
		}

		void windowOpened() {
			arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			arvore.setRootVisible(chkRaizVisivel.isSelected());
			splitPane.setDividerLocation(DIVISAO2);
		}

		class OuvinteArvore extends MouseAdapter {
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
						tableCampos.setModel(new ModeloCampo(selecionado.getTabela(tabelas)));
					}
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

	class PainelControle extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JButton buttonGetContent = new JButton(Util.getString("label.get_content"));
		private final JButton buttonUpdate = new JButton(Util.getString("label.execute_update"));
		private final JButton buttonQuery = new JButton(Util.getString("label.execute_query"));
		private final JButton buttonLimpar = new JButton(Util.getString("label.limpar"));
		private final JButton buttonFechar = new JButton(Util.getString("label.fechar"));
		private final JLabel labelStatus = new JLabel();

		PainelControle() {
			super(new FlowLayout(FlowLayout.LEFT));

			add(labelStatus);
			/* add(buttonFechar); */
			add(buttonLimpar);
			add(buttonUpdate);
			add(buttonQuery);
			add(buttonGetContent);

			labelStatus.setText(Util.getString("label.total_tabelas") + tabelas.getTotalTabelas());

			buttonLimpar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					limpar();
				}
			});

			buttonFechar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});

			buttonUpdate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					executeUpdate();
				}
			});

			buttonQuery.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					executeQuery();
				}
			});

			buttonGetContent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.setText(Util.getContentTransfered());
				}
			});
		}
	}
}