package br.com.consultas.visao.modelo;

import java.sql.ResultSetMetaData;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ModeloRSMD implements TableModel {
	private final Vector<Vector<String>> listagem;
	private final Vector<String> tipoColunas;
	private final Vector<String> nomeColunas;
	private final Vector<String> colunas;

	public ModeloRSMD(ResultSetMetaData rsmd) throws Exception {
		int qtdColunas = rsmd.getColumnCount();

		tipoColunas = new Vector<>();
		nomeColunas = new Vector<>();
		listagem = new Vector<>();
		colunas = getColunas();

		for (int i = 1; i <= qtdColunas; i++) {
			Vector<String> registro = new Vector<>();

			registro.add("" + rsmd.getColumnClassName(i));
			registro.add("" + rsmd.getColumnTypeName(i));
			registro.add("" + rsmd.getColumnLabel(i));
			registro.add("" + rsmd.getColumnName(i));
			registro.add("" + rsmd.getPrecision(i));
			registro.add("" + rsmd.isNullable(i));
			registro.add("" + rsmd.isAutoIncrement(i));
			registro.add("" + rsmd.isCaseSensitive(i));
			registro.add("" + rsmd.isSearchable(i));
			registro.add("" + rsmd.isCurrency(i));
			registro.add("" + rsmd.isSigned(i));
			registro.add("" + rsmd.getColumnDisplaySize(i));
			registro.add("" + rsmd.getScale(i));
			registro.add("" + rsmd.getSchemaName(i));
			registro.add("" + rsmd.getTableName(i));
			registro.add("" + rsmd.getCatalogName(i));
			registro.add("" + rsmd.getColumnType(i));
			registro.add("" + rsmd.isReadOnly(i));
			registro.add("" + rsmd.isWritable(i));
			registro.add("" + rsmd.isDefinitelyWritable(i));

			tipoColunas.add("" + rsmd.getColumnClassName(i));
			nomeColunas.add("" + rsmd.getColumnLabel(i));

			listagem.add(registro);
		}
	}

	private Vector<String> getColunas() {
		Vector<String> vector = new Vector<>();

		vector.add("getColumnClassName");
		vector.add("getColumnTypeName");
		vector.add("getColumnLabel");
		vector.add("getColumnName");
		vector.add("getPrecision");
		vector.add("isNullable");
		vector.add("isAutoIncrement");
		vector.add("isCaseSensitive");
		vector.add("isSearchable");
		vector.add("isCurrency");
		vector.add("isSigned");
		vector.add("getColumnDisplaySize");
		vector.add("getScale");
		vector.add("getSchemaName");
		vector.add("getTableName");
		vector.add("getCatalogName");
		vector.add("getColumnType");
		vector.add("isReadOnly");
		vector.add("isWritable");
		vector.add("isDefinitelyWritable");

		return vector;
	}

	public Vector<String> getTipoColunas() {
		return tipoColunas;
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
		return colunas.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colunas.elementAt(columnIndex);
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