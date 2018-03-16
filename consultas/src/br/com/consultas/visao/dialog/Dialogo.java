package br.com.consultas.visao.dialog;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Dialogo extends JFrame {
	private static final long serialVersionUID = 1L;

	public Dialogo() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setAlwaysOnTop(true);
	}
}