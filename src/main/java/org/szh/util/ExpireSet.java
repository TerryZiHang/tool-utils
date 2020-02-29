package org.szh.util;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 重复通知过滤 时效
 * 
 * @author Terry Zi
 * @param <K>
 */
public class ExpireSet<K> extends HashSet<K> {

	private static final long serialVersionUID = -1488591748185474774L;

	/** 周期秒 */
	private int period = 60;
	
	private Set<K> set= this;

	private Queue<KeyValue<K>> queue = new ConcurrentLinkedQueue<ExpireSet<K>.KeyValue<K>>();

	/** 延迟性周期执行线程池 ，性能优于Timer JDK1.5后不用Timer*/
	private static ScheduledExecutorService scheduledExecutorService = 
			new ScheduledThreadPoolExecutor(2, new BasicThreadFactory.Builder().daemon(true).build());

	@Override
	public boolean add(K e) {
		queue.offer(new KeyValue<K>(e, System.currentTimeMillis() + 1000 * period));
		return super.add(e);
	}
	
	public ExpireSet(){
		scheduledExecutorService.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				KeyValue<K> kv = null;
				long currentTime = System.currentTimeMillis();
				while(true) {
					kv = queue.peek();
					if(kv != null && kv.value < currentTime) {
						set.remove(kv.key);
						queue.poll();
					}else {
						break;
					}
				}
			}
		}, 10 * 1000, 60 * 1000, TimeUnit.MILLISECONDS);
	}

	public ExpireSet(int period) {
		this.period = period;
	}

	protected class KeyValue<KK> {
		KK key;
		long value;

		public KeyValue(KK key, long value) {
			this.key = key;
			this.value = value;
		}
	}
	
	public static void main(String[] args) {
		ExpireSet<String> expireSet = new ExpireSet<>(60);
		expireSet.add("12312323123123");
	}

}
