package br.com.consultas.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TreeModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import org.xml.sax.Attributes;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.visao.Formulario;
import br.com.consultas.visao.comp.Arvore;
import br.com.consultas.visao.comp.Table;
import br.com.consultas.visao.comp.TextArea;
import br.com.consultas.visao.modelo.ModeloArvore;
import br.com.consultas.visao.modelo.ModeloOrdenacao;

public class Util {
	public static ResourceBundle bundleConfig = ResourceBundle.getBundle("config");
	public static ResourceBundle bundleMsg = ResourceBundle.getBundle("mensagens");
	private static final String PREFIXO_FILTRO_CAMPO = "${";
	private static final String SUFIXO_FILTRO_CAMPO = "}";
	public static final int LARGURA_ICONE_ORDENAR = 20;
	private static final boolean LOG_MENSAGEM = true;
	public static final double DIVISAO2 = 0.70;
	public static final double DIVISAO3 = 0.70;
	public static final byte DOIS = 2;

	private Util() {
	}

	public static void checarVazio(String s, String chave, boolean trim) {
		checarVazio(s, chave, trim, null);
	}

	public static void checarVazio(String s, String chave, boolean trim, String posMsg) {
		if (s == null) {
			throw new IllegalArgumentException(bundleMsg.getString(chave) + (posMsg == null ? "" : posMsg));
		}

		if (trim && s.trim().length() == 0) {
			throw new IllegalArgumentException(bundleMsg.getString(chave) + (posMsg == null ? "" : posMsg));
		}
	}

	public static String getString(String chave) {
		return bundleMsg.getString(chave);
	}

	public static String getStringConfig(String chave) {
		return bundleConfig.getString(chave);
	}

	public static boolean getBooleanConfig(String chave) {
		return Boolean.parseBoolean(getStringConfig(chave));
	}

	public static List<Campo> criarCampos(Attributes attributes) {
		List<Campo> resposta = new ArrayList<>();

		for (int i = 0; i < attributes.getLength(); i++) {
			String nome = attributes.getQName(i);
			String valor = attributes.getValue(i);
			resposta.add(new Campo(nome, valor));
		}

		return resposta;
	}

	public static String getString(Attributes attributes, String nome, String padrao) {
		String string = attributes.getValue(nome);

		if (ehVazio(string)) {
			return padrao;
		}

		return string;
	}

	public static boolean getBoolean(Attributes attributes, String nome, boolean padrao) {
		String string = attributes.getValue(nome);

		if (ehVazio(string)) {
			return padrao;
		}

		return Boolean.parseBoolean(string);
	}

	public static int getInteger(Attributes attributes, String nome, int padrao) {
		String string = attributes.getValue(nome);

		if (ehVazio(string)) {
			return padrao;
		}

		return Integer.parseInt(string);
	}

