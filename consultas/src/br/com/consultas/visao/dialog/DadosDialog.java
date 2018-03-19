package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
import br.com.consultas.util.CellColor;
import br.com.consultas.util.Util;
import br.com.consultas.visao.Formulario;
import br.com.consultas.visao.PainelReferencia;
import br.com.consultas.visao.PainelReferenciaListener;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.CheckBox;
import br.com.consultas.visao.comp.Label;
import br.com.consultas.visao.comp.PanelBorderLayout;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.SplitPane;
import br.com.consultas.visao.comp.TabbedPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.comp.TextArea;
import br.com.consultas.visao.modelo.ModeloOrdenacao;
import br.com.consultas.visao.modelo.ModeloRSMD;
import br.com.consultas.visao.modelo.ModeloVazio;

public class DadosDialog extends Dialogo implements PainelReferenciaListener {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkAbrirDialogReferencia = new CheckBox("label.abrir_dialog_referencia",
			"dados_dialog.abrir_dialog_referencia");
	private final CheckBox chkAbrirAbaReferencia = new CheckBox("label.abrir_aba_referencia",
			"dados_dialog.abrir_aba_referencia");
	private Table tableMetaInfo = new Table(new ModeloOrdenacao(new ModeloVazio()));
	private Table table = new Table(new ModeloOrdenacao(new ModeloVazio()));
	private final TextArea textAreaConsulta = new TextArea();
	private final TextArea textAreaAtualiza = new TextArea();
	private final TextArea textAreaExclusao = new TextArea();
	private final TabbedPane fichario = new TabbedPane();
	private final PainelRegistros painelRegistros;
	private final PainelReferencia abaConsultas;
	private final Formulario formulario;
	private final Tabela tabela;
	private final String TITLE;

	public DadosDialog(Formulario formulario, String consulta, String atualizacao, String exclusao, Tabela tabela)
			throws Exception {
		final int largura = (int) (formulario.getWidth() * .8);
		TITLE = tabela != null ? tabela.getNome() : "";

		this.formulario = formulario;
		this.tabela = tabela;

		processar(consulta, formulario.getGraphics());

		if (tabela != null) {
			painelRegistros = new PainelRegistros(largura);
			fichario.addTab("label.consultas", painelRegistros);
		} else {
			painelRegistros = null;
			fichario.addTab("label.registros", new ScrollPane(table));
		}

		fichario.addTab("label.consulta", textAreaConsulta);
		fichario.addTab("label.atualiza", textAreaAtualiza);
		fichario.addTab("label.exclusao", textAreaExclusao);
		fichario.addTab("label.meta_info", new ScrollPane(tableMetaInfo));

		if (tabela != null) {
			abaConsultas = new PainelReferencia(formulario, tabela, null);
			fichario.addTab("label.consultas", abaConsultas);
		} else {
			abaConsultas = null;
		}

		add(BorderLayout.CENTER, fichario);
		add(BorderLayout.SOUTH, new PainelControle());

		textAreaAtualiza.setText(atualizacao);
		textAreaConsulta.setText(consulta);
		textAreaExclusao.setText(exclusao);

		setSize(largura, 500);
		setLocationRelativeTo(formulario);

		cfg();
		setVisible(true);
	}

	private void cfg() {
		Util.setActionESC((JComponent) getContentPane(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Util.fechar(DadosDialog.this);
			}
		});

