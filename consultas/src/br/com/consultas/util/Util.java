package br.com.consultas.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.xml.sax.Attributes;

import br.com.consultas.Campo;

public class Util {
	private static ResourceBundle bundle = ResourceBundle.getBundle("mensagens");

	public static void checarVazio(String s, String chave, boolean trim) {
		checarVazio(s, chave, trim, null);
	}

	public static void checarVazio(String s, String chave, boolean trim, String posMsg) {
		if (s == null) {
			throw new IllegalArgumentException(bundle.getString(chave) + (posMsg == null ? "" : posMsg));
		}

		if (trim && s.trim().length() == 0) {
			throw new IllegalArgumentException(bundle.getString(chave) + (posMsg == null ? "" : posMsg));
		}
	}

	public static String getString(String string) {
		return bundle.getString(string);
	}

	public static List<Campo> criarCampos(Attributes attributes) {
		List<Campo> resposta = new ArrayList<>();

		for(int i=0; i<attributes.getLength(); i++) {
			String nome = attributes.getQName(i);
			String valor = attributes.getValue(i);
			resposta.add(new Campo(nome, valor));
		}

		return resposta;
	}

	public static String getString(Attributes attributes, String nome, String padrao) {
		String string = attributes.getValue(nome);

		if(ehVazio(string)) {
			return padrao;
		}

		return string;
	}

	public static boolean getBoolean(Attributes attributes, String nome, boolean padrao) {
		String string = attributes.getValue(nome);

		if(ehVazio(string)) {
			return padrao;
		}

		return Boolean.parseBoolean(string);
	}

	public static int getInteger(Attributes attributes, String nome, int padrao) {
		String string = attributes.getValue(nome);

		if(ehVazio(string)) {
			return padrao;
		}

		return Integer.parseInt(string);
	}

	public static boolean ehVazio(String s) {
		return s == null || s.trim().length() == 0;
	}
}