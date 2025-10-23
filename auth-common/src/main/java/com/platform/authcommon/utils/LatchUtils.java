package com.platform.authcommon.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-10-23 2:49 PM
 */
public class LatchUtils {

    LinkedList<TaskInfo> taskInfos = new LinkedList<>();

    private static final ThreadLocal<List<TaskInfo>> THREADLOCAL = ThreadLocal.withInitial(LinkedList::new);

    public static void submitTask(Executor executor, Runnable runnable) {
        THREADLOCAL.get().add(new TaskInfo(executor, runnable));
    }

    private static List<TaskInfo> popTask() {
        List<TaskInfo> taskInfos = THREADLOCAL.get();
        THREADLOCAL.remove();
        return taskInfos;
    }

    public static boolean waitFor(long timeout, TimeUnit timeUnit) {
        List<TaskInfo> taskInfos = popTask();
        if (taskInfos.isEmpty()) {
            return true;
        }
        CountDownLatch latch = new CountDownLatch(taskInfos.size());
        for (TaskInfo taskInfo : taskInfos) {
            Executor executor = taskInfo.executor;
            Runnable runnable = taskInfo.runnable;
            executor.execute(() -> {
                try {
                    runnable.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        boolean await = false;
        try {
            await = latch.await(timeout, timeUnit);
        } catch (Exception ignored) {
        }
        return await;
    }

    private static final class TaskInfo {
        private final Executor executor;
        private final Runnable runnable;

        public TaskInfo(Executor executor, Runnable runnable) {
            this.executor = executor;
            this.runnable = runnable;
        }
    }

    public static void main(String[] args) {
        // 1. 准备一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        System.out.println("主流程开始，准备分发异步任务...");

        // 2. 提交多个异步任务
        // 任务一：获取用户信息
        LatchUtils.submitTask(executorService, () -> {
            try {
                System.out.println("开始获取用户信息...");
                Thread.sleep(1000); // 模拟耗时
                System.out.println("获取用户信息成功！");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 任务二：获取订单信息
        LatchUtils.submitTask(executorService, () -> {
            try {
                System.out.println("开始获取订单信息...");
                Thread.sleep(1500); // 模拟耗时
                System.out.println("获取订单信息成功！");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 任务三：获取商品信息
        LatchUtils.submitTask(executorService, () -> {
            try {
                System.out.println("开始获取商品信息...");
                Thread.sleep(500); // 模拟耗时
                System.out.println("获取商品信息成功！");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });


        System.out.println("所有异步任务已提交，主线程开始等待...");

        // 3. 等待所有任务完成，最长等待5秒
        boolean allTasksCompleted = LatchUtils.waitFor(5, TimeUnit.SECONDS);

        // 4. 根据等待结果继续主流程
        if (allTasksCompleted) {
            System.out.println("所有异步任务执行成功，主流程继续...");
        } else {
            System.err.println("有任务执行超时，主流程中断！");
        }

        // 5. 关闭线程池
        executorService.shutdown();

    }
}
