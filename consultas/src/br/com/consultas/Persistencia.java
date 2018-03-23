package br.com.consultas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import br.com.consultas.util.Util;
import br.com.consultas.visao.dialog.ProgressoDialog;

public class Persistencia {

	public static Connection getConnection() throws Exception {
		Class.forName(Util.getStringConfig("driver"));

		String url = Util.getStringConfig("url");
		String usr = Util.getStringConfig("login");
		String psw = Util.getStringConfig("senha");

		Connection conn = DriverManager.getConnection(url, usr, psw);

		return conn;
	}

	public static int executeUpdate(String string) throws Exception {
		Connection conn = getConnection();

		PreparedStatement psmt = conn.prepareStatement(string);

		int i = psmt.executeUpdate();

		psmt.close();
		conn.close();

		return i;
	}

	public static void atualizarTotalRegistros(List<Referencia> referencias, Tabelas tabelas, ProgressoDialog progresso) throws Exception {
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

	public static Vector<Object[]> getRegistrosAgrupados(Referencia ref, Tabelas tabelas, Campo campo) throws Exception {
		Vector<Object[]> vector = new Vector<Object[]>();
		Connection conn = getConnection();

		String consulta = campo == null ? ref.getConsultaGroupByCount(tabelas) : ref.getConsultaAgregada(tabelas, campo);

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

	public static Vector<Vector<String>> getDados(ResultSet rs, int qtdColunas) throws Exception {
		Vector<Vector<String>> dados = new Vector<>();

		while (rs.next()) {
			Vector<String> registro = new Vector<>();

			for (int i = 0; i < qtdColunas; i++) {
				registro.add(rs.getString(i + 1));
			}

			dados.add(registro);
		}

		return dados;
	}
}