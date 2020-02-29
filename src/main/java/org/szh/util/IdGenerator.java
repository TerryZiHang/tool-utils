package org.szh.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * snowflake算法生成ID：
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T
 * = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * 
 * @author Administrator
 * @date 2019年2月20日
 */
public class IdGenerator {

    /** 开始时间戳(2019-01-01) **/
    private final long twepoch = 1546272000000L;

    /** 机器id所占的位数 **/
    private final long workIdBits = 5L;

    /** 数据标识id所占的位数 **/
    private final long datacenterIdBits = 5L;

    /** 机器最大支持位数31 **/
    private final long maxWorkId = -1L ^ (-1L << workIdBits);

    /** 支持的最大机器id 也是31 **/
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** 序列在id中所占的位数 **/
    private final long sequenceBits = 12L;

    /** 机器id向左偏移的12位 **/
    private final long workIdShift = sequenceBits;

    /** 数据标识id向左偏移的17位 **/
    private final long datacenterIdShift = sequenceBits + workIdBits;

    /** 时间戳向左偏移的22位 **/
    private final long timestampLeftShift = sequenceBits + workIdBits + datacenterIdBits;

    /** 序列掩码 4095 **/
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    /** 工作机器ID(0~31) */
    private long workId;

    /** 数据中心ID(0~31) */
    private long datacenterId;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    private static IdGenerator idGenerator = new IdGenerator();

    private IdGenerator() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workId = getMaxWorkId(datacenterId, maxWorkId);
    }

    /**
     * 获取workId=0, datacenterId=0的对象
     * 
     * @date 2019年4月23日 上午11:29:55
     * @return
     */
    public static IdGenerator getInstance() {
        return idGenerator;
    }
    
    public static String getId() {
        return idGenerator.nexId();
    }

    public IdGenerator(long workId, long datacenterId) {
        if (workId > maxWorkId || workId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workId = workId;
        this.datacenterId = datacenterId;
    }

    public long getWorkId() {
        return workId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    /**
     * 描述：获取下一个ID
     */
    public synchronized String nexId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }

        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
                | (workId << workIdShift) | sequence;
        return String.valueOf(id);

    }

    /**
     * 描述：阻塞下一个毫秒获取新的时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 描述：获取当前系统时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取 maxWorkerId
     */
    protected static long getMaxWorkId(long datacenterId, long maxWorkerId) {
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 数据标识id部分
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDatacenterId + 1);
            }
        } catch (Exception e) {
            System.out.println(" getDatacenterId: " + e.getMessage());
        }
        return id;
    }

    public static void main(String[] args) {
//        IdGenerator t = IdGenerator.getInstance();
//        System.out.println(t.workId + "   " + t.datacenterId);
//        System.out.println(t.nexId());
        Thread thread = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
//                  System.out.println(IdGenerator.getId());
                  IdGenerator t = IdGenerator.getInstance();
                  System.out.println("1-" + t.nexId());
              }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
//                  System.out.println(IdGenerator.getId());
                  IdGenerator t = IdGenerator.getInstance();
                  System.out.println("2-" + t.nexId());
              }
            }
        });
        thread.start();
        thread2.start();
    }
}
