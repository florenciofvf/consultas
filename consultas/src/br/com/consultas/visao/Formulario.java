package br.com.consultas.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import br.com.consultas.Persistencia;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Icones;
import br.com.consultas.util.Util;
import br.com.consultas.visao.comp.Button;
import br.com.consultas.visao.comp.Label;
import br.com.consultas.visao.comp.Menu;
import br.com.consultas.visao.comp.MenuItem;
import br.com.consultas.visao.comp.PanelLeft;
import br.com.consultas.visao.comp.ScrollPane;
import br.com.consultas.visao.comp.SplitPane;
import br.com.consultas.visao.comp.TabbedPane;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.comp.TextArea;
import br.com.consultas.visao.dialog.DadosDialog;
import br.com.consultas.visao.dialog.ProgressoDialog;
import br.com.consultas.visao.modelo.ModeloBundle;
import br.com.consultas.visao.modelo.ModeloOrdenacao;
import br.com.consultas.xml.XML;

public class Formulario extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Table tableConfig = new Table(new ModeloOrdenacao(new ModeloBundle(Util.bundleConfig, true)));
	private final Table tableMsg = new Table(new ModeloOrdenacao(new ModeloBundle(Util.bundleMsg, false)));
	private final MenuItem itemLimparSL = new MenuItem("label.limpar_somente_leitura", Icones.LIMPAR);
	private final MenuItem itemLimparCampos = new MenuItem("label.limpar_campos", Icones.LIMPAR);
	private final MenuItem itemLimparIds = new MenuItem("label.limpar_ids", Icones.LIMPAR);
	private final MenuItem itemFechar = new MenuItem("label.fechar", Icones.SAIR);
	private final SplitPane splitPane = new SplitPane(SplitPane.VERTICAL_SPLIT);
	private final List<Referencia> referencias = new ArrayList<>();
	protected ProgressoDialog progresso = new ProgressoDialog();
	private final Menu menuArquivo = new Menu("label.arquivo");
	private static final double DIVISAO_TEXT_AREA = 0.80;
	private final TabbedPane fichario = new TabbedPane();
	protected final TextArea textArea = new TextArea();
	private final JMenuBar menuBar = new JMenuBar();
	private final Tabelas tabelas = new Tabelas();
	private final PainelTabelas painelRegistros;
	private final PainelTabelas painelDestaques;
	private final PainelConsultas abaConsultas;
	private final PainelTabelas painelTabelas;
	private int janelas;

	public Formulario(File file) throws Exception {
		super("Consultas");

		XML.processar(file, tabelas, referencias);
		Util.validarArvore(referencias, tabelas);

		painelRegistros = new PainelTabelas(this, false, true);
		painelDestaques = new PainelTabelas(this, true, false);
		painelTabelas = new PainelTabelas(this, false, false);
		abaConsultas = new PainelConsultas(this);

		setExtendedState(Formulario.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setSize(500, 500);
		montarLayout();

		cfg();
		setVisible(true);
	}

	private void cfg() {
		if (System.getProperty("os.name").startsWith("Mac OS")) {
			try {
				Class<?> classe = Class.forName("com.apple.eawt.FullScreenUtilities");
				Method method = classe.getMethod("setWindowCanFullScreen", Window.class, Boolean.TYPE);
				method.invoke(classe, this, true);
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".setWindowCanFullScreen()", e);
				Util.mensagem(this, msg);
			}
		}

		itemLimparCampos.addActionListener(e -> {
			tabelas.getTabelas().forEach(Tabela::limparCampos);

			Util.setEspecialFalse(referencias);

			for (Tabela t : tabelas.getTabelas()) {
				atualizarCampoIDForm(t);
			}
		});

		itemLimparIds.addActionListener(e -> {
			for (Tabela t : tabelas.getTabelas()) {
				t.limparID();
			}

			Util.setEspecialFalse(referencias);

			for (Tabela t : tabelas.getTabelas()) {
				atualizarCampoIDForm(t);
			}
		});

		itemLimparSL.addActionListener(e -> tabelas.getTabelas().forEach(Tabela::limparSolenteLeitura));

		itemFechar.addActionListener(e -> System.exit(0));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				tableConfig.ajustar(getGraphics());
				tableMsg.ajustar(getGraphics());
				painelRegistros.windowOpened();
				painelDestaques.windowOpened();
				abaConsultas.windowOpened();
				painelTabelas.windowOpened();
			}
		});

		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if ((e.getNewState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							splitPane.setDividerLocation((int) (getHeight() * DIVISAO_TEXT_AREA));
							painelRegistros.windowOpened();
							painelDestaques.windowOpened();
							abaConsultas.windowOpened();
							painelTabelas.windowOpened();
						}
					});
				}
			}
		});
	}

	public Tabelas getTabelas() {
		return tabelas;
	}

	public List<Referencia> getReferencias() {
		return referencias;
	}

	public void atualizarCampoIDForm(Tabela tabela) {
		abaConsultas.atualizarCampoID(tabela);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());

		fichario.addTab("label.destaques", painelDestaques);
		fichario.addTab("label.tabRegtros", painelRegistros);
		fichario.addTab("label.tabelas", painelTabelas);
		fichario.addTab("label.referencias", abaConsultas);
		fichario.addTab("label.config", new ScrollPane(tableConfig));
		fichario.addTab("label.mensagens", new ScrollPane(tableMsg));

		splitPane.setLeftComponent(fichario);
		splitPane.setRightComponent(textArea);

		add(BorderLayout.CENTER, splitPane);
		add(BorderLayout.SOUTH, new PainelControle());

		menuBar.add(menuArquivo);
		menuArquivo.add(itemLimparIds);
		menuArquivo.addSeparator();
		menuArquivo.add(itemLimparCampos);
		menuArquivo.addSeparator();
		menuArquivo.add(itemLimparSL);
		menuArquivo.addSeparator();
		menuArquivo.add(itemFechar);
		setJMenuBar(menuBar);
	}

	public void divisao(int i) {
		painelRegistros.splitPane.setDividerLocation(i);
		painelDestaques.splitPane.setDividerLocation(i);
		abaConsultas.splitPane.setDividerLocation(i);
		painelTabelas.splitPane.setDividerLocation(i);
	}

	private void executeUpdate() {
		String string = Util.getSQL(textArea.getText());

		if (string == null) {
			Util.mensagem(this, Util.getString("labe.consulta_vazia"));
			return;
		}

		if (Util.confirmarUpdate(this)) {
			try {
				Persistencia.executeUpdate(string);
				Util.mensagem(this, Util.getString("label.sucesso"));
			} catch (Exception e) {
				String msg = Util.getStackTrace(getClass().getName() + ".executeUpdate()", e);
				Util.mensagem(this, msg);
			}
		}
	}

	private void executeQuery() {
		String string = Util.getSQL(textArea.getText());

		if (string == null) {
			Util.mensagem(this, Util.getString("labe.consulta_vazia"));
			return;
		}

		try {
			new DadosDialog(this, null, null, false, string, null);
		} catch (Exception e) {
			String msg = Util.getStackTrace(getClass().getName() + ".executeQuery()", e);
			Util.mensagem(this, msg);
		}
	}

	private void limpar() {
		textArea.setText("");
		textArea.requestFocus();
	}

	public void abrirJanela() {
		janelas++;
		setAlwaysOnTop(janelas <= 0);
	}

	public void fecharJanela() {
		janelas--;
		setAlwaysOnTop(janelas <= 0);

		if (janelas < 0) {
			janelas = 0;
		}
	}

	private class PainelControle extends PanelLeft {
		private static final long serialVersionUID = 1L;
		private final Label labelValorVersao = new Label("versao_valor", new Color(0x99949991));
		private final Button buttonGetContent = new Button("label.get_content", Icones.BAIXAR);
		private final Button buttonUpdate = new Button("label.execute_update", Icones.EXECUTAR);
		private final Button buttonQuery = new Button("label.execute_query", Icones.EXECUTAR);
		private final Button buttonLimpar = new Button("label.limpar", Icones.LIMPAR);
		private final Label labelTabelas = new Label("label.total_tabelas");
		private final Label labelValorTabelas = new Label(Color.BLUE);

		PainelControle() {
			final String espacamento = "              ";

			adicionar(new Label("versao"), labelValorVersao, new JLabel(espacamento), labelTabelas, labelValorTabelas,
					new JLabel(espacamento), buttonLimpar, buttonUpdate, buttonQuery, buttonGetContent);

			labelValorVersao.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Util.mensagem(PainelControle.this,
							"florenciovieira@gmail.com\r\n\r\n" + labelValorVersao.getText());
				}
			});

			labelValorTabelas.setText("" + tabelas.getTotalTabelas());
			labelValorTabelas.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					fichario.setSelectedIndex(2);
				}
			});

			buttonLimpar.addActionListener(e -> limpar());

			buttonUpdate.addActionListener(e -> executeUpdate());

			buttonQuery.addActionListener(e -> executeQuery());

			buttonGetContent.addActionListener(e -> textArea.setText(Util.getContentTransfered()));
		}
	}
}