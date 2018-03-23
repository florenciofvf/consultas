package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import br.com.consultas.util.Util;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.PanelBorderLayout;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.dialog.Dialogo;

public abstract class PainelAbas extends PanelBorderLayout {
	private static final long serialVersionUID = 1L;
	protected final Button buttonExecutar = new Button("label.executar");
	protected final Button buttonFechar = new Button("label.fechar");
	protected final PanelLeft painelControle = new PanelLeft();
	protected final Dialogo dialogo;

	public PainelAbas(Dialogo dialogo, boolean executa) {
		this.dialogo = dialogo;

		painelControle.adicionar(buttonFechar);

		if (executa) {
			painelControle.adicionar(buttonExecutar);
		}

		buttonFechar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Util.fechar(dialogo);
			}
		});

		buttonExecutar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executar();
			}
		});

		add(BorderLayout.SOUTH, painelControle);
	}

	public abstract void executar();
}