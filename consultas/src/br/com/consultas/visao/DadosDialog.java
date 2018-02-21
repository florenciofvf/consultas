package br.com.consultas.visao;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import br.com.consultas.util.Util;

public class DadosDialog extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTable table = new JTable();

	public DadosDialog(final Formulario formulario, String string) throws Exception {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		processar(string, formulario.getGraphics());
		add(new JScrollPane(table));
		setAlwaysOnTop(true);
		setSize(500, 500);
		setLocationRelativeTo(formulario);
		setVisible(true);
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

	private void processar(String string, Graphics graphics) throws Exception {
		Class.forName(Util.getString("driver"));
		Connection conn = DriverManager.getConnection(Util.getString("url"), Util.getString("login"), Util.getString("senha"));
		PreparedStatement psmt = conn.prepareStatement(string);
		ResultSet rs = psmt.executeQuery();
		dados(rs, graphics);
		rs.close();
		psmt.close();
		conn.close();
	}

	private void dados(ResultSet rs, Graphics graphics) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
		int qtdColunas = rsmd.getColumnCount();

		Vector<String> colunas = new Vector<>();
		for(int i=0; i<qtdColunas; i++) {
			colunas.add(rsmd.getColumnLabel(i+1));
		}

		Vector<Vector<String>> dados = new Vector<>();
		while(rs.next()) {
			Vector<String> registro = new Vector<>();
			for(int i=0; i<qtdColunas; i++) {
				registro.add(rs.getString(i+1));
			}
			dados.add(registro);
		}

		table.setModel(new DefaultTableModel(dados, colunas));
		ajustar(table, graphics);
	}

	private void ajustar(JTable table, Graphics graphics) {
		DefaultTableColumnModel columnModel = (DefaultTableColumnModel) table.getColumnModel();
		FontMetrics fontMetrics = graphics.getFontMetrics();

		for (int icoluna = 0; icoluna < table.getColumnCount(); icoluna++) {
			String columnName = table.getColumnName(icoluna);
			int width = fontMetrics.stringWidth(columnName);

			for (int line = 0; line < table.getRowCount(); line++) {
				TableCellRenderer renderer = table.getCellRenderer(line, icoluna);
				Component component = renderer.getTableCellRendererComponent(table, table.getValueAt(line, icoluna), false, false, line, icoluna);
				width = Math.max(width, component.getPreferredSize().width);
			}

			TableColumn column = columnModel.getColumn(icoluna);
			column.setPreferredWidth(width + 7);
		}
	}
}