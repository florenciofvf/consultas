package br.com.consultas.xml;

import java.io.File;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.consultas.Campo;
import br.com.consultas.Referencia;
import br.com.consultas.Tabela;
import br.com.consultas.Tabelas;
import br.com.consultas.util.Util;

public class XML {
	public static void processar(File file, Tabelas tabelas, List<Referencia> referencias) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		try {
			parser.parse(file, new XMLHandler(tabelas, referencias));
		} catch (Exception ex) {
			String msg = Util.getStackTrace("XML.processar()", ex);
			Util.mensagem(null, msg);
			System.exit(0);
		}

		Util.ordenar(referencias);
	}
}

class XMLHandler extends DefaultHandler {
	private final List<Referencia> referencias;
	private boolean lendoReferencias;
	private Referencia selecionado;
	private final Tabelas tabelas;
	private boolean lendoTabelas;

	public XMLHandler(Tabelas tabelas, List<Referencia> referencias) {
		this.referencias = referencias;
		this.tabelas = tabelas;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("tabelas")) {
			lendoTabelas = true;

		} else if (qName.equals("refs")) {
			lendoReferencias = true;

		} else {
			if (lendoTabelas) {
				Tabela tab = new Tabela(qName);
				List<Campo> campos = Util.criarCampos(attributes);
				for (Campo c : campos) {
					tab.add(c);
				}

				tabelas.add(tab);
			} else if (lendoReferencias) {
				if (qName.equals("obj")) {
					final String pkString = Util.getString(attributes, "pk", null);
					final String fkString = Util.getString(attributes, "fk", null);

					final String alias = Util.getString(attributes, "alias", null);
					final String aliasAlt = Util.getString(attributes, "alias-alt", null);
					final boolean inverso = Util.getBoolean(attributes, "inverso", false);
					final boolean cloneCompleto = Util.getBoolean(attributes, "cloneCompleto", false);

					final int pk = Util.ehSomenteNumeros(pkString) ? Util.getInteger(attributes, "pk", 0)
							: Util.ehVazio(pkString) ? 0 : -1;
					final int fk = Util.ehSomenteNumeros(fkString) ? Util.getInteger(attributes, "fk", 1)
							: Util.ehVazio(fkString) ? 1 : -1;

					final String preJoin = Util.getString(attributes, "preJoin", null);
					final String resumo = Util.getString(attributes, "resumo", null);

					final String pkNome = !Util.ehVazio(pkString) && !Util.ehSomenteNumeros(pkString) ? pkString : null;
					final String fkNome = !Util.ehVazio(fkString) && !Util.ehSomenteNumeros(fkString) ? fkString : null;

					Referencia ref = new Referencia(alias, aliasAlt, inverso, pk, pkNome, fk, fkNome, preJoin, resumo,
							cloneCompleto);

					if (selecionado == null) {
						referencias.add(ref);
					} else {
						selecionado.add(ref);
					}

					selecionado = ref;
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("tabelas")) {
			lendoTabelas = false;

		} else if (qName.equals("refs")) {
			lendoReferencias = false;

		} else if (lendoReferencias) {
			if (qName.equals("obj")) {
				selecionado = selecionado.getPai();
			}
		}
	}
}