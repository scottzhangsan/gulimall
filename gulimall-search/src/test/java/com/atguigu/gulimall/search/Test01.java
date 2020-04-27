package com.atguigu.gulimall.search;

import org.junit.Test;

import java.util.concurrent.*;

public class Test01 {

    private //创建默认的线程池
            //核心的线程数是1，最大的线程数为2,任务数少于最大的线程时10s后开始减少线程数，使用LinkedBlocking阻塞队列
            /**
             * 工作原理
             * 1：线程池创建，准备好core数量的核心线程，准备接收任务
             * core满了，就将进来的任务放入阻塞队列中，空闲的core就会自己去阻塞队列中获取任务执行
             * 2:阻塞队列满了就直接开新的线程执行，最大只能到max指定的数量
             * max 满了就有 RejectedExecutorHandler拒绝任务
             * max都执行完成，有很多空闲，在指定的时间keepAliveTime以后释放max-core这些线程
             */
            ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 10, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @Test
    public void test() {

        System.out.println("test start....");
        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            int i = 10 / 2;
            System.out.println("运行的结果是：" + i);
        }, executor);
        System.out.println("test end....");
        System.out.println(Thread.currentThread().getName());
    }


    @Test
    public void test01() throws Exception {
        CompletableFuture future = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "Hello World";
                }, executor
        );
        System.out.println(future.get());
        System.out.println("end");
    }

    @Test
    public void test02() throws Exception {
        // fuction 函数，接收一个参数，返回一个返回值
        //场景，执行任务 A，执行任务B，待任务B执行完成后，用B的返回值区执行任务C
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() ->
                {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("执行任务A");
                    return "任务A";
                },
                executor);
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> {
                    System.out.println("执行任务B");
                    return "任务B";
                }
                , executor);
        CompletableFuture<String> futurec = futureB.thenApply((b) -> {
            System.out.println("执行任务C");
            System.out.println("参数：" + b);
            return "a";
        });
        System.out.println(futurec.get());
    }

    @Test
    public void test03() throws Exception {
        //当前任务完成以后执行，当前任务的执行结果会作为下一个任务的输入参数，有返回值
        //场景，多个任务串联执行，下一个任务的执行依赖上一个任务的结果，每个任务都有输入和输出
        //异步执行任务A，当任务A完成时使用 A的返回结果result作为入参进行任务B的处理，可以实现多个任务的串联执行。
        CompletableFuture futureA = CompletableFuture.supplyAsync(() -> "Hello", executor);
        CompletableFuture futureB = futureA.thenApply((a) -> a + " World");
        CompletableFuture futureC = futureB.thenApply((b) -> b);
        System.out.println(futureC.join());

    }

    @Test
    public void test04() throws Exception {

        // whenComplete 可以处理正常和异常的计算结果
        // exceptionally处理异常情况，
        // whenComplete和 whenCompleteAsync的区别
        // whenComplete:是执行当前任务的线程继续执行whenComplete的任务
        // whenCompleteAsync 是执行把 whenCompleteAsync 这个任务提交给线程池来执行
           /*CompletableFuture<Integer>  future = CompletableFuture.supplyAsync(
                   ()->{
                       System.out.println("开始执行任务");
                       return  10/0 ;}
                   ,executor).whenComplete((t,u)->{
               System.out.println("异步任务成功的完成了：结果是"+t+"异常："+u);
           })  ;*/

        //handle执行完成后的结果，还可以改变返回的值
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(
                () -> {
                    System.out.println("开始执行任务");
                    return 10 / 0;
                }

                , executor).handle((r, t) -> {
            return 10;
        });
        System.out.println(future.get());
        Thread.sleep(100);
    }

    @Test
    public void test05() throws Exception {

        // thenApplyAsync 当一个线程依赖另一个线程的返回结果时，使用另一个线程的返回结果并获取当前线程的返回结果的值
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("开始执行第一步骤");
            return 10;
        }, executor).thenApplyAsync(
                (r) -> {
                    System.out.println("开始执行第二步骤");
                    return r + 20;
                });
        System.out.println(future.get());
        Thread.sleep(100);
    }

    @Test
    public void test06() throws Exception {
        //thenCombineAsync 联合 futureA和futureB的返回结果，然后在返回相关的数据
        CompletableFuture<Integer> futureA = CompletableFuture.supplyAsync(() -> 10, executor);
        CompletableFuture<Integer> futureB = CompletableFuture.supplyAsync(() -> 20, executor);
        CompletableFuture futureC = futureA.thenCombineAsync(futureB, (r1, r2) -> {
            System.out.println("r1的值为：" + r1 + ":r2的值为:" + r2);
            return r1 + r2;
        });
        System.out.println(futureC.get());
        Thread.sleep(200);
    }

    @Test
    public void test07() throws ExecutionException, InterruptedException {
        System.out.println("start...");
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品信息1");
            return "future1";
        }, executor);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品信息2");
            return "future2";
        }, executor);

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品信息3");
            return "future3";
        }, executor);

        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(future1, future2, future3);
        voidCompletableFuture.get();
        System.out.println("end...future1的结果：" + future1.get() + ",future2的结果：" + future2.get() + ",future3的结果：" + future3.get());
    }

}
