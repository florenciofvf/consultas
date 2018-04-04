package br.com.consultas.visao.modelo;

public class PK {
	private final String tabela;
	private final String campo;

	public PK(String tabela, String campo) {
		this.tabela = tabela;
		this.campo = campo;
	}

	public String getTabela() {
		return tabela;
	}

	public String getCampo() {
		return campo;
	}
}