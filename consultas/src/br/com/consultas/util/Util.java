package br.com.consultas.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.xml.sax.Attributes;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.visao.SQL;

public class Util {
	private static final String PREFIXO_FILTRO_CAMPO = "${";
	private static final String SUFIXO_FILTRO_CAMPO = "}";

	public static ResourceBundle bundleConfig = ResourceBundle.getBundle("config");
	public static ResourceBundle bundleMsg = ResourceBundle.getBundle("mensagens");

	public static void checarVazio(String s, String chave, boolean trim) {
		checarVazio(s, chave, trim, null);
	}

	public static void checarVazio(String s, String chave, boolean trim, String posMsg) {
		if (s == null) {
			throw new IllegalArgumentException(bundleMsg.getString(chave) + (posMsg == null ? "" : posMsg));
		}

		if (trim && s.trim().length() == 0) {
			throw new IllegalArgumentException(bundleMsg.getString(chave) + (posMsg == null ? "" : posMsg));
		}
	}

	public static String getString(String chave) {
		return bundleMsg.getString(chave);
	}

	public static String getStringConfig(String chave) {
		return bundleConfig.getString(chave);
	}

	public static boolean getBooleanConfig(String chave) {
		return Boolean.parseBoolean(getStringConfig(chave));
	}

	public static List<Campo> criarCampos(Attributes attributes) {
		List<Campo> resposta = new ArrayList<>();

		for (int i = 0; i < attributes.getLength(); i++) {
			String nome = attributes.getQName(i);
			String valor = attributes.getValue(i);
			resposta.add(new Campo(nome, valor));
		}

		return resposta;
	}

	public static String getString(Attributes attributes, String nome, String padrao) {
		String string = attributes.getValue(nome);

		if (ehVazio(string)) {
			return padrao;
		}

		return string;
	}

	public static boolean getBoolean(Attributes attributes, String nome, boolean padrao) {
		String string = attributes.getValue(nome);

		if (ehVazio(string)) {
			return padrao;
		}

		return Boolean.parseBoolean(string);
	}

	public static int getInteger(Attributes attributes, String nome, int padrao) {
		String string = attributes.getValue(nome);

		if (ehVazio(string)) {
			return padrao;
		}

		return Integer.parseInt(string);
	}

	public static boolean ehVazio(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean ehSomenteNumeros(String s) {
		if (ehVazio(s)) {
			return false;
		}
		for (char c : s.toCharArray()) {
			boolean ehNumero = c >= '0' && c <= '9';
			if (!ehNumero) {
				return false;
			}
		}
		return true;
	}

	public static List<Referencia> criarReferencias(List<Tabela> tabelas) {
		List<Referencia> resposta = new ArrayList<>();

		for (Tabela tabela : tabelas) {
			Referencia ref = Referencia.criarReferenciaDados(tabela);
			ref.setExibirTotalRegistros(true);
			resposta.add(ref);
		}

		return resposta;
	}

	public static void filtrarDestaques(List<Referencia> referencias, Tabelas tabelas) {
		Iterator<Referencia> it = referencias.iterator();

		while (it.hasNext()) {
			Referencia ref = it.next();
			if (!ref.isDestaque(tabelas)) {
				it.remove();
			}
		}
	}

	public static void filtrarRegistros(List<Referencia> referencias, Tabelas tabelas) {
		Iterator<Referencia> it = referencias.iterator();

		while (it.hasNext()) {
			Referencia ref = it.next();
			if (ref.getTotalRegistros() == 0) {
				it.remove();
			}
		}
	}

	public static List<Referencia> pesquisarReferencias(List<Referencia> referencias, String alias) {
		List<Referencia> container = new ArrayList<>();

		for (Referencia r : referencias) {
			if (r.getAlias2().equals(alias)) {
				r.setEspecial(true);
				container.add(r);
			} else {
				r.setEspecial(false);
				refs(container, r, alias);
			}
		}

		List<Referencia> resposta = new ArrayList<>();

		for (Referencia ref : container) {
			resposta.add(ref.clonarCaminho());
		}

		return resposta;
	}

	private static void refs(List<Referencia> resposta, Referencia ref, String alias) {
		for (Referencia r : ref.getReferencias()) {
			if (r.getAlias2().equals(alias)) {
				r.setEspecial(true);
				resposta.add(r);
			} else {
				r.setEspecial(false);
				refs(resposta, r, alias);
			}
		}
	}

	public static void ordenar(List<Referencia> referencias) {
		Collections.sort(referencias, new Comparador());
	}

	static class Comparador implements Comparator<Referencia> {
		@Override
		public int compare(Referencia o1, Referencia o2) {
			return o1.getAlias().compareTo(o2.getAlias());
		}
	}

	public static String getSQL(String s) {
		if (ehVazio(s)) {
			return null;
		}
		s = s.trim();
		if (s.endsWith(";")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	public static Tabela criarTabela() {
		return new Tabela("CAMPOS");
	}

	public static Referencia criarReferencia() {
		return new Referencia("CAMPOS", null, false, -1, null, -1, null, null);
	}

	public static String fragmentoFiltroCampo(Campo campo) {
		StringBuilder sb = new StringBuilder(campo.getNome());
		String valor = campo.getValor();

		if (ehVazio(valor)) {
			sb.append("=" + valor);
		} else {
			if (valor.startsWith(PREFIXO_FILTRO_CAMPO) && valor.endsWith(SUFIXO_FILTRO_CAMPO)) {
				int posIni = valor.indexOf(PREFIXO_FILTRO_CAMPO);
				int posFim = valor.lastIndexOf(SUFIXO_FILTRO_CAMPO);
				valor = valor.substring(posIni + PREFIXO_FILTRO_CAMPO.length(), posFim);
				sb.append(" " + valor.trim());
			} else {
				sb.append("=" + valor.trim());
			}
		}

		return sb.toString();
	}

	public static void mensagem(Component componente, String string) {
		JOptionPane.showMessageDialog(componente, string);
	}

	public static boolean confirmarUpdate(Component componente) {
		return JOptionPane.showConfirmDialog(componente, getString("label.confirmar_update"),
				getString("label.atencao"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}

	public static SQL criarSQL(Referencia ref, Tabelas tabelas) {
		SQL sql = new SQL();

		sql.dados = ref.gerarConsultaDados(tabelas);
		sql.select = ref.gerarConsulta(tabelas);
		sql.delete = ref.gerarDelete(tabelas);
		sql.update = ref.gerarUpdate(tabelas);

		return sql;
	}
}