package br.com.consultas.util;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icones {
	public static final Icon DESC_NUMERO = criarImagem("desc_numero");
	public static final Icon ASC_NUMERO = criarImagem("asc_numero");
	public static final Icon DESC_TEXTO = criarImagem("desc_texto");
	public static final Icon CALCULADO = criarImagem("calculado");
	public static final Icon ATUALIZAR = criarImagem("atualizar");
	public static final Icon ASC_TEXTO = criarImagem("asc_texto");
	public static final Icon REFERENCIA = criarImagem("sucesso");
	public static final Icon EXECUTAR = criarImagem("executar");
	public static final Icon EXPANDIR = criarImagem("expandir");
	public static final Icon LARGURA = criarImagem("largura");
	public static final Icon RETRAIR = criarImagem("retrair");
	public static final Icon MEMORIA = criarImagem("memoria");
	public static final Icon DIALOGO = criarImagem("dialogo");
	public static final Icon TABELA = criarImagem("tabela");
	public static final Icon CAMPOS = criarImagem("campos");
	public static final Icon BAIXAR = criarImagem("baixar");
	public static final Icon LIMPAR = criarImagem("limpar");
	public static final Icon SAIR = criarImagem("sair");
	public static final Icon DML = criarImagem("dml");

	private static ImageIcon criarImagem(String nome) {
		URL url = Icones.class.getResource("/resources/" + nome + ".png");
		return new ImageIcon(url);
	}
}