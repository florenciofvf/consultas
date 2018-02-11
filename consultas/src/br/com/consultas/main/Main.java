package br.com.consultas.main;

import java.io.File;

import javax.swing.UIManager;

import br.com.consultas.visao.Formulario;

public class Main {
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new Formulario(new File("modelo.fvf"));
	}
}