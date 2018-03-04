package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Util;
import br.com.consultas.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JButton buttonUpdate = new JButton(Util.getString("label.execute_update"));
	private final JButton buttonQuery = new JButton(Util.getString("label.execute_query"));
	private final JButton buttonLimpar = new JButton(Util.getString("label.limpar"));
	private final JButton buttonFechar = new JButton(Util.getString("label.fechar"));
	private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private final List<Referencia> referencias = new ArrayList<>();
	private final JTabbedPane fichario = new JTabbedPane();
	private final JTextArea textArea = new JTextArea();
	private final JLabel labelStatus = new JLabel();
	protected final Tabelas tabelas = new Tabelas();
	private final PainelConsultas painelConsultas;
	private final PainelTabelas painelRegistros;
	private final PainelTabelas painelDestaques;
	private final PainelTabelas painelTabelas;
	private int janelas;

	public Formulario(File file) throws Exception {
		setExtendedState(Formulario.MAXIMIZED_BOTH);
		XML.processar(file, tabelas, referencias);
		painelRegistros = new PainelTabelas(false, true);
		painelDestaques = new PainelTabelas(true, false);
		painelTabelas = new PainelTabelas(false, false);
		painelConsultas = new PainelConsultas();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setSize(500, 500);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());

		fichario.addTab(Util.getString("label.destaques"), painelDestaques);
		fichario.addTab(Util.getString("label.tabRegtros"), painelRegistros);
		fichario.addTab(Util.getString("label.consultas"), painelConsultas);
		fichario.addTab(Util.getString("label.tabelas"), painelTabelas);
		fichario.addTab(Util.getString("label.config"),
				new JScrollPane(new JTable(new ModeloBundle(Util.bundleConfig))));
		fichario.addTab(Util.getString("label.mensagens"),
				new JScrollPane(new JTable(new ModeloBundle(Util.bundleMsg))));

		painelRegistros.config();
		painelDestaques.config();
		painelConsultas.config();
		painelTabelas.config();

		splitPane.setLeftComponent(fichario);
		splitPane.setRightComponent(new JScrollPane(textArea));

		splitPane.setOneTouchExpandable(true);
		add(BorderLayout.CENTER, splitPane);

		JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.LEFT));
		painelSul.add(labelStatus);
		painelSul.add(buttonFechar);
		painelSul.add(buttonLimpar);
		painelSul.add(buttonUpdate);
		painelSul.add(buttonQuery);
		add(BorderLayout.SOUTH, painelSul);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				splitPane.setDividerLocation(.80);
				painelRegistros.windowOpened();
				painelDestaques.windowOpened();
				painelConsultas.windowOpened();
				painelTabelas.windowOpened();
			}
		});

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
	}

	void divisao(int i) {
		painelRegistros.splitPane.setDividerLocation(i);
		painelDestaques.splitPane.setDividerLocation(i);
		painelConsultas.splitPane.setDividerLocation(i);
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
				e.printStackTrace();
				Util.mensagem(this, Util.getString("label.erro"));
			}
		}
	}

	void executeQuery() {
		String string = Util.getSQL(textArea.getText());
		if (string == null) {
			return;
		}
		try {
			new DadosDialog(Formulario.this, string, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void limpar() {
		textArea.setText("");
		textArea.requestFocus();
	}

	void abrirJanela() {
		janelas++;
		setAlwaysOnTop(janelas <= 0);
	}

	void fecharJanela() {
		janelas--;
		setAlwaysOnTop(janelas <= 0);
		if (janelas < 0) {
			janelas = 0;
		}
	}

	private class PainelConsultas extends JPanel {
		private static final long serialVersionUID = 1L;
		final JCheckBox chkAreaTransferencia = new JCheckBox(Util.getString("label.area_transferencia"),
				Util.getBooleanConfig("consultas.area_transferencia"));
		final JCheckBox chkAbrirDialog = new JCheckBox(Util.getString("label.abrir_dialog"),
				Util.getBooleanConfig("consultas.abrir_dialog"));
		final JCheckBox chkRaizVisivel = new JCheckBox(Util.getString("label.raiz_visivel"),
				Util.getBooleanConfig("consultas.raiz_visivel"));
		final JCheckBox chkLinhaRaiz = new JCheckBox(Util.getString("label.raiz_linha"),
				Util.getBooleanConfig("consultas.raiz_linha"));
		final JMenuItem itemDelete = new JMenuItem(Util.getString("label.gerar_delete"));
		final JMenuItem itemUpdate = new JMenuItem(Util.getString("label.gerar_update"));
		final JMenuItem itemMeuSQL = new JMenuItem(Util.getString("label.gerar_dados"));
		final JMenuItem itemCampos = new JMenuItem(Util.getString("label.campos"));
		final JMenuItem itemSQL = new JMenuItem(Util.getString("label.gerar_sql"));
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final JTable tableCampos = new JTable(new ModeloReferencia(null));
		final JPopupMenu popup = new JPopupMenu();
		Referencia selecionado;
		final JTree arvore;

		PainelConsultas() {
			arvore = new JTree(new ModeloArvore(referencias, Util.getString("label.consultas")));
			arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arvore.addMouseListener(new OuvinteArvore());
			setLayout(new BorderLayout());

			JPanel painelNorte = new JPanel();
			painelNorte.add(chkAreaTransferencia);
			painelNorte.add(chkAbrirDialog);
			painelNorte.add(chkRaizVisivel);
			painelNorte.add(chkLinhaRaiz);
			add(BorderLayout.NORTH, painelNorte);

			splitPane.setLeftComponent(new JScrollPane(arvore));
			splitPane.setRightComponent(new JScrollPane(tableCampos));

			splitPane.setOneTouchExpandable(true);
			add(BorderLayout.CENTER, splitPane);
		}

		void config() {
			popup.add(itemMeuSQL);
			popup.addSeparator();
			popup.add(itemSQL);
			popup.addSeparator();
			popup.add(itemCampos);
			popup.addSeparator();
			popup.add(itemUpdate);
			popup.addSeparator();
			popup.add(itemDelete);

			itemMeuSQL.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(tabelas));
				}
			});

			itemSQL.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.select, sql.update, sql.delete, selecionado.getTabela(tabelas));
				}
			});

			itemCampos.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new CampoDialog(Formulario.this, selecionado.getTabela(tabelas));
				}
			});

			itemUpdate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.setText(selecionado.gerarUpdate(tabelas));
				}
			});

			itemDelete.addActionListener(new ActionListener() {
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

		void texto(String consulta, String atualizacao, String exclusao, Tabela tabela) {
//			new ReferenciaDialog(Formulario.this, referencias, "pessoa");
			textArea.setText(consulta);

			if (chkAreaTransferencia.isSelected()) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard != null) {
					clipboard.setContents(new StringSelection(consulta), null);
				}
			}

			if (chkAbrirDialog.isSelected()) {
				try {
					new DadosDialog(Formulario.this, Util.getSQL(consulta), Util.getSQL(atualizacao),
							Util.getSQL(exclusao), tabela);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		void windowOpened() {
			arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			arvore.setRootVisible(chkRaizVisivel.isSelected());
			splitPane.setDividerLocation(.50);
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
				CampoReferencia campo = modelo.getCampoReferencia(row);
				setBackground(campo.editavel ? Color.WHITE : Color.LIGHT_GRAY);
				setForeground(table.getForeground());

				return this;
			}
		}
	}

	private class PainelTabelas extends JPanel {
		private static final long serialVersionUID = 1L;
		final JCheckBox chkAreaTransferencia = new JCheckBox(Util.getString("label.area_transferencia"),
				Util.getBooleanConfig("tabelas.area_transferencia"));
		final JCheckBox chkAbrirDialog = new JCheckBox(Util.getString("label.abrir_dialog"),
				Util.getBooleanConfig("tabelas.abrir_dialog"));
		final JCheckBox chkRaizVisivel = new JCheckBox(Util.getString("label.raiz_visivel"),
				Util.getBooleanConfig("tabelas.raiz_visivel"));
		final JCheckBox chkLinhaRaiz = new JCheckBox(Util.getString("label.raiz_linha"),
				Util.getBooleanConfig("tabelas.raiz_linha"));
		final JMenuItem itemDelete = new JMenuItem(Util.getString("label.gerar_delete"));
		final JMenuItem itemUpdate = new JMenuItem(Util.getString("label.gerar_update"));
		final JMenuItem itemMeuSQL = new JMenuItem(Util.getString("label.gerar_dados"));
		final JMenuItem itemCampos = new JMenuItem(Util.getString("label.campos"));
		final JTable tableCampos = new JTable(new ModeloCampo(Util.criarTabela()));
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final JPopupMenu popup = new JPopupMenu();
		Referencia selecionado;
		final JTree arvore;

		PainelTabelas(boolean destaque, boolean comRegistros) {
			List<Referencia> referencias = Util.criarReferencias(tabelas.getTabelas());
			if (destaque) {
				Util.filtrarDestaques(referencias, tabelas);
			}
			try {
				DadosDialog.atualizarTotalRegistros(referencias, tabelas);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (comRegistros) {
				Util.filtrarRegistros(referencias, tabelas);
			}
			Util.ordenar(referencias);
			labelStatus.setText(Util.getString("label.total_tabelas") + referencias.size());
			arvore = new JTree(new ModeloArvore(referencias, Util.getString("label.tabelas")));
			arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arvore.addMouseListener(new OuvinteArvore());
			setLayout(new BorderLayout());

			JPanel painelNorte = new JPanel();
			painelNorte.add(chkAreaTransferencia);
			painelNorte.add(chkAbrirDialog);
			painelNorte.add(chkRaizVisivel);
			painelNorte.add(chkLinhaRaiz);
			add(BorderLayout.NORTH, painelNorte);

			splitPane.setLeftComponent(new JScrollPane(arvore));
			splitPane.setRightComponent(new JScrollPane(tableCampos));

			splitPane.setOneTouchExpandable(true);
			add(BorderLayout.CENTER, splitPane);
		}

		void config() {
			popup.add(itemMeuSQL);
			popup.addSeparator();
			popup.add(itemCampos);
			popup.addSeparator();
			popup.add(itemUpdate);
			popup.addSeparator();
			popup.add(itemDelete);

			itemMeuSQL.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SQL sql = Util.criarSQL(selecionado, tabelas);
					texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(tabelas));
				}
			});

			itemCampos.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new CampoDialog(Formulario.this, selecionado.getTabela(tabelas));
				}
			});

			itemUpdate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.setText(selecionado.gerarUpdate(tabelas));
				}
			});

			itemDelete.addActionListener(new ActionListener() {
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

		void texto(String consulta, String atualizacao, String exclusao, Tabela tabela) {
			textArea.setText(consulta);

			if (chkAreaTransferencia.isSelected()) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard != null) {
					clipboard.setContents(new StringSelection(consulta), null);
				}
			}

			if (chkAbrirDialog.isSelected()) {
				try {
					new DadosDialog(Formulario.this, Util.getSQL(consulta), Util.getSQL(atualizacao),
							Util.getSQL(exclusao), tabela);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		void windowOpened() {
			arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			arvore.setRootVisible(chkRaizVisivel.isSelected());
			splitPane.setDividerLocation(.50);
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
}