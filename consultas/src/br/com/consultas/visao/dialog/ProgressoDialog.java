package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class ProgressoDialog extends JWindow {
	private static final long serialVersionUID = 1L;
	private JProgressBar progresso = new JProgressBar();

	public ProgressoDialog() {
		setLayout(new BorderLayout());
		add(BorderLayout.SOUTH, progresso);

		progresso.setPreferredSize(new Dimension(400, 40));
		progresso.setBorderPainted(true);
		progresso.setStringPainted(true);
		progresso.setMinimum(0);

		pack();
		setLocationRelativeTo(null);
	}

	public void exibir(int maximo) {
		progresso.setMaximum(maximo);
		setVisible(true);
	}

	public void atualizar(int valor) {
		progresso.setValue(valor);
	}

	public void esconder() {
		setVisible(false);
	}
}