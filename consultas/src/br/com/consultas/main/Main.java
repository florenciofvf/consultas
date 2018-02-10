package br.com.consultas.main;

import java.io.File;

import br.com.consultas.visao.Formulario;

public class Main {
	public static void main(String[] args) throws Exception {
		new Formulario(new File("modelo.fvf"));
	}
}