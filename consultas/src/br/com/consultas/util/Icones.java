package br.com.consultas.util;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icones {
	public static final Icon REFERENCIA = criarImagem("sucesso");
	public static final Icon ATUALIZAR = criarImagem("atualizar");
	public static final Icon EXECUTAR = criarImagem("executar");
	public static final Icon EXPANDIR = criarImagem("expandir");
	public static final Icon LARGURA = criarImagem("largura");
	public static final Icon RETRAIR = criarImagem("retrair");
	public static final Icon TABELA = criarImagem("tabela");
	public static final Icon BAIXAR = criarImagem("baixar");
	public static final Icon LIMPAR = criarImagem("limpar");
	public static final Icon SAIR = criarImagem("sair");

	private static ImageIcon criarImagem(String nome) {
		URL url = Icones.class.getResource("/resources/" + nome + ".png");
		return new ImageIcon(url);
	}
}