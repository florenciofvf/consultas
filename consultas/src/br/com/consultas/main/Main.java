package br.com.consultas.main;

import java.io.File;

import javax.swing.UIManager;

import br.com.consultas.util.Util;
import br.com.consultas.visao.Formulario;

public class Main {
	public static void main(String[] args) throws Exception {
		String os = System.getProperty("os.name");

		if (Util.ehVazio(os)) {
			os = "";
		}

		if (Util.getBooleanConfig("ui_manager")) {
			if (os.toLowerCase().indexOf("indows") >= 0 || os.toLowerCase().indexOf("mac") >= 0) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		}

		File file = new File("projeto_atual.xml");

		if (file.exists() && file.canRead()) {
			new Formulario(file);

		} else if (!file.exists()) {
			Util.mensagem(null, "Arquivo inexistente!\r\n\r\n" + file.getAbsolutePath());

		} else if (!file.canRead()) {
			Util.mensagem(null, "O arquivo nao pode ser lido!\r\n\r\n" + file.getAbsolutePath());

		} else {
			Util.mensagem(null, "Erro!");
		}
	}
}