package com.ityongman.delegate.simple;

import java.util.HashMap;
import java.util.Map;

public class Leader implements IWorker {

    private Map<String, IWorker> workers = new HashMap<>();

    public Leader() {
        workers.put("java", new WorkerA());
        workers.put("php", new WorkerB());
        workers.put("go", new WorkerC());
    }

    public void doWork(String cmd) {
        IWorker worker = workers.get(cmd);
        worker.doWork(cmd);
    }
}
