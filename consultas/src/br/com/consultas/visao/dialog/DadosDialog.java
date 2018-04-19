package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.util.CellColor;
import br.com.consultas.util.Icones;
import br.com.consultas.util.Persistencia;
import br.com.consultas.util.SQL;
import br.com.consultas.util.Util;
import br.com.consultas.visao.Formulario;
import br.com.consultas.visao.PainelAbas;
import br.com.consultas.visao.PainelReferencia;
import br.com.consultas.visao.PainelReferenciaListener;
import br.com.consultas.visao.Popup;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.CheckBox;
import br.com.consultas.visao.comp.Label;
import br.com.consultas.visao.comp.Menu;
import br.com.consultas.visao.comp.MenuItem;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.SplitPane;
import br.com.consultas.visao.comp.TabbedPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.comp.TextArea;
import br.com.consultas.visao.modelo.ModeloFK;
import br.com.consultas.visao.modelo.ModeloOrdenacao;
import br.com.consultas.visao.modelo.ModeloRSMD;
import br.com.consultas.visao.modelo.ModeloVazio;
import br.com.consultas.visao.modelo.PK;

public class DadosDialog extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final MenuItem itemLargura = new MenuItem("label.largura", Icones.LARGURA);
	private final MenuItem itemFechar = new MenuItem("label.fechar", Icones.SAIR);
	private final Menu menuArquivo = new Menu("label.arquivo");
	private final TabbedPane fichario = new TabbedPane();
	private final JMenuBar menuBar = new JMenuBar();

	private final PainelREGISTROSReferencia painelREGISTROSReferencia;
	private final PainelREFERENCIA painelREFERENCIA;
	private final PainelREGISTROS painelREGISTROS;
	private final PainelMETAINFO painelMETAINFO;
	private final PainelSELECT painelSELECT;
	private final PainelUPDATE painelUPDATE;
	private final PainelDELETE painelDELETE;
	private final PainelFK painelFK;

	private final Referencia selecionado;
	private final Formulario formulario;
	private final byte PRIMEIRA_ABA = 0;
	private final boolean pesquisa;
	private final Tabela tabela;
	private final String TITLE;
	private boolean um;

	public DadosDialog(Formulario formulario, Referencia selecionado, Tabela tabela, boolean pesquisa, String consulta,
			String aliasTemp) throws Exception {
		TITLE = tabela != null ? tabela.getNome() : "";

		this.selecionado = selecionado;
		this.formulario = formulario;
		this.pesquisa = pesquisa;
		this.tabela = tabela;

		painelMETAINFO = new PainelMETAINFO(this);
		painelSELECT = new PainelSELECT(this);
		painelUPDATE = new PainelUPDATE(this);
		painelDELETE = new PainelDELETE(this);

		if (tabela != null) {
			painelREGISTROSReferencia = new PainelREGISTROSReferencia(this);
			fichario.addTab("label.registros", painelREGISTROSReferencia);
			painelREGISTROS = null;
		} else {
			painelREGISTROS = new PainelREGISTROS(this);
			fichario.addTab("label.registros", painelREGISTROS);
			painelREGISTROSReferencia = null;
		}

		fichario.addTab("label.select", painelSELECT);
		fichario.addTab("label.update", painelUPDATE);
		fichario.addTab("label.delete", painelDELETE);
		fichario.addTab("label.meta_i", painelMETAINFO);

		if (tabela != null) {
			painelFK = new PainelFK(this);
			fichario.addTab("label.foreign_key", painelFK);

			painelREFERENCIA = new PainelREFERENCIA(this);
			fichario.addTab("label.referencias", painelREFERENCIA);
		} else {
			painelREFERENCIA = null;
			painelFK = null;
		}

		if (selecionado == null) {
			processar(consulta, formulario.getGraphics());
		} else {
			SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), aliasTemp);
			processar(pesquisa ? sql.select : sql.dados, formulario.getGraphics());
		}

		add(BorderLayout.CENTER, fichario);
		atualizarTextArea(consulta);
		montarMenu();

		setSize((int) (formulario.getWidth() * Util.DIVISAO3), 500);
		setLocationRelativeTo(formulario);

		cfg();
		setVisible(true);
		SwingUtilities.invokeLater(() -> toFront());
	}

	private void montarMenu() {
		menuBar.add(menuArquivo);
		menuArquivo.add(itemLargura);
		menuArquivo.addSeparator();
		menuArquivo.add(itemFechar);
		setJMenuBar(menuBar);

		itemFechar.addActionListener(e -> Util.fechar(DadosDialog.this));

		itemLargura.addActionListener(e -> {
			largura();

			SwingUtilities.invokeLater(() -> {
				if (painelREGISTROSReferencia != null) {
					painelREGISTROSReferencia.setLocationSplitPane();
				}
			});
		});
	}

	private void atualizarTextArea(String consulta) {
		if (selecionado == null) {
			painelSELECT.textArea.setText(consulta);
		} else {
			SQL sql = Util.criarSQL(selecionado, formulario.getTabelas(), null);

			painelSELECT.textArea.setText(pesquisa ? sql.select : sql.dados);
			painelUPDATE.textArea.setText(sql.update);
			painelDELETE.textArea.setText(sql.delete);
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

	private class PainelREGISTROS extends PainelAbas {
		private static final long serialVersionUID = 1L;
		private Table table = new Table(new ModeloOrdenacao(new ModeloVazio()));
		private final JTableHeader tableHeader;
		private Popup popup = new Popup();

		PainelREGISTROS(Dialogo dialogo) {
			super(dialogo, false);

			add(BorderLayout.CENTER, new ScrollPane(table));

			tableHeader = table.getTableHeader();
			cfg();
		}

		private void cfg() {
			popup.copiar(false);

			tableHeader.addMouseListener(new MouseAdapter() {
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

					int tableColuna = table.columnAtPoint(e.getPoint());
					int modelColuna = table.convertColumnIndexToModel(tableColuna);
					popup.setTag(modelColuna);
					popup.show(tableHeader, e.getX(), e.getY());
				}
			});

			popup.itemCopiar.addActionListener(e -> {
				List<String> resp = table.getValores(popup.getTag());
				Util.setContentTransfered(Util.getStringLista(resp, false));
			});

			popup.itemCopiarComAspas.addActionListener(e -> {
				List<String> resp = table.getValores(popup.getTag());
				Util.setContentTransfered(Util.getStringLista(resp, true));
			});
		}

		@Override
		public void executar() {
		}
	}

	private class PainelREGISTROSReferencia extends PainelAbas implements PainelReferenciaListener {
		private static final long serialVersionUID = 1L;
		private CheckBox chkAbrirDialogRef = new CheckBox("label.abrir_dialog_ref", "dados_dialog.abrir_dialog_ref");
		private CheckBox chkAbrirAbaRef = new CheckBox("label.abrir_aba_ref", "dados_dialog.abrir_aba_referencia");
		private Table table = new Table(new ModeloOrdenacao(new ModeloVazio()));
		private Label labelLimpar = new Label("label.limpar", Color.BLUE);
		private Label labelValor = new Label(Color.MAGENTA);
		private Label labelStatus = new Label(Color.BLACK);
		private SplitPane splitPane = new SplitPane();
		private PainelReferencia painelReferencia;
		private final JTableHeader tableHeader;
		private Popup popup = new Popup();
		private ScrollPane scroll;

		PainelREGISTROSReferencia(Dialogo dialogo) {
			super(dialogo, false);

			add(BorderLayout.NORTH,
					new PanelLeft(chkAbrirDialogRef, chkAbrirAbaRef, labelLimpar, labelStatus, labelValor));
			painelReferencia = new PainelReferencia(formulario, tabela, this);

			scroll = new ScrollPane(table);
			splitPane.setLeftComponent(scroll);
			splitPane.setRightComponent(painelReferencia);

			SwingUtilities.invokeLater(() -> {
				setLocationSplitPane();
			});

			labelLimpar.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					limpar(tabela);
				}
			});

			add(BorderLayout.CENTER, splitPane);
			setInfo("", "");

			tableHeader = table.getTableHeader();
			cfg();
		}

		@Override
		public void limparID(Tabela tabela) {
			if (DadosDialog.this.tabela != null && tabela != null) {
				if (DadosDialog.this.tabela.equals(tabela)) {
					limpar(tabela);
				}
			}
		}

		@Override
		public void limparCampos(Tabela tabela) {
			limparID(tabela);
		}

		private void setLocationSplitPane() {
			final int largura = (int) (DadosDialog.this.getWidth() * Util.DIVISAO3);
			splitPane.setDividerLocation(largura);
		}

		void setInfo(String status, String valor) {
			boolean visivel = !Util.ehVazio(valor);

			labelLimpar.setVisible(visivel);
			labelStatus.setVisible(visivel);
			labelValor.setVisible(visivel);

			labelStatus.setText(status);
			labelValor.setText("[" + valor + "]");
		}

		void finalScroll() {
			JScrollBar horizontalScrollBar = scroll.getHorizontalScrollBar();
			horizontalScrollBar.setValue(table.getWidth());
		}

		private void cfg() {
			popup.copiar(true);
			popup.mostrarRegistro();

			tableHeader.addMouseListener(new MouseAdapter() {
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

					int tableColuna = table.columnAtPoint(e.getPoint());
					int modelColuna = table.convertColumnIndexToModel(tableColuna);
					popup.setTag(modelColuna);
					popup.show(tableHeader, e.getX(), e.getY());
				}
			});

			popup.itemCopiar.addActionListener(e -> {
				List<String> resp = table.getValores(popup.getTag());
				Util.setContentTransfered(Util.getStringLista(resp, false));
			});

			popup.itemCopiarComAspas.addActionListener(e -> {
				List<String> resp = table.getValores(popup.getTag());
				Util.setContentTransfered(Util.getStringLista(resp, true));
			});

			popup.itemFiltrarCampo.addActionListener(e -> {
				String nomeColuna = table.getModel().getColumnName(popup.getTag());
				Campo campo = tabela.get(nomeColuna);

				if (campo == null) {
					campo = new Campo(nomeColuna, "");
					tabela.add(campo);
				}

				String valor = Util.getValorInputDialog(DadosDialog.this, tabela, campo);

				if (valor == null) {
					return;
				}

				campo.setValor(valor);
				painelREGISTROSReferencia.atualizarViews(tabela);

				Campo campoPK = tabela.get(0);
				valor = campoPK.getNome().equalsIgnoreCase(campo.getNome()) ? campo.getValor() : null;

				painelREGISTROSReferencia.setInfo(TITLE + "." + campo.getNome(), valor);
				painelSELECT.executar();
			});

			popup.itemMostrarRegistro.addActionListener(e -> {
				int[] is = table.getSelectedRows();

				if (is == null || is.length != 1) {
					Util.mensagem(this, Util.getString("label.selecione_uma_linha"));
					return;
				}

				int linha = is[0];
				int coluna = popup.getTag();
				Object valor = table.getModel().getValueAt(linha, coluna);

				if (valor == null || Util.ehVazio(valor.toString())) {
					Util.mensagem(this, Util.getString("label.vazio"));
					return;
				}

				String nomeColuna = table.getModel().getColumnName(popup.getTag());
				ModeloOrdenacao modeloOrdenacao = (ModeloOrdenacao) painelFK.table.getModel();
				ModeloFK modelo = (ModeloFK) modeloOrdenacao.getModel();
				PK pk = modelo.getPK(nomeColuna);

				if (pk == null) {
					Util.mensagem(this, Util.getString("label.sem_fk"));
					return;
				}

				final String string = "SELECT * FROM " + pk.getTabela() + " WHERE " + pk.getCampo() + " = "
						+ valor.toString();

				try {
					new DadosDialog(formulario, null, null, false, string, null);
				} catch (Exception ex) {
					String msg = Util.getStackTrace(getClass().getName() + ".itemMostrarRegistro()", ex);
					Util.mensagem(this, msg);
				}
			});
		}

		@Override
		public void executar() {
		}

		private void atualizarViews(Tabela tabela) {
			atualizarTextArea(null);

			formulario.atualizarCampoIDForm(tabela);
			painelReferencia.atualizarCampoID(false, tabela);
			painelREFERENCIA.painelReferencia.atualizarCampoID(false, tabela);
		}

		@Override
		public void calcularTotal(Referencia ref) throws Exception {
			Vector<Object[]> resp = Persistencia.getRegistrosAgrupados(ref, formulario.getTabelas(), null);

			String mensagemErro = Util.getMensagemErro(resp, table);

			if (!Util.ehVazio(mensagemErro)) {
				Util.mensagem(this, mensagemErro);
				return;
			}

			Vector<Object> dados = Util.criarDados(resp, (ModeloOrdenacao) table.getModel());

			table.addColuna(Util.getTituloCampoAgregado(ref, "COUNT"), dados, Boolean.TRUE);
			configTable(table);
			table.ajustar(getGraphics());

			if (painelREGISTROSReferencia != null) {
				SwingUtilities.invokeLater(() -> painelREGISTROSReferencia.finalScroll());
			}
		}

		@Override
		public void agruparColuna(Referencia ref, Campo campo) throws Exception {
			Vector<Object[]> resp = Persistencia.getRegistrosAgrupados(ref, formulario.getTabelas(), campo);

			String mensagemErro = Util.getMensagemErro(resp, table);

			if (!Util.ehVazio(mensagemErro)) {
				Util.mensagem(this, mensagemErro);
				return;
			}

			Vector<Object> dados = Util.criarDados(resp, (ModeloOrdenacao) table.getModel());

			table.addColuna(Util.getTituloCampoAgregado(ref, campo.getNome()), dados, Boolean.FALSE);
			configTable(table);
			table.ajustar(getGraphics());

			if (painelREGISTROSReferencia != null) {
				SwingUtilities.invokeLater(() -> painelREGISTROSReferencia.finalScroll());
			}
		}

		@Override
		public void agruparColuna(Referencia ref, Referencia pai, Campo campo) throws Exception {
			Vector<Object[]> resp = Persistencia.getRegistrosAgrupados(ref, pai, formulario.getTabelas(), campo);

			String mensagemErro = Util.getMensagemErro(resp, table);

			if (!Util.ehVazio(mensagemErro)) {
				Util.mensagem(this, mensagemErro);
				return;
			}

			Vector<Object> dados = Util.criarDados(resp, (ModeloOrdenacao) table.getModel());

			table.addColuna(Util.getTituloCampoAgregado(pai, ref, campo.getNome()), dados, Boolean.FALSE);
			configTable(table);
			table.ajustar(getGraphics());

			if (painelREGISTROSReferencia != null) {
				SwingUtilities.invokeLater(() -> painelREGISTROSReferencia.finalScroll());
			}
		}

		@Override
		public void calcularTotal(Referencia ref, Referencia pai, Campo campo) throws Exception {
			Vector<Object[]> resp = Persistencia.getRegistrosAgrupadosCOUNT(ref, pai, formulario.getTabelas(), campo);

			String mensagemErro = Util.getMensagemErro(resp, table);

			if (!Util.ehVazio(mensagemErro)) {
				Util.mensagem(this, mensagemErro);
				return;
			}

			Vector<Object> dados = Util.criarDados(resp, (ModeloOrdenacao) table.getModel());

			table.addColuna(Util.getTituloCampoAgregado(pai, ref, "COUNT"), dados, Boolean.TRUE);
			configTable(table);
			table.ajustar(getGraphics());

			if (painelREGISTROSReferencia != null) {
				SwingUtilities.invokeLater(() -> painelREGISTROSReferencia.finalScroll());
			}
		}
	}

	private class PainelSELECT extends PainelAbas {
		private static final long serialVersionUID = 1L;
		private Button buttonGetContent = new Button("label.get_content", Icones.BAIXAR);
		private TextArea textArea = new TextArea();

		PainelSELECT(Dialogo dialogo) {
			super(dialogo, true);

			painelControle.adicionar(buttonGetContent);
			buttonGetContent.addActionListener(e -> textArea.setText(Util.getContentTransfered()));

			add(BorderLayout.CENTER, textArea);
		}

		@Override
		public void executar() {
			String string = Util.getSQL(textArea.getText());

			if (string == null) {
				Util.mensagem(DadosDialog.this, Util.getString("labe.consulta_vazia"));
				return;
			}

			try {
				processar(string, getGraphics());
				fichario.setSelectedIndex(PRIMEIRA_ABA);

				formulario.atualizarCampoIDForm(tabela);

				if (painelREGISTROSReferencia != null) {
					painelREGISTROSReferencia.painelReferencia.atualizarCampoID(false, tabela);
				}

				if (painelREFERENCIA != null) {
					painelREFERENCIA.painelReferencia.atualizarCampoID(false, tabela);
				}
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".executeQuery()", e);
				Util.mensagem(DadosDialog.this, msg);
			}
		}
	}

	private class PainelUPDATE extends PainelAbas {
		private static final long serialVersionUID = 1L;
		private TextArea textArea = new TextArea();

		PainelUPDATE(Dialogo dialogo) {
			super(dialogo, true);
			buttonExecutar.setIcon(Icones.EXECUTAR2);
			add(BorderLayout.CENTER, textArea);
		}

		@Override
		public void executar() {
			String string = Util.getSQL(textArea.getText());

			if (string == null) {
				Util.mensagem(DadosDialog.this, Util.getString("labe.instrucao_vazia"));
				return;
			}

			if (Util.confirmarUpdate(DadosDialog.this)) {
				try {
					int i = Persistencia.executeUpdate(string);
					Util.mensagem(DadosDialog.this, Util.getString("label.sucesso") + " (" + i + ")");
				} catch (Exception e) {
					String msg = Util.getStackTrace(getClass().getName() + ".executeUpdate()", e);
					Util.mensagem(DadosDialog.this, msg);
				}
			}
		}
	}

	private class PainelDELETE extends PainelAbas {
		private static final long serialVersionUID = 1L;
		private TextArea textArea = new TextArea();

		PainelDELETE(Dialogo dialogo) {
			super(dialogo, true);
			buttonExecutar.setIcon(Icones.EXECUTAR2);
			add(BorderLayout.CENTER, textArea);
		}

		@Override
		public void executar() {
			String string = Util.getSQL(textArea.getText());

			if (string == null) {
				Util.mensagem(DadosDialog.this, Util.getString("labe.instrucao_vazia"));
				return;
			}

			if (Util.confirmarUpdate(DadosDialog.this)) {
				try {
					int i = Persistencia.executeUpdate(string);
					Util.mensagem(DadosDialog.this, Util.getString("label.sucesso") + " (" + i + ")");
				} catch (Exception e) {
					String msg = Util.getStackTrace(getClass().getName() + ".executeDelete()", e);
					Util.mensagem(DadosDialog.this, msg);
				}
			}
		}
	}

	private class PainelMETAINFO extends PainelAbas {
		private static final long serialVersionUID = 1L;
		private Table table = new Table(new ModeloOrdenacao(new ModeloVazio()));

		PainelMETAINFO(Dialogo dialogo) {
			super(dialogo, false);

			add(BorderLayout.CENTER, new ScrollPane(table));
		}

		@Override
		public void executar() {
		}
	}

	private class PainelFK extends PainelAbas {
		private static final long serialVersionUID = 1L;
		private Table table = new Table(new ModeloOrdenacao(new ModeloVazio()));

		PainelFK(Dialogo dialogo) {
			super(dialogo, false);

			add(BorderLayout.CENTER, new ScrollPane(table));
		}

		@Override
		public void executar() {
		}
	}

	private class PainelREFERENCIA extends PainelAbas implements PainelReferenciaListener {
		private static final long serialVersionUID = 1L;
		private final PainelReferencia painelReferencia;

		PainelREFERENCIA(Dialogo dialogo) {
			super(dialogo, false);

			painelReferencia = new PainelReferencia(formulario, tabela, this);
			add(BorderLayout.CENTER, painelReferencia);
		}

		@Override
		public void executar() {
		}

		@Override
		public void calcularTotal(Referencia ref) throws Exception {
			painelREGISTROSReferencia.calcularTotal(ref);
			fichario.setSelectedIndex(PRIMEIRA_ABA);
		}

		@Override
		public void agruparColuna(Referencia ref, Campo campo) throws Exception {
			painelREGISTROSReferencia.agruparColuna(ref, campo);
			fichario.setSelectedIndex(PRIMEIRA_ABA);
		}

		@Override
		public void agruparColuna(Referencia ref, Referencia pai, Campo campo) throws Exception {
			painelREGISTROSReferencia.agruparColuna(ref, pai, campo);
			fichario.setSelectedIndex(PRIMEIRA_ABA);
		}

		@Override
		public void calcularTotal(Referencia ref, Referencia pai, Campo campo) throws Exception {
			painelREGISTROSReferencia.calcularTotal(ref, pai, campo);
			fichario.setSelectedIndex(PRIMEIRA_ABA);
		}

		@Override
		public void limparCampos(Tabela tabela) {
			painelREGISTROSReferencia.limparCampos(tabela);
			fichario.setSelectedIndex(PRIMEIRA_ABA);
		}

		@Override
		public void limparID(Tabela tabela) {
			painelREGISTROSReferencia.limparID(tabela);
			fichario.setSelectedIndex(PRIMEIRA_ABA);
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
		public boolean shouldSelectCell(EventObject evento) {
			boolean multiplos = false;

			if (evento instanceof MouseEvent) {
				multiplos = ((MouseEvent) evento).getClickCount() >= 2;
			}

			if (!multiplos) {
				if (painelREGISTROSReferencia.chkAbrirDialogRef.isSelected()) {
					new ReferenciaDialog(formulario, tabela);

				} else if (painelREGISTROSReferencia.chkAbrirAbaRef.isSelected()) {
					fichario.setSelectedIndex(fichario.getTabCount() - 1);
				}
			} else {
				um = !um;

				if (!um) {
					limpar(tabela);
				}

				painelSELECT.executar();
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

			painelREGISTROSReferencia.atualizarViews(tabela);

			painelREGISTROSReferencia.setInfo(TITLE + "." + campo.getNome(), campo.getValor());

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

	private void processar(String string, Graphics graphics) throws Exception {
		Connection conn = Persistencia.getConnection();
		PreparedStatement psmt = conn.prepareStatement(Util.getSQL(string));
		ResultSet rs = psmt.executeQuery();

		coletar(conn, rs, graphics);

		rs.close();
		psmt.close();
	}

	private void coletar(Connection conn, ResultSet rs, Graphics graphics) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();

		ModeloRSMD modeloRSMD = new ModeloRSMD(rsmd);
		Vector<Vector<String>> dados = Persistencia.getDados(rs, rsmd.getColumnCount());

		titulo(dados);

		Table table = tabela != null ? painelREGISTROSReferencia.table : painelREGISTROS.table;

		int[] is = table.getSelectedRows();
		Vector<String> nomeColunas = modeloRSMD.getNomeColunas();
		ModeloOrdenacao modeloOrdenacao = new ModeloOrdenacao(new DefaultTableModel(dados, nomeColunas));
		table.setModel(modeloOrdenacao);

		if (tabela != null) {
			configTable(table);
		}

		table.ajustar(graphics);
		Util.selecionarLinhas(is, dados, table);
		modeloOrdenacao.configMapaTipoColuna(modeloRSMD.getTipoColunas());

		painelMETAINFO.table.setModel(new ModeloOrdenacao(modeloRSMD));
		painelMETAINFO.table.ajustar(graphics);

		if (tabela != null) {
			ModeloFK modeloFK = new ModeloFK(conn.getMetaData(), tabela);
			painelFK.table.setModel(new ModeloOrdenacao(modeloFK));
			painelFK.table.ajustar(graphics);
		}
	}

	private void largura() {
		int largura = formulario.getWidth() - 20;
		setSize(largura, getHeight());
		setLocation(formulario.getX() + 10, getY());
	}

	private void limpar(Tabela tabela) {
		um = false;
		tabela.limparID();
		Campo campo = tabela.get(0);
		painelREGISTROSReferencia.atualizarViews(tabela);
		painelREGISTROSReferencia.setInfo(TITLE + "." + campo.getNome(), "");
		painelSELECT.executar();
	}

	private void configTable(Table table) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn column = columnModel.getColumn(0);
		column.setCellRenderer(new CellColor());
		column.setCellEditor(new CellEditor());
	}

	private void titulo(Vector<Vector<String>> dados) {
		setTitle(Util.ehVazio(TITLE) ? "REGISTROS [" + dados.size() + "]"
				: TITLE + " - REGISTROS [" + dados.size() + "]");
	}
}