	public static boolean ehVazio(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean ehSomenteNumeros(String s) {
		if (ehVazio(s)) {
			return false;
		}

		for (char c : s.toCharArray()) {
			boolean ehNumero = c >= '0' && c <= '9';
			if (!ehNumero) {
				return false;
			}
		}

		return true;
	}

	public static List<Referencia> criarReferencias(List<Tabela> tabelas) {
		List<Referencia> resposta = new ArrayList<>();

		for (Tabela tabela : tabelas) {
			Referencia ref = Referencia.criarReferenciaDados(tabela);
			ref.setTotalRegistros(tabela.getTotalRegistros());
			ref.setExibirTotalRegistros(true);
			ref.setIcone(tabela.getIcone());

			resposta.add(ref);
		}

		return resposta;
	}

	public static void somenteDestaques(List<Referencia> referencias, Tabelas tabelas) {
		Iterator<Referencia> it = referencias.iterator();

		while (it.hasNext()) {
			Referencia ref = it.next();
			if (!ref.isDestaque(tabelas)) {
				it.remove();
			}
		}
	}

	public static void somenteComRegistros(List<Referencia> referencias) {
		Iterator<Referencia> it = referencias.iterator();

		while (it.hasNext()) {
			Referencia ref = it.next();
			if (ref.getTotalRegistros() == 0) {
				it.remove();
			}
		}
	}

	public static void somenteSemRegistros(List<Referencia> referencias) {
		Iterator<Referencia> it = referencias.iterator();

		while (it.hasNext()) {
			Referencia ref = it.next();
			if (ref.getTotalRegistros() > 0) {
				it.remove();
			}
		}
	}

	public static void selecionarPeloNome(List<Referencia> referencias, String nome) {
		for (Referencia ref : referencias) {
			ref.selecionarPeloNome(nome);
		}
	}

	public static List<Referencia> pesquisarReferencias(List<Referencia> referencias, Tabela tabela, Tabelas tabelas) {
		final String alias = tabela.getAlias().getValor();
		List<Referencia> container = new ArrayList<>();

		for (Referencia r : referencias) {
			r.especial(false);
		}

		for (Referencia ref : referencias) {
			if (ref.getAlias2().equals(alias)) {
				ref.setEspecial(true);
				container.add(ref);
			} else {
				refs(container, ref, alias);
			}
		}

		List<Referencia> resposta = new ArrayList<>();

		for (Referencia ref : container) {
			resposta.add(ref.clonarCaminho());
		}

		atualizarCampoID(resposta, tabelas);

		return resposta;
	}

	public static List<Referencia> pesquisarReferenciasPelaTabela(List<Referencia> referencias, Tabela tabela,
			Tabelas tabelas) {
		final String alias = tabela.getAlias().getValor();
		List<Referencia> resposta = new ArrayList<>();

		for (Referencia ref : referencias) {
			if (ref.getAlias2().equals(alias)) {
				resposta.add(ref);
			}
			refs2(resposta, ref, alias);
		}

		return resposta;
	}

	private static void refs2(List<Referencia> resposta, Referencia ref, String alias) {
		for (Referencia r : ref.getReferencias()) {
			if (r.getAlias2().equals(alias)) {
				resposta.add(r);
			}
			refs2(resposta, r, alias);
		}
	}

	public static void validarArvore(List<Referencia> referencias, Tabelas tabelas) {
		for (Referencia r : referencias) {
			r.validar(tabelas);
		}
	}

	public static List<Referencia> filtrarTopo(List<Referencia> referencias, Tabela tabela, Tabelas tabelas) {
		final String alias = tabela.getAlias().getValor();

		List<Referencia> resposta = new ArrayList<>();

		for (Referencia ref : referencias) {
			if (ref.getAlias2().equals(alias)) {
				resposta.add(ref);
			}
		}

		atualizarCampoID(resposta, tabelas);

		return resposta;
	}

	public static void setEspecialFalse(List<Referencia> referencias) {
		for (Referencia r : referencias) {
			r.especial(false);
		}
	}

	public static void atualizarCampoID(List<Referencia> referencias, Tabelas tabelas) {
		for (Referencia ref : referencias) {
			ref.setCampoID(tabelas);
		}
	}

	public static void setIcone(List<Referencia> referencias, Tabelas tabelas) {
		for (Referencia ref : referencias) {
			ref.setIcone(tabelas);
		}
	}

	private static void refs(List<Referencia> resposta, Referencia ref, String alias) {
		for (Referencia r : ref.getReferencias()) {
			if (r.getAlias2().equals(alias)) {
				r.setEspecial(true);
				resposta.add(r);
			} else {
				refs(resposta, r, alias);
			}
		}
	}

	public static void ordenar(List<Referencia> referencias) {
		Collections.sort(referencias, new Comparador());

		for (Referencia r : referencias) {
			r.ordenar();
		}
	}

	static class Comparador implements Comparator<Referencia> {
		@Override
		public int compare(Referencia o1, Referencia o2) {
			return o1.getAlias().compareTo(o2.getAlias());
		}
	}

	public static String getSQL(String s) {
		if (ehVazio(s)) {
			return null;
		}

		s = s.trim();

		if (s.endsWith(";")) {
			s = s.substring(0, s.length() - 1);
		}

		return s;
	}

	public static Tabela criarTabela() {
		return new Tabela("Campos");
	}

	public static String fragmentoFiltroCampo(Campo campo) {
		StringBuilder sb = new StringBuilder(campo.getNome());
		String valor = campo.getValor();

		if (ehVazio(valor)) {
			sb.append("=");
		} else {
			if (valor.startsWith(PREFIXO_FILTRO_CAMPO) && valor.endsWith(SUFIXO_FILTRO_CAMPO)) {
				int posIni = valor.indexOf(PREFIXO_FILTRO_CAMPO);
				int posFim = valor.lastIndexOf(SUFIXO_FILTRO_CAMPO);
				valor = valor.substring(posIni + PREFIXO_FILTRO_CAMPO.length(), posFim);
				sb.append(" " + valor.trim());
			} else {
				sb.append("=" + valor.trim());
			}
		}

		return sb.toString();
	}

	public static void mensagem(Component componente, String string) {
		TextArea textArea = new TextArea(string);
		textArea.setPreferredSize(new Dimension(500, 300));
		JOptionPane.showMessageDialog(componente, textArea, getString("label.atencao"), JOptionPane.PLAIN_MESSAGE);
	}

	public static boolean confirmarUpdate(Component componente) {
		return JOptionPane.showConfirmDialog(componente, getString("label.confirmar_update"),
				getString("label.atencao"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}

	public static SQL criarSQL(Referencia ref, Tabelas tabelas, String aliasTemp) {
		SQL sql = new SQL();

		sql.dados = ref.gerarConsultaDados(tabelas);
		sql.select = ref.gerarConsulta(tabelas, aliasTemp);
		sql.delete = ref.gerarDelete(tabelas);
		sql.update = ref.gerarUpdate(tabelas);

		return sql;
	}

	public static SQL criarSQL(Referencia ref, Tabelas tabelas) {
		return criarSQL(ref, tabelas, null);
	}

	public static String getAliasTemp(Component componente, Referencia referencia) {
		Object[] opcoes = referencia.getCaminhoArray();
		Object selecionado = JOptionPane.showInputDialog(componente, "Selecione", getString("label.opcoes"),
				JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[opcoes.length - 1]);
		return selecionado == null ? null : selecionado.toString();
	}

	public static String getValorInputDialog(Component componente, Tabela tabela, Campo campo) {
		Object objeto = JOptionPane.showInputDialog(componente, tabela.getNome() + "." + campo.getNome(),
				tabela.getNome(), JOptionPane.PLAIN_MESSAGE, null, null, campo.getValor());
		return objeto != null ? objeto.toString() : null;
	}

	public static String getNomeCampo(Component componente, Tabela tabela) {
		Object[] opcoes = tabela.getNomeCampos().toArray();
		Object selecionado = JOptionPane.showInputDialog(componente, "Selecione", getString("label.opcoes"),
				JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);
		return selecionado == null ? null : selecionado.toString();
	}

	public static Referencia getPai(Component componente, Referencia referencia) {
		Object[] opcoes = referencia.getCaminhoArray();

		int indice = opcoes.length - 1;
		Referencia ref = referencia;

		while (ref != null) {
			opcoes[indice] = indice + " - " + opcoes[indice];

			indice--;
			ref = ref.getPai();
		}

		Object selecionado = JOptionPane.showInputDialog(componente, "Selecione o Pai", getString("label.opcoes"),
				JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);

		if (selecionado == null) {
			return null;
		}

		indice = opcoes.length - 1;
		ref = referencia;

		while (ref != null) {
			if (selecionado.equals(opcoes[indice])) {
				return ref;
			}

			indice--;
			ref = ref.getPai();
		}

		return ref;
	}

	public static void expandirRetrair(Arvore tree, boolean expandir) {
		ModeloArvore modelo = (ModeloArvore) tree.getModel();
		String raiz = (String) modelo.getRoot();
		int filhos = modelo.getChildCount(raiz);

		List<Referencia> folhas = new ArrayList<>();

		for (int i = 0; i < filhos; i++) {
			Referencia ref = (Referencia) modelo.getChild(raiz, i);
			ref.addFolha(folhas);
		}

		for (Referencia r : folhas) {
			List<Object> lista = new ArrayList<>();
			r.caminho(lista);
			lista.add(0, raiz);

			TreePath path = new TreePath(lista.toArray(new Object[] {}));
			if (expandir) {
				tree.expandPath(path);
			} else {
				tree.collapsePath(path);
			}
		}
	}

	public static void expandirRetrairTodos(Arvore tree, boolean expandir) {
		ModeloArvore modelo = (ModeloArvore) tree.getModel();
		String raiz = (String) modelo.getRoot();
		int filhos = modelo.getChildCount(raiz);

		List<Referencia> todos = new ArrayList<>();

		for (int i = 0; i < filhos; i++) {
			Referencia ref = (Referencia) modelo.getChild(raiz, i);
			ref.todos(todos);
		}

		for (Referencia r : todos) {
			List<Object> lista = new ArrayList<>();
			r.caminho(lista);
			lista.add(0, raiz);

			if (lista.size() == 1) {
				continue;
			}

			TreePath path = new TreePath(lista.toArray(new Object[] {}));
			if (expandir) {
				tree.expandPath(path);
			} else {
				tree.collapsePath(path);
			}
		}
	}

	public static void atualizarTodaEstrutura(Arvore tree) {
		ModeloArvore modelo = (ModeloArvore) tree.getModel();
		String raiz = (String) modelo.getRoot();
		int filhos = modelo.getChildCount(raiz);

		List<Referencia> referencias = new ArrayList<>();

		for (int i = 0; i < filhos; i++) {
			Referencia ref = (Referencia) modelo.getChild(raiz, i);
			ref.atualizarTodaEstrutura(referencias);
		}

		for (Referencia r : referencias) {
			List<Object> lista = new ArrayList<>();
			r.caminho(lista);
			lista.add(0, raiz);
			lista.add(r);

			TreePath path = new TreePath(lista.toArray(new Object[] {}));
			TreeModelEvent event = new TreeModelEvent(r, path);
			modelo.treeNodesChanged(event);
		}
	}

	public static void atualizarEstrutura(Arvore tree, Tabelas tabelas, Tabela tabela) {
		ModeloArvore modelo = (ModeloArvore) tree.getModel();
		String raiz = (String) modelo.getRoot();
		int filhos = modelo.getChildCount(raiz);

		List<Referencia> referencias = new ArrayList<>();

		for (int i = 0; i < filhos; i++) {
			Referencia ref = (Referencia) modelo.getChild(raiz, i);
			ref.atualizar(referencias, tabelas, tabela);
		}

		for (Referencia r : referencias) {
			List<Object> lista = new ArrayList<>();
			r.caminho(lista);
			lista.add(0, raiz);
			lista.add(r);

			TreePath path = new TreePath(lista.toArray(new Object[] {}));
			TreeModelEvent event = new TreeModelEvent(r, path);
			modelo.treeNodesChanged(event);
		}
	}

	public static void atualizarEstrutura(Arvore tree, Tabelas tabelas, boolean comID) {
		ModeloArvore modelo = (ModeloArvore) tree.getModel();
		String raiz = (String) modelo.getRoot();
		int filhos = modelo.getChildCount(raiz);

		List<Referencia> referencias = new ArrayList<>();

		for (int i = 0; i < filhos; i++) {
			Referencia ref = (Referencia) modelo.getChild(raiz, i);
			ref.atualizar(referencias, tabelas, comID);
		}

		for (Referencia r : referencias) {
			List<Object> lista = new ArrayList<>();
			r.caminho(lista);
			lista.add(0, raiz);
			lista.add(r);

			TreePath path = new TreePath(lista.toArray(new Object[] {}));
			TreeModelEvent event = new TreeModelEvent(r, path);
			modelo.treeNodesChanged(event);
		}
	}

	public static String getContentTransfered() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Object resposta = null;

		if (clipboard != null) {
			try {
				resposta = clipboard.getData(DataFlavor.stringFlavor);
			} catch (Exception e) {
			}
		}

		return resposta != null ? resposta.toString() : "";
	}

	public static void setContentTransfered(String string) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		if (clipboard != null) {
			clipboard.setContents(new StringSelection(string), null);
		}
	}

	public static void ajustar(JTable table, Graphics graphics) {
		ajustar(table, graphics, Util.LARGURA_ICONE_ORDENAR);
	}

	public static void ajustar(JTable table, Graphics graphics, int ajuste) {
		DefaultTableColumnModel columnModel = (DefaultTableColumnModel) table.getColumnModel();
		FontMetrics fontMetrics = graphics.getFontMetrics();

		for (int icoluna = 0; icoluna < table.getColumnCount(); icoluna++) {
			String columnName = table.getColumnName(icoluna);
			int width = fontMetrics.stringWidth(columnName);

			for (int line = 0; line < table.getRowCount(); line++) {
				TableCellRenderer renderer = table.getCellRenderer(line, icoluna);
				Component component = renderer.getTableCellRendererComponent(table, table.getValueAt(line, icoluna),
						false, false, line, icoluna);
				width = Math.max(width, component.getPreferredSize().width);
			}

			TableColumn column = columnModel.getColumn(icoluna);
			column.setPreferredWidth(width + ajuste);
		}
	}

	public static void setActionESC(JComponent component, Action action) {
		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

		ActionMap actionMap = component.getActionMap();
		actionMap.put("esc", action);
	}

	public static void fechar(Window w) {
		WindowEvent event = new WindowEvent(w, WindowEvent.WINDOW_CLOSING);
		EventQueue systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		systemEventQueue.postEvent(event);
	}

	public static void setWindowListener(final JFrame frame, final Formulario formulario) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				frame.setState(JFrame.NORMAL);
				frame.setVisible(false);
				frame.setVisible(true);
			}

