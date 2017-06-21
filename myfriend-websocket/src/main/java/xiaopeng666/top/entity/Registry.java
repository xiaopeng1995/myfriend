package xiaopeng666.top.entity;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mqtt 客户端连接注册表
 */
public enum Registry {

    // instance
    INSTANCE;

    public Map<String, Object> value = new ConcurrentHashMap<>();

    // connect pool
    private final ExecutorService es = Executors.newFixedThreadPool(5000);

    // save 配置值
    public void saveKey(String key, Object value) {
        this.value.put(key, value);
    }


    /**
     * @return AgentID_TotWhExp, AgentID_Job, AgentID_TotWhImp, AgentID_Soc, AgentIDstorage01
     * AgentID_STROAGE_002Config,AgentID_date,startDate,AgentID_packing
     * agents
     */
    public Map<String, Object> getValue() {
        return this.value;
    }

    public void startJob(Thread job) {
        this.es.execute(job);

    }

    public void shutdown() {
        this.es.shutdown();
    }
}
