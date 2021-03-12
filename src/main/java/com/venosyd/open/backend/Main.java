package com.venosyd.open.backend;

import com.venosyd.open.backend.lib.DynamicClassLoader;
import com.venosyd.open.backend.lib.ServicesLoader;
import com.venosyd.open.backend.lib.TasksLoader;
import com.venosyd.open.backend.secure.SecureAPIGateway;
import com.venosyd.open.backend.secure.SecureServer;
import com.venosyd.open.backend.secure.SecureWebSocketServer;
import com.venosyd.open.backend.unsecure.UnsecureAPIGateway;
import com.venosyd.open.backend.unsecure.UnsecureSeeker;
import com.venosyd.open.backend.unsecure.UnsecureServer;
import com.venosyd.open.backend.unsecure.UnsecureWebSocketServer;

/**
 * @author sergio lisan <sels@venosyd.com>
 * 
 *         venosyd © 2016-2021
 * 
 *         Porta de entrada, classe inicial ou init assim como desejar, do
 *         backend da venosyd
 */
public class Main {

    /**
     * tipo de servidor (monolitico ou modular)
     */
    private String type;

    /**
     * modo do servidor, DEV ou PROD
     */
    private String mode;

    /**
     * carrega os modulos que nada mais sao do que servicos que este servidor ira
     * ofertas. eles sao postos na pasta assets/modules em formato .jar e sao
     * descritos em assets/config/services.yaml
     */
    static {
        ((DynamicClassLoader) ClassLoader.getSystemClassLoader()).loadModules();
    }

    public static void main(String[] args) {
        var main = new Main(); // carrega instancia do servidor

        // inicia modulos configurados no services.yaml
        main.init(); // inicia propriamente
        main.initTasks(); // inicia as tasks
    }

    /**
     * inicia o servidor setando o modo e o tipo, e de acordo chama a funcao
     * especifica para puxar os servicos requisitados
     */
    private void init() {
        // carrega o tipo
        type = System.getenv("TYPE");
        if (type == null || type.isEmpty())
            type = "MODULAR";

        // carrega o modo
        mode = System.getenv("MODE");
        if (mode == null || mode.isEmpty())
            mode = "PROD";

        // servidor standalone
        if (type.equals("MONOLITH")) {
            startMonolithServer();
        }

        // servidor monolitico
        else if (type.equals("MODULAR")) {
            startModularServer();
        }
    }

    /**
     * se o servidor for monolitico, carrega o servidor seguro/inseguro de acordo
     * com as configuracoes de desenvolvimento
     */
    private void startMonolithServer() {
        var service = System.getenv("STANDALONE");

        if (isDev()) {
            new UnsecureServer().init(service);
        } else {
            new SecureServer().init(service);
        }
    }

    /**
     * inicia a interface do servidor seguro/inseguro de acordo com as configuracoes
     * e os servicos prestados em seguida
     * 
     * nota-se que a seguranca se reserva ao API Gateway, ja que a comnicacao
     * interna entre os servicos, para ser mais eficiente, ja ocorre em um ambiente
     * seguro
     */
    private void startModularServer() {
        if (isDev()) {
            new UnsecureAPIGateway().init(); // GATEKEEPER inseguro
            new UnsecureWebSocketServer().init(); // Servidor de Websocket inseguro
        } else {
            new SecureAPIGateway().init(); // GATEKEEPER seguro
            new SecureWebSocketServer().init(); // Servidor de Websocket seguro
        }

        new UnsecureSeeker().init(); // inicia o SEEKER

        // lista de servicos listados em assets/config/services.yaml e que podem
        // ter codigo carregado atraves de seus .jar em assets/modules
        for (var service : ServicesLoader.INSTANCE.getAllServices()) {
            new UnsecureServer().init(service); // inicia servidor inseguro
        }
    }

    /**
     * Inicia as tarefas configuracas no tasks.yaml. Tarefas sao atividades,
     * consultas, backup ou qualquer codigo e procedimento que precise rodar no
     * background, nao necessariamente precisa de um endpoint
     */
    private void initTasks() {
        try {
            for (var service : TasksLoader.INSTANCE.getAllTasks()) {
                var clazz = Class.forName((String) service, true,
                        ((DynamicClassLoader) ClassLoader.getSystemClassLoader()));
                var task = (Runnable) clazz.getDeclaredConstructor().newInstance();

                task.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * verifica se o servidor foi configuraco para o modo de desenvolvimento
     */
    private boolean isDev() {
        return mode != null && mode.equalsIgnoreCase("--dev");
    }

}
