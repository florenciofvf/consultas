package br.com.consultas;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import br.com.consultas.util.Icones;
import br.com.consultas.util.Util;

public class Tabela {
	private final List<Campo> campos;
	private int totalRegistros;
	private final String nome;
	private boolean destaque;
	private Campo alias;
	private Icon icone;

	public Tabela(String nome) {
		Util.checarVazio(nome, "nome.tabela.invalido", true);
		campos = new ArrayList<>();
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void add(Campo campo) {
		if (campo.isAlias()) {
			Util.checarVazio(campo.getValor(), "valor.campo.invalido", true, nome + "[" + campo.getNome() + "]");
			alias = campo;

		} else if (campo.isDestaque()) {
			destaque = Boolean.parseBoolean(campo.getValor());

		} else if (campo.isIcone()) {
			icone = Icones.getIcon(campo.getValor());

		} else {
			campos.add(campo);
		}
	}

	public List<Campo> getCampos() {
		return campos;
	}

	public List<Campo> getCamposSelecionados() {
		List<Campo> campos = new ArrayList<>();

		for (Campo c : this.campos) {
			if (c.isSelecionado()) {
				campos.add(c);
			}
		}

		return campos;
	}

	public List<String> getNomeCampos() {
		List<String> nomes = new ArrayList<>();

		for (Campo c : campos) {
			nomes.add(c.getNome());
		}

		return nomes;
	}

	public void limparCampos() {
		for (Campo c : campos) {
			c.setValor("");
		}
	}

	public void limparSolenteLeitura() {
		for (Campo c : campos) {
			c.setSomenteLeitura(false);
		}
	}

	public void limparID() {
		get(0).setValor(null);
	}

	public Campo getAlias() {
		return alias;
	}

	public void setAlias(Campo alias) {
		this.alias = alias;
	}

	public boolean isDestaque() {
		return destaque;
	}

	public void setDestaque(boolean destaque) {
		this.destaque = destaque;
	}

	public Campo get(int i) {
		return campos.get(i);
	}

	public Campo get(String nome) {
		for (Campo c : campos) {
			if (c.getNome().equalsIgnoreCase(nome)) {
				return c;
			}
		}

		return null;
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

	public String getConsultaCount() {
		return "SELECT COUNT(*) AS total FROM " + getNome();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tabela) {
			return nome.equals(((Tabela) obj).getNome());
		}

		return false;
	}

	@Override
	public String toString() {
		return nome;
	}

	public Icon getIcone() {
		return icone;
	}
}