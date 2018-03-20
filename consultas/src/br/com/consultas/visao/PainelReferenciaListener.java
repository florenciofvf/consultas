package br.com.consultas.visao;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;

public interface PainelReferenciaListener {

	public void calcularTotal(Referencia ref) throws Exception;

	public void agruparColuna(Referencia ref, Campo campo) throws Exception;

}