package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
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
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.Util;

public class ReferenciaDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JCheckBox chkRaizVisivel = new JCheckBox(Util.getString("label.raiz_visivel"),
			Util.getBooleanConfig("consultas.raiz_visivel"));
	private final JCheckBox chkLinhaRaiz = new JCheckBox(Util.getString("label.raiz_linha"),
			Util.getBooleanConfig("consultas.raiz_linha"));
	private final JMenuItem itemMeuSQLDialogo = new JMenuItem(Util.getString("label.gerar_dados_dialogo"));
	private final JMenuItem itemMeuSQLMemoria = new JMenuItem(Util.getString("label.gerar_dados_memoria"));
	private final JMenuItem itemSQLDialogo = new JMenuItem(Util.getString("label.gerar_sql_dialogo"));
	private final JMenuItem itemSQLMemoria = new JMenuItem(Util.getString("label.gerar_sql_memoria"));
	private final JPopupMenu popup = new JPopupMenu();
	private final Formulario formulario;
	private Referencia selecionado;
	private final JTree arvore;

	public ReferenciaDialog(final Formulario formulario, List<Referencia> referencias, Tabela tabela) {
		List<Referencia> caminhos = Util.pesquisarReferencias(referencias, tabela.getAlias().getValor());
		arvore = new JTree(new ModeloArvore(caminhos, Util.getString("label.caminho")));
		Util.expandir(arvore);
		arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setTitle(tabela.getNome() + " - " + tabela.getAlias().getValor());
		arvore.setCellRenderer(new TreeCellRenderer());
		arvore.addMouseListener(new OuvinteArvore());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		this.formulario = formulario;

		JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
		painelNorte.add(chkRaizVisivel);
		painelNorte.add(chkLinhaRaiz);
		add(BorderLayout.NORTH, painelNorte);

		add(BorderLayout.CENTER, new JScrollPane(arvore));
		add(BorderLayout.SOUTH, new PainelControle());
		setAlwaysOnTop(true);
		setSize(600, 400);
		setLocationRelativeTo(formulario);
		config();
		cfg();
		setVisible(true);
	}

	private void cfg() {
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
		popup.add(itemMeuSQLDialogo);
		popup.add(itemMeuSQLMemoria);
		popup.addSeparator();
		popup.add(itemSQLDialogo);
		popup.add(itemSQLMemoria);

		itemMeuSQLDialogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.tabelas);
				texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(formulario.tabelas), true, true);
			}
		});

		itemMeuSQLMemoria.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.tabelas);
				texto(sql.dados, sql.update, sql.delete, selecionado.getTabela(formulario.tabelas), true, false);
			}
		});

		itemSQLDialogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.tabelas);
				texto(sql.select, sql.update, sql.delete, selecionado.getTabela(formulario.tabelas), true, true);
			}
		});

		itemSQLMemoria.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQL sql = Util.criarSQL(selecionado, formulario.tabelas);
				texto(sql.select, sql.update, sql.delete, selecionado.getTabela(formulario.tabelas), true, false);
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

	void texto(String consulta, String atualizacao, String exclusao, Tabela tabela, boolean chkAreaTransferencia,
			boolean chkAbrirDialog) {

		if (chkAreaTransferencia) {
			Util.setContentTransfered(consulta);
		}

		if (chkAbrirDialog) {
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
}