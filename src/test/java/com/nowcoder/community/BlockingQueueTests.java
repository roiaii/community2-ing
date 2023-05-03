package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(20);
        new Thread(new Producer(queue)).start();
        new Thread((new Consumer(queue))).start();
        new Thread((new Consumer(queue))).start();
        new Thread((new Consumer(queue))).start();
    }
}

class Producer implements Runnable {
    private BlockingQueue<Integer> queue;
    Lock lock = new ReentrantLock();
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        //lock.lock();
        try{
            for(int i=0; i<100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产：" + queue.size());
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //lock.unlock();
        }
    }
}

//消费者
class Consumer implements Runnable {
    BlockingQueue<Integer> queue;
    Lock lock = new ReentrantLock();
    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        //lock.lock();
        try {
            while(true){
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费：" + queue.size());
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            //lock.unlock();
        }
    }
}
