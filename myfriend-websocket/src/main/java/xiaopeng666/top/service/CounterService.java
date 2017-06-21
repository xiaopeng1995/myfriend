package xiaopeng666.top.service;

/**
 * Created by xiaopeng on 2016/11/21.
 */
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xiaopeng666.top.handler.CounterHandler;

@Component
public class CounterService {

    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    CounterHandler counterHandler;

    @Scheduled(fixedDelay = 1000)
    public void sendCounterUpdate() {
        counterHandler.counterIncrementedCallback(counter.incrementAndGet());
    }

    Integer getValue() {
        return counter.get();
    }
}
