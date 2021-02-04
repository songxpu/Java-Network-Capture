package pro.soft.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadFactory {
    //单例模式
    private static ThreadFactory instance = new ThreadFactory();
    public static ThreadFactory getInstance(){
        return instance;
    }
    //单例进程池
    private ExecutorService singleThreadExecutor;

    public ThreadFactory() {
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
    }



    public void getAllInterface(){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "多线程嘻嘻嘻");
                try {
                    Thread.sleep(2000);
                    System.out.println("sleep finished");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
