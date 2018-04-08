package br.com.consultas.visao;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;

public interface PainelReferenciaListener {
	public void agruparColuna(Referencia ref, Referencia pai, Campo campo) throws Exception;

	public void calcularTotal(Referencia ref, Referencia pai, Campo campo) throws Exception;

	public void agruparColuna(Referencia ref, Campo campo) throws Exception;

	public void calcularTotal(Referencia ref) throws Exception;

	public void limparCampos(Tabela tabela);

	public void limparID(Tabela tabela);
}