		Util.setWindowListener(this, formulario);
	}

	private void executeUpdate() {
		String string = Util.getSQL(textAreaAtualiza.getText());

		if (string == null) {
			return;
		}

		if (Util.confirmarUpdate(this)) {
			try {
				int i = executeUpdate(string);
				Util.mensagem(this, Util.getString("label.sucesso") + " (" + i + ")");
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".executeUpdate()", e);
				Util.mensagem(this, msg);
			}
		}
	}

	private void executeDelete() {
		String string = Util.getSQL(textAreaExclusao.getText());

		if (string == null) {
			return;
		}

		if (Util.confirmarUpdate(this)) {
			try {
				int i = executeUpdate(string);
				Util.mensagem(this, Util.getString("label.sucesso") + " (" + i + ")");
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".executeDelete()", e);
				Util.mensagem(this, msg);
			}
		}
	}

	private void executeQuery() {
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
			String msg = Util.getStackTrace(getClass().getName() + ".executeQuery()", e);
			Util.mensagem(this, msg);
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

	public static void atualizarTotalRegistros(List<Referencia> referencias, Tabelas tabelas, ProgressoDialog progresso)
			throws Exception {
		Connection conn = getConnection();

		int i = 0;

		for (Referencia ref : referencias) {
			PreparedStatement psmt = conn.prepareStatement(ref.getConsultaCount(tabelas));
			ResultSet rs = psmt.executeQuery();
			rs.next();
			ref.setTotalRegistros(rs.getInt("total"));
			progresso.atualizar(++i);
			rs.close();
			psmt.close();
		}

		conn.close();
	}

	public static Vector<Object[]> getRegistrosGroupBy(Referencia ref, Tabelas tabelas) throws Exception {
		Vector<Object[]> vector = new Vector<Object[]>();
		Connection conn = getConnection();

		String consulta = ref.getConsultaGroupByCount(tabelas);
		PreparedStatement psmt = conn.prepareStatement(consulta);
		ResultSet rs = psmt.executeQuery();

		while (rs.next()) {
			Object[] array = { rs.getString(1), rs.getString(2) };
			vector.add(array);
		}

		rs.close();
		psmt.close();
		conn.close();

		return vector;
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
		table.setModel(new ModeloOrdenacao(new DefaultTableModel(dados, colunas)));

		if (tabela != null) {
			TableColumnModel columnModel = table.getColumnModel();
			TableColumn column = columnModel.getColumn(0);
			column.setCellRenderer(new CellColor());
			column.setCellEditor(new CellEditor());
		}

		table.ajustar(graphics);

		if (is != null) {
			for (int i : is) {
				if (i < dados.size()) {
					table.addRowSelectionInterval(i, i);
				}
			}
		}

		tableMetaInfo.setModel(new ModeloOrdenacao(new ModeloRSMD(rsmd)));
		tableMetaInfo.ajustar(graphics);
	}

	@Override
	public void calcularTotal(Referencia ref) throws Exception {
		String titulo = "" + ref.getPai().getAlias() + " >> " + ref.getAlias();

		Vector<Object[]> resp = getRegistrosGroupBy(ref, formulario.getTabelas());

		Vector<Object> dados = new Vector<>();

		for (Object[] array : resp) {
			dados.add(array[1]);
		}

		table.addColuna(titulo, dados);
		table.ajustar(getGraphics());
	}

	private class PainelRegistros extends PanelBorderLayout {
		private static final long serialVersionUID = 1L;
		private final Label labelStatus = new Label(Color.BLUE);
		private final Label labelValor = new Label(Color.RED);
		private final SplitPane splitPane = new SplitPane();
		private final PainelReferencia painelReferencia;

		PainelRegistros(int largura) {
			splitPane.setDividerLocation(largura / 2);

			splitPane.setLeftComponent(new ScrollPane(table));
			painelReferencia = new PainelReferencia(formulario, tabela, DadosDialog.this);
			splitPane.setRightComponent(painelReferencia);

			add(BorderLayout.NORTH,
					new PanelLeft(chkAbrirDialogReferencia, chkAbrirAbaReferencia, labelStatus, labelValor));
			add(BorderLayout.CENTER, splitPane);
		}

		void setInfo(String status, String valor) {
			labelStatus.setText(status);
			labelValor.setText(valor);
		}
	}

	private class PainelControle extends PanelLeft {
		private static final long serialVersionUID = 1L;
		private final Button buttonGetContent = new Button("label.get_content");
		private final Button buttonUpdate = new Button("label.execute_update");
		private final Button buttonDelete = new Button("label.execute_delete");
		private final Button buttonQuery = new Button("label.execute_query");
		private final Button buttonLargura = new Button("label.largura");
		private final Button buttonFechar = new Button("label.fechar");

		PainelControle() {
			adicionar(buttonFechar, buttonUpdate, buttonDelete, buttonQuery, buttonGetContent, buttonLargura);

			buttonFechar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Util.fechar(DadosDialog.this);
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

			buttonLargura.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int largura = formulario.getWidth() - 20;
					DadosDialog.this.setSize(largura, DadosDialog.this.getHeight());
					DadosDialog.this.setLocation(formulario.getX() + 10, DadosDialog.this.getY());

					if (painelRegistros != null) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								painelRegistros.splitPane.setDividerLocation((int) (largura * Util.DIVISAO3));
							}
						});
					}
				}
			});
		}
	}

	private class CellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
	}

	private class CellEditor extends CellRenderer implements TableCellEditor {
		private static final long serialVersionUID = 1L;
		private final EventListenerList listenerList = new EventListenerList();
		private final ChangeEvent changeEvent = new ChangeEvent(this);
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
				new ReferenciaDialog(formulario, tabela);

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