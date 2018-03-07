package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Util;

public class DadosDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JCheckBox chkAbrirDialogReferencia = new JCheckBox(Util.getString("label.abrir_dialog_referencia"),
			Util.getBooleanConfig("dados_dialog.abrir_dialog_referencia"));
	private final JCheckBox chkAbrirAbaReferencia = new JCheckBox(Util.getString("label.abrir_aba_referencia"),
			Util.getBooleanConfig("dados_dialog.abrir_aba_referencia"));
	private final JTextArea textAreaConsulta = new JTextArea();
	private final JTextArea textAreaAtualiza = new JTextArea();
	private final JTextArea textAreaExclusao = new JTextArea();
	private final JTabbedPane fichario = new JTabbedPane();
	private final PainelRegistros painelRegistros;
	private final PainelReferencia abaConsultas;
	private final Formulario formulario;
	private JTable table = new JTable();
	private final Tabela tabela;
	private final String TITLE;

	public DadosDialog(Formulario formulario, String consulta, String atualizacao, String exclusao, Tabela tabela)
			throws Exception {
		int largura = (int) (formulario.getWidth() * .8);
		this.tabela = tabela;
		this.formulario = formulario;
		TITLE = tabela != null ? tabela.getNome() : "";
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		processar(consulta, formulario.getGraphics());

		if (tabela != null) {
			painelRegistros = new PainelRegistros(largura);
			fichario.addTab(Util.getString("label.consultas"), painelRegistros);
		} else {
			painelRegistros = null;
			fichario.addTab(Util.getString("label.registros"), new JScrollPane(table));
		}

		fichario.addTab(Util.getString("label.consulta"), new JScrollPane(textAreaConsulta));
		fichario.addTab(Util.getString("label.atualiza"), new JScrollPane(textAreaAtualiza));
		fichario.addTab(Util.getString("label.exclusao"), new JScrollPane(textAreaExclusao));

		if (tabela != null) {
			abaConsultas = new PainelReferencia(formulario, formulario.referencias, tabela);
			fichario.addTab(Util.getString("label.consultas"), abaConsultas);
		} else {
			abaConsultas = null;
		}

		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, fichario);
		add(BorderLayout.SOUTH, new PainelControle());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		textAreaAtualiza.setText(atualizacao);
		textAreaConsulta.setText(consulta);
		textAreaExclusao.setText(exclusao);
		setAlwaysOnTop(true);
		setSize(largura, 500);
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

			if (painelRegistros != null) {
				painelRegistros.painelReferencia.atualizarCampoID();
			}

			if (abaConsultas != null) {
				abaConsultas.atualizarCampoID();
			}

			formulario.atualizarCampoIDForm();
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

		Util.ajustar(table, graphics);

		if (is != null) {
			for (int i : is) {
				if (i < dados.size()) {
					table.addRowSelectionInterval(i, i);
				}
			}
		}
	}

	class PainelRegistros extends JPanel {
		private static final long serialVersionUID = 1L;
		final PainelReferencia painelReferencia;
		JLabel labelStatus = new JLabel();
		JLabel labelValor = new JLabel();

		PainelRegistros(int largura) {
			super(new BorderLayout());

			JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
			painelNorte.add(chkAbrirDialogReferencia);
			painelNorte.add(chkAbrirAbaReferencia);
			labelStatus.setForeground(Color.BLUE);
			add(BorderLayout.NORTH, painelNorte);
			labelValor.setForeground(Color.RED);
			painelNorte.add(labelStatus);
			painelNorte.add(labelValor);

			JSplitPane splitPane = new JSplitPane();
			splitPane.setDividerLocation(largura / 2);
			splitPane.setOneTouchExpandable(true);
			splitPane.setContinuousLayout(true);
			splitPane.setLeftComponent(new JScrollPane(table));
			painelReferencia = new PainelReferencia(formulario, formulario.referencias, tabela);
			splitPane.setRightComponent(painelReferencia);

			add(BorderLayout.CENTER, splitPane);
		}

		void setInfo(String status, String valor) {
			labelStatus.setText(status);
			labelValor.setText(valor);
		}
	}

	class PainelControle extends JPanel {
		private static final long serialVersionUID = 1L;
		JButton buttonGetContent = new JButton(Util.getString("label.get_content"));
		JButton buttonUpdate = new JButton(Util.getString("label.execute_update"));
		JButton buttonDelete = new JButton(Util.getString("label.execute_delete"));
		JButton buttonQuery = new JButton(Util.getString("label.execute_query"));
		JButton buttonFechar = new JButton(Util.getString("label.fechar"));

		PainelControle() {
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
			} else if (chkAbrirAbaReferencia.isSelected()) {
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
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {

			valor = value;
			Campo campo = tabela.get(0);
			campo.setValor(valor.toString());

			painelRegistros.painelReferencia.atualizarCampoID();
			formulario.atualizarCampoIDForm();
			abaConsultas.atualizarCampoID();

			painelRegistros.setInfo(TITLE + "." + campo.getNome(), "[" + campo.getValor() + "]");

			Component c = getTableCellRendererComponent(table, value, isSelected, true, row, column);
			c.setForeground(table.getSelectionForeground());
			c.setBackground(table.getSelectionBackground());

			return c;
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class, l);
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class, l);
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
}