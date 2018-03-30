package br.com.consultas.visao;

import java.awt.BorderLayout;

import br.com.consultas.util.Icones;
import br.com.consultas.util.Util;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.PanelBorderLayout;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.dialog.Dialogo;

public abstract class PainelAbas extends PanelBorderLayout {
	private static final long serialVersionUID = 1L;
	protected final Button buttonExecutar = new Button("label.executar", Icones.EXECUTAR);
	protected final Button buttonFechar = new Button("label.fechar", Icones.SAIR);
	protected final PanelLeft painelControle = new PanelLeft();
	protected final Dialogo dialogo;

	public PainelAbas(Dialogo dialogo, boolean executa) {
		this.dialogo = dialogo;

		painelControle.adicionar(buttonFechar);

		if (executa) {
			painelControle.adicionar(buttonExecutar);
		}

		buttonFechar.addActionListener(e -> Util.fechar(dialogo));

		buttonExecutar.addActionListener(e -> executar());

		add(BorderLayout.SOUTH, painelControle);
	}

	public abstract void executar();
}