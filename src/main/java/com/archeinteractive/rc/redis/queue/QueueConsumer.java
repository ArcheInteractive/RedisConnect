package com.archeinteractive.rc.redis.queue;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class QueueConsumer implements Runnable {
    private Logger logger;
    private CopyOnWriteArrayList<Queue> queue;
    private int position = 0;

    public QueueConsumer(Logger logger, CopyOnWriteArrayList<Queue> queue) {
        this.logger = logger;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            Queue q = take();

            if (q == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    logger.info("exiting");
                }

                continue;
            }

            if (q.getMessage().equalsIgnoreCase("exit")) {
                break;
            }

            if (q != null) {
                if (q.conditionsMet()) {
                    logger.info(q.process());
                    complete(q);
                }
            }
        }
    }

    private Queue take() {
        if (position >= queue.size()) {
            position = 0;
        }

        Queue q = queue.isEmpty() ? null : queue.get(position++);
        return q;
    }

    private void complete(Queue q) {
        queue.remove(q);
    }
}