			public void windowOpened(WindowEvent e) {
				formulario.abrirJanela();
			}

			public void windowClosing(WindowEvent e) {
				formulario.fecharJanela();
			}
		});
	}

	public static String getStackTrace(String info, Exception e) {
		StringBuilder sb = new StringBuilder(info + "\r\n\r\n");

		if (e != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);

			if (LOG_MENSAGEM) {
				e.printStackTrace(ps);
			} else {
				e.printStackTrace();
			}

			sb.append(new String(baos.toByteArray()));
		}

		return sb.toString();
	}

	public static Tabela limparID(Referencia referencia, Formulario formulario) {
		Tabela tabela = referencia.getTabela(formulario.getTabelas());
		tabela.limparID();

		return tabela;
	}

	public static String getStringLista(List<String> lista, boolean apostrofes) {
		StringBuilder sb = new StringBuilder();

		for (String string : lista) {
			if (ehVazio(string)) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(apostrofes ? citar(string) : string);
		}

		return sb.toString();
	}

	private static String citar(String string) {
		return "'" + string + "'";
	}

	public static Vector<Object> criarDados(Vector<Object[]> registros, ModeloOrdenacao modelo) {
		Vector<Object> resposta = new Vector<>();

		for (int i = 0; i < modelo.getRowCount(); i++) {
			Object id = modelo.getValueAt2(i, 0);
			Object valor = getValor(id, registros);

			resposta.add(valor != null ? valor : "");
		}

		return resposta;
	}

	private static Object getValor(Object id, Vector<Object[]> resp) {
		StringBuilder sb = new StringBuilder();

		for (Object[] objects : resp) {
			if (objects[0].equals(id)) {

				if (sb.length() > 0) {
					sb.append(", ");
				}

				sb.append(objects[1]);
			}
		}

		return sb.length() > 0 ? sb.toString() : null;
	}

	public static void pesquisaSelecionadosMemoria(Referencia ref, Tabelas tabelas) {
		String pesquisa = ref.getConsultaSelecionados(tabelas);
		setContentTransfered(pesquisa);
	}

	public static String getMensagemErro(Vector<Object[]> dados, Table table) {
		if (dados.isEmpty()) {
			return Util.getString("label.nenhum_registro_encontrado");
		}

		if (table.getModel().getRowCount() == 0) {
			return Util.getString("label.sem_registros_table");
		}

		if (table.getModel().getRowCount() < dados.size()) {
			return Util.getString("label.registros_vs_table") + " [" + table.getModel().getRowCount() + "/"
					+ dados.size() + "]";
		}

		return null;
	}

	public static String getTituloCampoAgregado(Referencia ref, String nome) {
		return ref.getPai().getAlias() + " >> " + ref.getAlias() + "." + nome;
	}

	public static String getTituloCampoAgregado(Referencia pai, Referencia ref, String nome) {
		return pai.getAlias() + " >> " + ref.getAlias() + "." + nome;
	}

	public static void selecionarLinhas(int[] is, Vector<Vector<String>> dados, Table table) {
		if (is != null) {
			for (int i : is) {
				if (i < dados.size()) {
					table.addRowSelectionInterval(i, i);
				}
			}
		}
	}
}