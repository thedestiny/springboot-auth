package com.platform.authmpg.web;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Priority;
import java.lang.management.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-06 3:42 PM
 */

@Order

@Slf4j
@RestController
public class IndexController {





    @Async("customExecutor") // 指定线程池
    public CompletableFuture<String> processAsyncTask(String taskId) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("start index!");
            return "start" + taskId;
        });

        // 耗时操作
        // return CompletableFuture.completedFuture("success" + taskId);
    }


    @GetMapping(value = "index")
    public String index() {
        return "success";
    }

    @GetMapping(value = "information")
    public String information() {
        showJvmInfo();
        showMemoryInfo();
        showSystem();
        showClassLoading();
        showCompilation();
        showThread();
        showGarbageCollector();
        showMemoryManager();
        showMemoryPool();
        return "success";
    }


    public static void showJvmInfo() {
        RuntimeMXBean rtMxBean = ManagementFactory.getRuntimeMXBean();
        System.out.println("Java 虚拟机的运行时系统(RuntimeMXBean):");
        System.out.println("jvm vendor:" + rtMxBean.getVmVendor());
        System.out.println("jvm name:" + rtMxBean.getVmName());
        System.out.println("jvm version:" + rtMxBean.getVmVersion());
        System.out.println("jvm bootClassPath:" + rtMxBean.getBootClassPath());
        System.out.println("jvm start time:" + rtMxBean.getStartTime());
    }


    /**
     * Java 虚拟机的内存系统
     */
    public static void showMemoryInfo() {
        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMxBean.getHeapMemoryUsage();
        System.out.println("Java 虚拟机的内存系统(MemoryMXBean):");
        System.out.println("Heap " + heap.toString());
        System.out.println(
                "Heap" +
                        " init:" + heap.getInit() + byte2Mb(heap.getInit()) +
                        " used:" + byte2Mb(heap.getUsed()) +
                        " committed:" + byte2Mb(heap.getCommitted()) +
                        " max:" + byte2Mb(heap.getMax()));
        System.out.println("\n");
    }

    private static String byte2Mb(long source) {
        return "(" + source / 1024 / 1024 + "mb)";
    }

    /**
     * Java 虚拟机在其上运行的操作系统
     */
    public static void showSystem() {
        OperatingSystemMXBean operatingSystemMxBean = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("Java 虚拟机在其上运行的操作系统(OperatingSystemMXBean):");
        System.out.println("Architecture(操作系统架构): " + operatingSystemMxBean.getArch());
        System.out.println("Processors(Java虚拟机可用的处理器数): " + operatingSystemMxBean.getAvailableProcessors());
        System.out.println("System name(操作系统名称): " + operatingSystemMxBean.getName());
        System.out.println("System version(操作系统版本): " + operatingSystemMxBean.getVersion());
        System.out.println("Last minute load(最后一分钟的系统负载平均值): " + operatingSystemMxBean.getSystemLoadAverage());
        System.out.println("\n");
    }

    /**
     * Java 虚拟机的类加载系统
     */
    public static void showClassLoading() {
        ClassLoadingMXBean classLoadingMxBean = ManagementFactory.getClassLoadingMXBean();
        System.out.println("Java 虚拟机的类加载系统(ClassLoadingMXBean):");
        System.out.println("TotalLoadedClassCount(加载的类总数): " + classLoadingMxBean.getTotalLoadedClassCount());
        System.out.println("LoadedClassCount(当前加载的类的数量)" + classLoadingMxBean.getLoadedClassCount());
        System.out.println("UnloadedClassCount(卸载类的总数):" + classLoadingMxBean.getUnloadedClassCount());
        System.out.println("\n");
    }

    /**
     * Java 虚拟机的编译系统
     */
    public static void showCompilation() {
        CompilationMXBean compilationMxBean = ManagementFactory.getCompilationMXBean();
        System.out.println("Java 虚拟机的编译系统(CompilationMXBean):");
        System.out.println("TotalCompilationTime(编译时间（毫秒）):" + compilationMxBean.getTotalCompilationTime());
        System.out.println("name(JIT编译器的名称):" + compilationMxBean.getName());
        System.out.println("\n");
    }

    /**
     * Java 虚拟机的线程系统
     */
    public static void showThread() {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        System.out.println("Java 虚拟机的线程系统(ThreadMXBean):");
        System.out.println("ThreadCount(当前活动线程数)" + threadMxBean.getThreadCount());
        System.out.println("PeakThreadCount(峰值实时线程计数)" + threadMxBean.getPeakThreadCount());
        System.out.println("TotalStartedThreadCount(启动的线程总数)" + threadMxBean.getTotalStartedThreadCount());
        System.out.println("DaemonThreadCount(当前活动后台进程线程数)" + threadMxBean.getDaemonThreadCount());
        System.out.println("isSynchronizerUsageSupported(虚拟机是否支持监视可下载同步器的使用情况)" + threadMxBean.isSynchronizerUsageSupported());
        System.out.println("AllThreadIds(所有活动线程ID):" + JSONObject.toJSONString(threadMxBean.getAllThreadIds()));
        System.out.println("CurrentThreadUserTime(当前线程在用户模式下执行的CPU时间（以纳秒为单位）)" + threadMxBean.getCurrentThreadUserTime());
        for (ThreadInfo threadInfo : threadMxBean.getThreadInfo(threadMxBean.getAllThreadIds(), 1)) {
            System.out.print(threadInfo.getThreadId() + threadInfo.toString());
        }
        System.out.println("\n");
    }

    /**
     * Java 虚拟机中的垃圾回收器。
     */
    public static void showGarbageCollector() {
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.println("Java 虚拟机中的垃圾回收器(GarbageCollectorMXBean):");
        for (GarbageCollectorMXBean collectorMxBean : garbageCollectorMXBeans) {
            System.out.println("name(垃圾收集器名称):" + collectorMxBean.getName());
            System.out.println("--CollectionCount:" + collectorMxBean.getCollectionCount());
            System.out.println("--CollectionTime" + collectorMxBean.getCollectionTime());
            System.out.println("\n");
        }
        System.out.println("\n");
    }

    /**
     * Java 虚拟机中的内存管理器
     */
    public static void showMemoryManager() {
        List<MemoryManagerMXBean> memoryManagerMxBeans = ManagementFactory.getMemoryManagerMXBeans();
        System.out.println("Java 虚拟机中的内存管理器(MemoryManagerMXBean):");
        for (MemoryManagerMXBean managerMxBean : memoryManagerMxBeans) {
            System.out.println("name(内存管理器名称):" + managerMxBean.getName());
            System.out.println("--MemoryPoolNames:" + String.join(",", managerMxBean.getMemoryPoolNames()));
            System.out.println("\n");
        }
    }

    /**
     * Java 虚拟机中的内存池
     */
    public static void showMemoryPool() {
        List<MemoryPoolMXBean> memoryPoolMxBeans = ManagementFactory.getMemoryPoolMXBeans();
        System.out.println("Java 虚拟机中的内存池(MemoryPoolMXBean):");
        for (MemoryPoolMXBean memoryPoolMxBean : memoryPoolMxBeans) {
            System.out.println("name:" + memoryPoolMxBean.getName());
            System.out.println("--CollectionUsage:" + memoryPoolMxBean.getCollectionUsage());
            System.out.println("--type:" + memoryPoolMxBean.getType());
            System.out.println("\n");
        }
        System.out.println("\n");
    }

}
