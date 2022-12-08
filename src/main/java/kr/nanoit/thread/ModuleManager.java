package kr.nanoit.thread;

public interface ModuleManager {

    static ModuleManager moduleManager(){
        return new ModuleManagerImpl();
    }
    boolean register(Module... modules);

    boolean unregister(String uuid);

    long moduleTotal();

    long total();

    long running();

    long terminated();
}
