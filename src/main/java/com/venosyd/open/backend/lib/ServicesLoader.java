package com.venosyd.open.backend.lib;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.venosyd.open.commons.log.Debuggable;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 * 
 *         Carrega informacoes sobre os servicos encontrados no arquivo
 *         assets/config/services.yaml
 */
public enum ServicesLoader implements Debuggable {

    INSTANCE;

    /**
     * mapa de configuracoes
     */
    private Map<String, Object> _yaml;

    ServicesLoader() {
        init();
    }

    /**
     * inicia o mapa em memoria contendo as configuracoes lendo o arquivo de
     * configuracoes definido por padrao em YAML
     */
    @SuppressWarnings("unchecked")
    private void init() {
        try {
            var reader = new YamlReader(new FileReader("assets/config/services.yaml"));
            _yaml = (Map<String, Object>) reader.read();
        } catch (final Exception e) {
            err.tag("SERVICES LOADER YAML EXCEPTION").ln(e);
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
    public Set<String> getAllServices() {
        var services = _yaml.keySet();
        services.remove("seeker");

        return services;
    }

    /**
     * imprime o mapa em memoria que representa o arquivo lido
     */
    public void outPrintln() {
        out.tag("SERVICES LOADER YAML FILE").map_(_yaml);
    }

}
