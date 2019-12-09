package nioselector.niodata;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 泛型队列
 * FIFO
 * */
public class TQueue<T> {

    private Object lockObj = new Object();

    private Queue<T> queue = new LinkedList<T>();

    /**
     * 获取队列的长度
     * */
    public Integer getQueueSize(){
        return queue.size();
    }
    /***
     * 进队
     * @param t
     */
    public void DeQueue(T t){
        synchronized (lockObj){
            queue.offer(t);
        }
    }
    /**
     * 出队
     * @return T
     * */
    public T EnQueue(){
        synchronized (lockObj) {
            return queue.poll();
        }
    }
}

