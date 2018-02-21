package br.com.consultas.main;

import java.io.File;

import javax.swing.UIManager;

import br.com.consultas.visao.Formulario;

public class Main {
	public static void main(String[] args) throws Exception {
		String os = System.getProperty("os.name");
		if(os == null) {
			os = "";
		}
		if(os.toLowerCase().indexOf("indows") >= 0 || os.toLowerCase().indexOf("mac") >= 0) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} else {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		new Formulario(new File("modelo.fvf"));
	}
}