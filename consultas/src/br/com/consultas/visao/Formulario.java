package br.com.consultas.visao;

import java.awt.BorderLayout;
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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
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
	private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private final List<Referencia> referencias = new ArrayList<>();
	private final JTabbedPane fichario = new JTabbedPane();
	private final JTextArea textArea = new JTextArea();
	private final JLabel labelStatus = new JLabel();
	private final PainelConsultas painelConsultas;
	private final Tabelas tabelas = new Tabelas();
	private final PainelTabelas painelDestaques;
	private final PainelTabelas painelTabelas;
	private int janelas;

	public Formulario(File file) throws Exception {
		setExtendedState(Formulario.MAXIMIZED_BOTH);
		XML.processar(file, tabelas, referencias);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		painelDestaques = new PainelTabelas(true);
		painelTabelas = new PainelTabelas(false);
		painelConsultas = new PainelConsultas();
		setAlwaysOnTop(true);
		setSize(500, 500);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());

		fichario.addTab(Util.getString("label.destaques"), painelDestaques);
		fichario.addTab(Util.getString("label.consultas"), painelConsultas);
		fichario.addTab(Util.getString("label.tabelas"), painelTabelas);

		painelDestaques.config();
		painelConsultas.config();
		painelTabelas.config();

		splitPane.setLeftComponent(fichario);
		splitPane.setRightComponent(new JScrollPane(textArea));

		splitPane.setOneTouchExpandable(true);
		add(BorderLayout.CENTER, splitPane);

		JPanel panelSul = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelSul.add(labelStatus);
		panelSul.add(buttonLimpar);
		panelSul.add(buttonUpdate);
		panelSul.add(buttonQuery);
		add(BorderLayout.SOUTH, panelSul);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				splitPane.setDividerLocation(.80);
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

	void executeUpdate() {
		String string = Util.getSQL(textArea.getText());
		if (string == null) {
			return;
		}
		try {
			DadosDialog.executeUpdate(string);
			mensagem(Util.getString("label.sucesso"));
		} catch (Exception e) {
			e.printStackTrace();
			mensagem(Util.getString("label.erro"));
		}
	}

	void executeQuery() {
		String string = Util.getSQL(textArea.getText());
		if (string == null) {
			return;
		}
		try {
			new DadosDialog(Formulario.this, string, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void limpar() {
		textArea.setText("");
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

	private void mensagem(String string) {
		JOptionPane.showMessageDialog(this, string);
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
		final JMenuItem itemMeuSQL = new JMenuItem(Util.getString("label.gerar_dados"));
		final JMenuItem itemSQL = new JMenuItem(Util.getString("label.gerar_sql"));
		final JPopupMenu popup = new JPopupMenu();
		Referencia selecionado;
		final JTree arvore;

		PainelConsultas() {
			arvore = new JTree(new ModeloArvore(referencias, Util.getString("label.consultas")));
			arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arvore.addMouseListener(new OuvinteArvore());
			setLayout(new BorderLayout());

			JPanel panelNorte = new JPanel();
			panelNorte.add(chkAreaTransferencia);
			panelNorte.add(chkAbrirDialog);
			panelNorte.add(chkRaizVisivel);
			panelNorte.add(chkLinhaRaiz);
			add(BorderLayout.NORTH, panelNorte);

			add(BorderLayout.CENTER, new JScrollPane(arvore));
		}

		void config() {
			popup.add(itemMeuSQL);
			popup.addSeparator();
			popup.add(itemSQL);

			itemMeuSQL.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					texto(selecionado.gerarConsultaDados(tabelas), selecionado.getTabela(tabelas));
				}
			});

			itemSQL.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					texto(selecionado.gerarConsulta(tabelas), selecionado.getTabela(tabelas));
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
		}

		void texto(String string, Tabela tabela) {
			textArea.setText(string);

			if (chkAreaTransferencia.isSelected()) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard != null) {
					clipboard.setContents(new StringSelection(string), null);
				}
			}

			if (chkAbrirDialog.isSelected()) {
				try {
					new DadosDialog(Formulario.this, Util.getSQL(string), tabela);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		void windowOpened() {
			arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			arvore.setRootVisible(chkRaizVisivel.isSelected());
		}

		class OuvinteArvore extends MouseAdapter {
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
		final JPopupMenu popup = new JPopupMenu();
		Referencia selecionado;
		final JTree arvore;

		PainelTabelas(boolean destaque) {
			List<Referencia> referencias = Util.criarReferencias(tabelas.getTabelas());
			if (destaque) {
				Util.filtrarDestaques(referencias, tabelas);
			}
			try {
				DadosDialog.atualizarTotalRegistros(referencias, tabelas);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Util.ordenar(referencias);
			labelStatus.setText(Util.getString("label.total_tabelas") + referencias.size());
			arvore = new JTree(new ModeloArvore(referencias, Util.getString("label.tabelas")));
			arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arvore.addMouseListener(new OuvinteArvore());
			arvore.addMouseListener(new OuvinteArvore());
			setLayout(new BorderLayout());

			JPanel panelNorte = new JPanel();
			panelNorte.add(chkAreaTransferencia);
			panelNorte.add(chkAbrirDialog);
			panelNorte.add(chkRaizVisivel);
			panelNorte.add(chkLinhaRaiz);
			add(BorderLayout.NORTH, panelNorte);

			add(BorderLayout.CENTER, new JScrollPane(arvore));
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
					texto(selecionado.gerarConsultaDados(tabelas), selecionado.getTabela(tabelas));
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
		}

		void texto(String string, Tabela tabela) {
			textArea.setText(string);

			if (chkAreaTransferencia.isSelected()) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard != null) {
					clipboard.setContents(new StringSelection(string), null);
				}
			}

			if (chkAbrirDialog.isSelected()) {
				try {
					new DadosDialog(Formulario.this, Util.getSQL(string), tabela);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		void windowOpened() {
			arvore.setShowsRootHandles(chkLinhaRaiz.isSelected());
			arvore.setRootVisible(chkRaizVisivel.isSelected());
		}

		class OuvinteArvore extends MouseAdapter {
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