package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.Util;

public class PainelReferencia extends JPanel {
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
	private final JLabel labelStatus = new JLabel();
	private final JLabel labelValor = new JLabel();
	private final Formulario formulario;
	private Referencia selecionado;
	private final JTree arvore;

	public PainelReferencia(final Formulario formulario, List<Referencia> referencias, Tabela tabela) {
		List<Referencia> caminhos = Util.pesquisarReferencias(referencias, tabela.getAlias().getValor());
		arvore = new JTree(new ModeloArvore(caminhos, Util.getString("label.caminho")));
		arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvore.setCellRenderer(new TreeCellRenderer());
		arvore.addMouseListener(new OuvinteArvore());
		setLayout(new BorderLayout());
		this.formulario = formulario;
		Util.expandir(arvore);

		JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelStatus.setForeground(Color.BLUE);
		add(BorderLayout.NORTH, painelNorte);
		labelValor.setForeground(Color.RED);
		painelNorte.add(chkRaizVisivel);
		painelNorte.add(chkLinhaRaiz);
		painelNorte.add(labelStatus);
		painelNorte.add(labelValor);

		add(BorderLayout.CENTER, new JScrollPane(arvore));
		config();
	}

	public void setInfo(String status, String valor) {
		labelStatus.setText(status);
		labelValor.setText(valor);
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