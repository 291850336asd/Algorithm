package com.test;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.sun.management.OperatingSystemMXBean;

public class CPUMemory {
    public static int cpuLoad() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osmxb.getSystemCpuLoad();
        int percentCpuLoad = (int) (cpuLoad * 100);
        return percentCpuLoad;
    }

    public static int memoryLoad() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double totalvirtualMemory = osmxb.getTotalPhysicalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();

        double value = freePhysicalMemorySize/totalvirtualMemory;
        int percentMemoryLoad = (int) ((1-value)*100);
        return percentMemoryLoad;

    }

    /**
     * 将字节容量转化为GB
     */
    public static String transformation(long size){
        return size / 1024 / 1024 / 1024 + "GB"+"   ";
    }

    /**
     * 获取系统各个硬盘的总容量、已经使用的容量、剩余容量和使用率
     * @throws IOException
     */
    public static void getDiskInfo() throws IOException {
        DecimalFormat df = new DecimalFormat("#0.00");
        File[] disks = File.listRoots();
        for (File file : disks) {
            // 获取盘符
            System.out.print(file.getCanonicalPath() + "   ");
            // 获取总容量
            long totalSpace = file.getTotalSpace();
            // 获取剩余容量
            long usableSpace = file.getUsableSpace();
            // 获取已经使用的容量
            long freeSpace = totalSpace - usableSpace;
            // 获取使用率
            float useRate = (float)((freeSpace * 1.0 / totalSpace) * 100);
            System.out.print("总容量： " + transformation(totalSpace));
            System.out.print("已经使用： " + transformation(freeSpace));
            System.out.print("剩余容量： " + transformation(usableSpace));
            System.out.println("使用率： " + Double.parseDouble(df.format(useRate)) + "%   ");
        }
    }

    public static void main(String[] args) {

//https://www.quickprogrammingtips.com/java/how-to-use-java-to-get-a-list-of-running-processes-in-linux.html
//        https://stackoverflow.com/questions/54686/how-to-get-a-list-of-current-open-windows-process-with-java
        try{
            Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
            new Thread(() -> {
                List<CpuMemoryInfo> allProcess = new ArrayList<>();
                Scanner sc = new Scanner(process.getInputStream());
                if (sc.hasNextLine()) sc.nextLine();
                double sum = 0;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(",");
                    String unq = parts[0].substring(1).replaceFirst(".$", "");
                    String pid = parts[1].substring(1).replaceFirst(".$", "");
                    CpuMemoryInfo cpuMemoryInfo = new CpuMemoryInfo();

                    if(parts.length>=6){
                        double memory = Integer.parseInt(parts[5].split(" ")[0]) / 1024.00;
                        cpuMemoryInfo.setMemory(memory);
                        sum+=memory;
                        System.out.println("name:" + unq + " pid:" + pid + " mem:" + memory +"G");
                    }
                    cpuMemoryInfo.setPid(pid);
                    cpuMemoryInfo.setpName(unq);
                    allProcess.add(cpuMemoryInfo);

                }
                System.out.println(sum);
            }).start();
            process.waitFor();


//            Process process = Runtime.getRuntime().exec
//                    (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
//            BufferedReader r =  new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line = null;
//
//            while((line=r.readLine())!=null) {
//                System.out.println();
//            }
        }catch (Exception e){
            e.printStackTrace();
        }



//        while (true){
//            try{
//
//                System.out.println(cpuLoad()+2 + "---" + memoryLoad());
//                getDiskInfo();
//                Thread.sleep(1000);
//            }catch (Exception e){
//
//            }
//        }
    }



    private static class CpuMemoryInfo{
        String pid;
        double memory;
        String pName;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public double getMemory() {
            return memory;
        }

        public void setMemory(double memory) {
            this.memory = memory;
        }

        public String getpName() {
            return pName;
        }

        public void setpName(String pName) {
            this.pName = pName;
        }
    }
}
