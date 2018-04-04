package br.com.consultas.visao.modelo;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import br.com.consultas.Tabela;

public class ModeloFK implements TableModel {
	private final Vector<Vector<String>> listagem;
	private final Vector<String> nomeColunas;

	public ModeloFK(DatabaseMetaData dbmd, Tabela tabela) throws Exception {
		ResultSet rs = dbmd.getImportedKeys(null, null, tabela.getNome());
		ResultSetMetaData rsmd = rs.getMetaData();
		int qtdColunas = rsmd.getColumnCount();

		nomeColunas = new Vector<>();
		listagem = new Vector<>();

		nomeColunas(rsmd, qtdColunas);

		while (rs.next()) {
			Vector<String> registro = new Vector<>();

			for (int i = 1; i <= qtdColunas; i++) {
				registro.add(rs.getString(i));
			}

			listagem.add(registro);
		}
	}

	private void nomeColunas(ResultSetMetaData rsmd, int qtdColunas) throws Exception {
		for (int i = 1; i <= qtdColunas; i++) {
			nomeColunas.add(rsmd.getColumnLabel(i));
		}
	}

	public PK getPK(String nomeColuna) {
		if (nomeColuna == null) {
			return null;
		}

		PK resp = null;

		final int PK_TABLE_INDEX = nomeColunas.indexOf("PKTABLE_NAME");
		final int PK_COLUMN_NAME = nomeColunas.indexOf("PKCOLUMN_NAME");
		final int FK_COLUMN_NAME = nomeColunas.indexOf("FKCOLUMN_NAME");

		if (PK_TABLE_INDEX != -1 && PK_COLUMN_NAME != -1 && FK_COLUMN_NAME != -1) {
			int linha = getIndice(FK_COLUMN_NAME, nomeColuna);

			if (linha != -1) {
				Object tabela = getValueAt(linha, PK_TABLE_INDEX);
				Object campo = getValueAt(linha, PK_COLUMN_NAME);

				if (tabela != null && campo != null) {
					resp = new PK(tabela.toString(), campo.toString());
				}
			}
		}

		return resp;
	}

	private int getIndice(int coluna, String nome) {
		int resp = -1;
		int i = 0;

		for (Vector<String> registro : listagem) {
			String valor = registro.get(coluna);

			if (nome.equals(valor)) {
				resp = i;
			}

			i++;
		}

		return resp;
	}

	public Vector<String> getNomeColunas() {
		return nomeColunas;
	}

	@Override
	public int getRowCount() {
		return listagem.size();
	}

	@Override
	public int getColumnCount() {
		return nomeColunas.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return nomeColunas.elementAt(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Vector<String> registro = listagem.get(rowIndex);
		return registro.elementAt(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}