package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
import javax.swing.JScrollBar;
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
import br.com.consultas.util.SQL;
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
	private final CheckBox chkAbrirDialogRef = new CheckBox("label.abrir_dialog_ref", "dados_dialog.abrir_dialog_ref");
	private final CheckBox chkAbrirAbaRef = new CheckBox("label.abrir_aba_ref", "dados_dialog.abrir_aba_referencia");
	private Table tableMetaInfo = new Table(new ModeloOrdenacao(new ModeloVazio()));
	private Table table = new Table(new ModeloOrdenacao(new ModeloVazio()));
	private final TextArea textAreaConsulta = new TextArea();
	private final TextArea textAreaAtualiza = new TextArea();
	private final TextArea textAreaExclusao = new TextArea();
	private final TabbedPane fichario = new TabbedPane();
	private final PainelRegistros painelRegistros;
	private final PainelReferencia abaConsultas;
	private final Referencia selecionado;
	private final Formulario formulario;
	private final boolean pesquisa;
	private final Tabela tabela;
	private final String TITLE;
	private boolean um;

	public DadosDialog(Formulario formulario, Referencia selecionado, Tabela tabela, boolean pesquisa, String consulta,
			String aliasTemp) throws Exception {
		final int largura = (int) (formulario.getWidth() * .8);
		TITLE = tabela != null ? tabela.getNome() : "";

		this.selecionado = selecionado;
		this.formulario = formulario;
		this.pesquisa = pesquisa;
		this.tabela = tabela;

		if (selecionado == null) {
			processar(consulta, formulario.getGraphics());
		} else {
			SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);
			processar(pesquisa ? sql.select : sql.dados, formulario.getGraphics());
		}

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

		atualizarTextArea(consulta);

		setSize(largura, 500);
		setLocationRelativeTo(formulario);

		cfg();
		setVisible(true);
	}

	private void atualizarTextArea(String consulta) {
		if (selecionado == null) {
			textAreaConsulta.setText(consulta);
		} else {
			SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), null);

			textAreaConsulta.setText(pesquisa ? sql.select : sql.dados);
			textAreaAtualiza.setText(sql.update);
			textAreaExclusao.setText(sql.delete);
		}
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
			Util.mensagem(this, Util.getString("labe.consulta_vazia"));
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
			Util.mensagem(this, Util.getString("labe.consulta_vazia"));
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
			Util.mensagem(this, Util.getString("labe.consulta_vazia"));
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
		PreparedStatement psmt = conn.prepareStatement(Util.getSQL(string));
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

	public static Vector<Object[]> getRegistrosAgrupados(Referencia ref, Tabelas tabelas, Campo campo)
			throws Exception {
		Vector<Object[]> vector = new Vector<Object[]>();
		Connection conn = getConnection();

		String consulta = campo == null ? ref.getConsultaGroupByCount(tabelas)
				: ref.getConsultaAgregada(tabelas, campo);

		if (Util.getBooleanConfig("consultas.area_transferencia")) {
			Util.setContentTransfered(consulta);
		}

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
		ModeloOrdenacao modeloOrdenacao = new ModeloOrdenacao(new DefaultTableModel(dados, colunas));
		table.setModel(modeloOrdenacao);

		if (tabela != null) {
			configTable(table);
		}

		table.ajustar(graphics);

		if (is != null) {
			for (int i : is) {
				if (i < dados.size()) {
					table.addRowSelectionInterval(i, i);
				}
			}
		}

		ModeloRSMD modeloRSMD = new ModeloRSMD(rsmd);
		modeloOrdenacao.configMapaTipoColuna(modeloRSMD.getTipoColunas());

		tableMetaInfo.setModel(new ModeloOrdenacao(modeloRSMD));
		tableMetaInfo.ajustar(graphics);
	}

	private String getMensagemErro(Vector<Object[]> dados) {
		if (dados.isEmpty()) {
			return Util.getString("label.sem_registros_encontrados");
		}

		if (table.getModel().getRowCount() == 0) {
			return Util.getString("label.sem_registros_table");
		}

		if (table.getModel().getRowCount() < dados.size()) {
			return Util.getString("label.registros_vs_table") + " [" + table.getModel().getRowCount() + "/"
					+ dados.size() + "]";
		}

		return null;
	}

	private static String getTituloCampoAgregado(Referencia ref, String nome) {
		return ref.getPai().getAlias() + " >> " + ref.getAlias() + "." + nome;
	}

	@Override
	public void calcularTotal(Referencia ref) throws Exception {
		if (ref.isInverso()) {
			Util.mensagem(this, Util.getString("erro.relacionamento_inverso"));
			return;
		}

		Vector<Object[]> resp = getRegistrosAgrupados(ref, formulario.getTabelas(), null);

		String mensagemErro = getMensagemErro(resp);

		if (!Util.ehVazio(mensagemErro)) {
			Util.mensagem(this, mensagemErro);
			return;
		}

		Vector<Object> dados = Util.criarDados(resp, (ModeloOrdenacao)table.getModel());

		table.addColuna(getTituloCampoAgregado(ref, "COUNT"), dados, Boolean.TRUE);
		configTable(table);
		table.ajustar(getGraphics());

		if (painelRegistros != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					painelRegistros.finalScroll();
				}
			});
		}
	}

	@Override
	public void agruparColuna(Referencia ref, Campo campo) throws Exception {
		if (ref.isInverso()) {
			Util.mensagem(this, Util.getString("erro.relacionamento_inverso"));
			return;
		}

		Vector<Object[]> resp = getRegistrosAgrupados(ref, formulario.getTabelas(), campo);

		String mensagemErro = getMensagemErro(resp);

		if (!Util.ehVazio(mensagemErro)) {
			Util.mensagem(this, mensagemErro);
			return;
		}

		Vector<Object> dados = Util.criarDados(resp, (ModeloOrdenacao)table.getModel());

		table.addColuna(getTituloCampoAgregado(ref, campo.getNome()), dados, Boolean.FALSE);
		configTable(table);
		table.ajustar(getGraphics());

		if (painelRegistros != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					painelRegistros.finalScroll();
				}
			});
		}
	}

	private void configTable(Table table) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn column = columnModel.getColumn(0);
		column.setCellRenderer(new CellColor());
		column.setCellEditor(new CellEditor());
	}

	private class PainelRegistros extends PanelBorderLayout {
		private static final long serialVersionUID = 1L;
		private final Label labelStatus = new Label(Color.BLUE);
		private final Label labelValor = new Label(Color.RED);
		private final SplitPane splitPane = new SplitPane();
		private final PainelReferencia painelReferencia;
		private final ScrollPane scroll;

		PainelRegistros(int largura) {
			splitPane.setDividerLocation(largura / 2);

			scroll = new ScrollPane(table);
			splitPane.setLeftComponent(scroll);
			painelReferencia = new PainelReferencia(formulario, tabela, DadosDialog.this);
			splitPane.setRightComponent(painelReferencia);

			add(BorderLayout.NORTH, new PanelLeft(chkAbrirDialogRef, chkAbrirAbaRef, labelStatus, labelValor));
			add(BorderLayout.CENTER, splitPane);
		}

		void setInfo(String status, String valor) {
			labelStatus.setText(status);
			labelValor.setText(valor);
		}

		void finalScroll() {
			JScrollBar horizontalScrollBar = scroll.getHorizontalScrollBar();
			horizontalScrollBar.setValue(table.getWidth());
		}
	}

	private void limpar() {
		Campo campo = tabela.get(0);
		campo.setValor(null);
		atualizarViews();
	}

	private class PainelControle extends PanelLeft {
		private static final long serialVersionUID = 1L;
		private final Button buttonGetContent = new Button("label.get_content");
		private final Button buttonUpdate = new Button("label.execute_update");
		private final Button buttonDelete = new Button("label.execute_delete");
		private final Button buttonQuery = new Button("label.execute_query");
		private final Button buttonCopiarIds = new Button("label.copiar_id");
		private final Button buttonLimparId = new Button("label.limpar_id");
		private final Button buttonLargura = new Button("label.largura");
		private final Button buttonFechar = new Button("label.fechar");

		PainelControle() {
			adicionar(buttonFechar, buttonUpdate, buttonDelete, buttonQuery);

			if (tabela != null) {
				adicionar(buttonLimparId);
			}

			adicionar(buttonGetContent, buttonCopiarIds, buttonLargura);

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

			buttonLimparId.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					limpar();
				}
			});

			buttonGetContent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textAreaConsulta.setText(Util.getContentTransfered());
				}
			});

			buttonCopiarIds.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<String> resp = table.getIds(0);
					Util.setContentTransfered(Util.getStringLista(resp));
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

	private void atualizarViews() {
		atualizarTextArea(null);

		painelRegistros.painelReferencia.atualizarCampoID();
		formulario.atualizarCampoIDForm();
		abaConsultas.atualizarCampoID();
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
		public boolean shouldSelectCell(EventObject evento) {
			boolean multiplos = false;

			if (evento instanceof MouseEvent) {
				multiplos = ((MouseEvent) evento).getClickCount() >= 2;
			}

			if (!multiplos) {
				if (chkAbrirDialogRef.isSelected()) {
					new ReferenciaDialog(formulario, tabela);

				} else if (chkAbrirAbaRef.isSelected()) {
					fichario.setSelectedIndex(fichario.getTabCount() - 1);
				}
			} else {
				um = !um;

				if (!um) {
					limpar();
				}

				executeQuery();
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

			atualizarViews();

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