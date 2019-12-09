package nioselector.niodata;

import java.util.LinkedList;
import java.util.Queue;

/**
 * ���Ͷ���
 * FIFO
 * */
public class TQueue<T> {

    private Object lockObj = new Object();

    private Queue<T> queue = new LinkedList<T>();

    /**
     * ��ȡ���еĳ���
     * */
    public Integer getQueueSize(){
        return queue.size();
    }
    /***
     * ����
     * @param t
     */
    public void DeQueue(T t){
        synchronized (lockObj){
            queue.offer(t);
        }
    }
    /**
     * ����
     * @return T
     * */
    public T EnQueue(){
        synchronized (lockObj) {
            return queue.poll();
        }
    }
}

