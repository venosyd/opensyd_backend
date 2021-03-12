package com.venosyd.open.backend.lib;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.esotericsoftware.yamlbeans.YamlReader;

import com.venosyd.open.commons.log.Debuggable;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 * 
 *         Carrega as tasks configuradas em assets/config/tasks.yaml e seu jar
 *         devidamente colocado em assets/modules
 */
public enum TasksLoader implements Debuggable {

    INSTANCE;

    /**
     * mapa de configuracoes
     */
    private Map<String, Object> _yaml;

    TasksLoader() {
        init();
    }

    /**
     * inicia o mapa em memoria contendo as configuracoes lendo o arquivo de
     * configuracoes definido por padrao em YAML
     */
    @SuppressWarnings("unchecked")
    private void init() {
        try {
            var reader = new YamlReader(new FileReader("assets/config/tasks.yaml"));
            _yaml = (Map<String, Object>) reader.read();
        } catch (final Exception e) {
            err.tag("TASKS LOADER YAML EXCEPTION").ln(e);
        }
    }

    /**
     * retorna uma propriedade de acordo com o tipo especificado
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final String property) {
        return (T) _yaml.get(property);
    }

    /** lista todos os serviços */
    public Set<Object> getAllTasks() {
        return (_yaml != null && !_yaml.isEmpty()) ? _yaml.values().stream().collect(Collectors.toSet())
                : new HashSet<>();
    }

    /**
     * imprime o mapa em memoria que representa o arquivo lido
     */
    public void outPrintln() {
        out.tag("TASKS LOADER YAML FILE").map_(_yaml);
    }

}
