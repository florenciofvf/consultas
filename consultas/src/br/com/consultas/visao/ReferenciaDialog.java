package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.Util;

public class ReferenciaDialog extends JFrame {
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

	private final JPopupMenu popup = new JPopupMenu();
	private final Formulario formulario;
	private Referencia selecionado;
	private final JTree arvore;

	public ReferenciaDialog(final Formulario formulario, List<Referencia> referencias, String alias) {
		List<Referencia> caminhos = Util.pesquisarReferencias(referencias, alias);
		arvore = new JTree(new ModeloArvore(caminhos, Util.getString("label.caminho")));
		arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvore.setCellRenderer(new TreeCellRenderer());
		arvore.addMouseListener(new OuvinteArvore());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		this.formulario = formulario;
		setTitle(alias);

		JPanel painelNorte = new JPanel();
		painelNorte.add(chkAreaTransferencia);
		painelNorte.add(chkAbrirDialog);
		painelNorte.add(chkRaizVisivel);
		painelNorte.add(chkLinhaRaiz);
		add(BorderLayout.NORTH, painelNorte);

		add(BorderLayout.CENTER, new JScrollPane(arvore));
		add(BorderLayout.SOUTH, new PainelControle());
		setAlwaysOnTop(true);
		setSize(400, 400);
		setLocationRelativeTo(formulario);
		config();
		setVisible(true);

		((JComponent) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");
		((JComponent) getContentPane()).getActionMap().put("esc", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				fechar();
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				setState(NORMAL);
			}

			public void windowOpened(WindowEvent e) {
				formulario.abrirJanela();
			}

			public void windowClosing(WindowEvent e) {
				formulario.fecharJanela();
			}
		});
	}

	private void config() {
		// popup.add(itemMeuSQL);
		// popup.addSeparator();
		// popup.add(itemSQL);
		// popup.addSeparator();
		// popup.add(itemCampos);
		// popup.addSeparator();
		// popup.add(itemUpdate);
		// popup.addSeparator();
		// popup.add(itemDelete);

		itemMeuSQL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.tabelas);
				texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(formulario.tabelas));
			}
		});

		itemSQL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.tabelas);
				texto(sql.select, sql.update, sql.delete, selecionado.getTabela(formulario.tabelas));
			}
		});

		// itemCampos.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// new CampoDialog(Formulario.this, selecionado.getTabela(tabelas));
		// }
		// });

		// itemUpdate.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// textArea.setText(selecionado.gerarUpdate(tabelas));
		// }
		// });

		// itemDelete.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// textArea.setText(selecionado.gerarDelete(tabelas));
		// }
		// });

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

	void texto(String consulta, String atualizacao, String exclusao, Tabela tabela) {
		// textArea.setText(consulta);

		if (chkAreaTransferencia.isSelected()) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (clipboard != null) {
				clipboard.setContents(new StringSelection(consulta), null);
			}
		}

		if (chkAbrirDialog.isSelected()) {
			try {
				new DadosDialog(formulario, Util.getSQL(consulta), Util.getSQL(atualizacao), Util.getSQL(exclusao),
						tabela);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void fechar() {
		WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		EventQueue systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		systemEventQueue.postEvent(event);
	}

	class PainelControle extends JPanel {
		private static final long serialVersionUID = 1L;
		JButton buttonFechar = new JButton(Util.getString("label.fechar"));

		public PainelControle() {
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(buttonFechar);

			buttonFechar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fechar();
				}
			});
		}
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

	class TreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			// DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			// Object objeto = node.getUserObject();
			Object objeto = value;
			if (objeto instanceof Referencia) {
				Referencia ref = (Referencia) objeto;
				if (ref.isEspecial()) {
					setForeground(hasFocus ? Color.WHITE : Color.BLUE);
				} else {
					setForeground(hasFocus ? Color.WHITE : Color.BLACK);
				}
			}
			return this;
		}
	}
}