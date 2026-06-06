package com.sloth.boot.starter.monitor.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统资源端点，暴露 {@code /actuator/systemResources}。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Endpoint(id = "systemResources")
public class SystemResourceEndpoint {

    @ReadOperation
    public Map<String, Object> systemResources() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        Map<String, Object> result = new LinkedHashMap<>();

        // CPU: 0-1 ratio (frontend multiplies by 100)
        double loadAverage = osBean.getSystemLoadAverage();
        int processors = osBean.getAvailableProcessors();
        result.put("cpuUsage", loadAverage >= 0 && processors > 0 ? loadAverage / processors : 0);
        result.put("cpuCores", processors);

        // Memory: bytes
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        result.put("memoryUsed", heapUsage.getUsed());
        result.put("memoryTotal", heapUsage.getMax() > 0 ? heapUsage.getMax() : heapUsage.getCommitted());

        // Disk: bytes
        File[] roots = File.listRoots();
        long diskTotal = 0;
        long diskUsed = 0;
        if (roots != null) {
            for (File root : roots) {
                diskTotal += root.getTotalSpace();
                diskUsed += root.getTotalSpace() - root.getUsableSpace();
            }
        }
        result.put("diskUsed", diskUsed);
        result.put("diskTotal", diskTotal);

        return result;
    }
}
