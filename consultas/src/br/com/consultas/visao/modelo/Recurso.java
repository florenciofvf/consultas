package br.com.consultas.visao.modelo;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Recurso extends ResourceBundle {
	private final Map<String, String> map;

	public Recurso(List<ChaveValor> listagem) {
		map = new HashMap<>();

		for (ChaveValor cv : listagem) {
			map.put(cv.chave, cv.valor);
		}
	}

	@Override
	protected Object handleGetObject(String key) {
		return map.get(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(map.keySet());
	}
}