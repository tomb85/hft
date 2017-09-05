package hft;

import com.neovisionaries.ws.client.WebSocket;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HeartbeatMonitor {

    private static final Logger LOG = Logger.getLogger(HeartbeatMonitor.class);

    private final WebSocket websocket;
    private final String sessionId;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public HeartbeatMonitor(WebSocket websocket, String sessionId) {
        this.websocket = websocket;
        this.sessionId = sessionId;
        startMonitoringThread();
    }

    private void startMonitoringThread() {
        Thread thread = new Thread(() -> {
            LOG.info("Started heartbeat monitoring");
            lock.lock();
            try {
                while (true) {
                    boolean received = condition.await(10, TimeUnit.SECONDS);
                    if (!received) {
                        break;
                    }
                }
                LOG.info("Missed heartbeat. Disconnecting the websocket");
                websocket.disconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "HeartbeatMonitor-" + sessionId);
        thread.setDaemon(true);
        thread.start();
    }

    public void onHeartbeat() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}