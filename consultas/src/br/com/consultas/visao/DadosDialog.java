package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Util;

public class DadosDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JCheckBox chkAbrirDialogReferencia = new JCheckBox(Util.getString("label.abrir_dialog_referencia"),
			Util.getBooleanConfig("dados_dialog.abrir_dialog_referencia"));
	private final JTextArea textAreaConsulta = new JTextArea();
	private final JTextArea textAreaAtualiza = new JTextArea();
	private final JTextArea textAreaExclusao = new JTextArea();
	private final JTabbedPane fichario = new JTabbedPane();
	private final Formulario formulario;
	private JTable table = new JTable();
	private final Tabela tabela;
	private final String TITLE;

	public DadosDialog(final Formulario formulario, String consulta, String atualizacao, String exclusao, Tabela tabela)
			throws Exception {
		this.formulario = formulario;
		this.tabela = tabela;
		TITLE = tabela != null ? tabela.getNome() : "";
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		processar(consulta, formulario.getGraphics());

		JPanel painelRegistros = new JPanel(new BorderLayout());
		JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
		painelNorte.add(chkAbrirDialogReferencia);
		painelRegistros.add(BorderLayout.NORTH, painelNorte);
		painelRegistros.add(BorderLayout.CENTER, new JScrollPane(table));

		fichario.addTab(Util.getString("label.registros"), painelRegistros);
		fichario.addTab(Util.getString("label.consulta"), new JScrollPane(textAreaConsulta));
		fichario.addTab(Util.getString("label.atualiza"), new JScrollPane(textAreaAtualiza));
		fichario.addTab(Util.getString("label.exclusao"), new JScrollPane(textAreaExclusao));
		if (tabela != null) {
			fichario.addTab(Util.getString("label.consultas"),
					new PainelReferencia(formulario, formulario.referencias, tabela));
		}

		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, fichario);
		add(BorderLayout.SOUTH, new PainelControle());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		textAreaAtualiza.setText(atualizacao);
		textAreaConsulta.setText(consulta);
		textAreaExclusao.setText(exclusao);
		setAlwaysOnTop(true);
		setSize((int) (formulario.getWidth() * .8), 500);
		setLocationRelativeTo(formulario);
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

	void fechar() {
		WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		EventQueue systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		systemEventQueue.postEvent(event);
	}

	class PainelControle extends JPanel {
		private static final long serialVersionUID = 1L;
		JButton buttonGetContent = new JButton(Util.getString("label.get_content"));
		JButton buttonUpdate = new JButton(Util.getString("label.execute_update"));
		JButton buttonDelete = new JButton(Util.getString("label.execute_delete"));
		JButton buttonQuery = new JButton(Util.getString("label.execute_query"));
		JButton buttonFechar = new JButton(Util.getString("label.fechar"));

		public PainelControle() {
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(buttonFechar);
			add(buttonUpdate);
			add(buttonDelete);
			add(buttonQuery);
			add(buttonGetContent);

			buttonFechar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fechar();
				}
			});

			buttonUpdate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					executeUpdate();
				}
			});

			buttonDelete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					executeDelete();
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
					textAreaConsulta.setText(Util.getContentTransfered());
				}
			});
		}
	}

	void executeUpdate() {
		String string = Util.getSQL(textAreaAtualiza.getText());
		if (string == null) {
			return;
		}
		if (Util.confirmarUpdate(this)) {
			try {
				int i = executeUpdate(string);
				Util.mensagem(this, Util.getString("label.sucesso") + " (" + i + ")");
			} catch (Exception e) {
				e.printStackTrace();
				Util.mensagem(this, Util.getString("label.erro"));
			}
		}
	}

	void executeDelete() {
		String string = Util.getSQL(textAreaExclusao.getText());
		if (string == null) {
			return;
		}
		if (Util.confirmarUpdate(this)) {
			try {
				int i = executeUpdate(string);
				Util.mensagem(this, Util.getString("label.sucesso") + " (" + i + ")");
			} catch (Exception e) {
				e.printStackTrace();
				Util.mensagem(this, Util.getString("label.erro"));
			}
		}
	}

	void executeQuery() {
		String string = Util.getSQL(textAreaConsulta.getText());
		if (string == null) {
			return;
		}
		try {
			processar(string, getGraphics());
			fichario.setSelectedIndex(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Connection getConnection() throws Exception {
		Class.forName(Util.getStringConfig("driver"));
		Connection conn = DriverManager.getConnection(Util.getStringConfig("url"), Util.getStringConfig("login"),
				Util.getStringConfig("senha"));
		return conn;
	}

	private void processar(String string, Graphics graphics) throws Exception {
		Connection conn = getConnection();
		PreparedStatement psmt = conn.prepareStatement(string);
		ResultSet rs = psmt.executeQuery();
		dados(rs, graphics);
		rs.close();
		psmt.close();
		conn.close();
	}

	public static void atualizarTotalRegistros(List<Referencia> referencias, Tabelas tabelas) throws Exception {
		Connection conn = getConnection();

		for (Referencia ref : referencias) {
			PreparedStatement psmt = conn.prepareStatement(ref.getConsultaCount(tabelas));
			ResultSet rs = psmt.executeQuery();
			rs.next();
			ref.setTotalRegistros(rs.getInt("total"));
			rs.close();
			psmt.close();
		}

		conn.close();
	}

	public static int executeUpdate(String string) throws Exception {
		Connection conn = getConnection();
		PreparedStatement psmt = conn.prepareStatement(string);
		int i = psmt.executeUpdate();
		psmt.close();
		conn.close();
		return i;
	}

	private void dados(ResultSet rs, Graphics graphics) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
		int qtdColunas = rsmd.getColumnCount();

		Vector<String> colunas = new Vector<>();
		for (int i = 0; i < qtdColunas; i++) {
			colunas.add(rsmd.getColumnLabel(i + 1));
		}

		Vector<Vector<String>> dados = new Vector<>();
		while (rs.next()) {
			Vector<String> registro = new Vector<>();
			for (int i = 0; i < qtdColunas; i++) {
				registro.add(rs.getString(i + 1));
			}
			dados.add(registro);
		}

		setTitle(Util.ehVazio(TITLE) ? "REGISTROS [" + dados.size() + "]"
				: TITLE + " - REGISTROS [" + dados.size() + "]");

		int[] is = table.getSelectedRows();
		table.setModel(new DefaultTableModel(dados, colunas));
		if (tabela != null) {
			TableColumnModel columnModel = table.getColumnModel();
			TableColumn column = columnModel.getColumn(0);
			column.setCellRenderer(new CellColor());
			column.setCellEditor(new CellEditor());
		}
		ajustar(table, graphics);

		if (is != null) {
			for (int i : is) {
				if (i < dados.size()) {
					table.addRowSelectionInterval(i, i);
				}
			}
		}
	}

	private void ajustar(JTable table, Graphics graphics) {
		DefaultTableColumnModel columnModel = (DefaultTableColumnModel) table.getColumnModel();
		FontMetrics fontMetrics = graphics.getFontMetrics();

		for (int icoluna = 0; icoluna < table.getColumnCount(); icoluna++) {
			String columnName = table.getColumnName(icoluna);
			int width = fontMetrics.stringWidth(columnName);

			for (int line = 0; line < table.getRowCount(); line++) {
				TableCellRenderer renderer = table.getCellRenderer(line, icoluna);
				Component component = renderer.getTableCellRendererComponent(table, table.getValueAt(line, icoluna),
						false, false, line, icoluna);
				width = Math.max(width, component.getPreferredSize().width);
			}

			TableColumn column = columnModel.getColumn(icoluna);
			column.setPreferredWidth(width + 7);
		}
	}

	class CellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
	}

	class CellEditor extends CellRenderer implements TableCellEditor {
		private static final long serialVersionUID = 1L;
		private EventListenerList listenerList = new EventListenerList();
		private ChangeEvent changeEvent = new ChangeEvent(this);
		private Object valor;

		@Override
		public Object getCellEditorValue() {
			return valor;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			if (chkAbrirDialogReferencia.isSelected()) {
				new ReferenciaDialog(formulario, formulario.referencias, tabela);
			} else {
				fichario.setSelectedIndex(fichario.getTabCount() - 1);
			}
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			fireEditingStopped();
			return true;
		}

		@Override
		public void cancelCellEditing() {
			fireEditingCanceled();
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class, l);
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class, l);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			valor = value;
			tabela.get(0).setValor(valor.toString());
			return super.getTableCellRendererComponent(table, value, isSelected, true, row, column);
		}

		private void fireEditingStopped() {
			Object[] listeners = listenerList.getListenerList();

			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == CellEditorListener.class) {
					((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
				}
			}
		}

		private void fireEditingCanceled() {
			Object[] listeners = listenerList.getListenerList();

			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == CellEditorListener.class) {
					((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
				}
			}
		}
	}

	class PainelReferencia extends JPanel {
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
		private Referencia selecionado;
		private final JTree arvore;

		public PainelReferencia(final Formulario formulario, List<Referencia> referencias, Tabela tabela) {
			List<Referencia> caminhos = Util.pesquisarReferencias(referencias, tabela.getAlias().getValor());
			arvore = new JTree(new ModeloArvore(caminhos, Util.getString("label.caminho")));
			Util.expandir(arvore);
			arvore.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			arvore.setCellRenderer(new TreeCellRenderer());
			arvore.addMouseListener(new OuvinteArvore());
			setLayout(new BorderLayout());

			JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
			painelNorte.add(chkRaizVisivel);
			painelNorte.add(chkLinhaRaiz);
			add(BorderLayout.NORTH, painelNorte);

			add(BorderLayout.CENTER, new JScrollPane(arvore));
			config();
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
}