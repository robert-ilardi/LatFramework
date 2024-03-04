/**
 * Created Feb 25, 2024
 */
package com.ilardi.systems.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * @author robert.ilardi
 *
 */
class ObjectPoolTest {

  static class TestObject {
    private String name;
    private long useCnt;

    public TestObject(String name) {
      this.name = name;
      useCnt = 0;
    }

    public synchronized void increment() {
      useCnt++;
    }

    public synchronized void decrement() {
      useCnt--;
    }

    public synchronized long getUseCnt() {
      return useCnt;
    }

    public String getName() {
      return name;
    }
  }

  static class TestObjectAllocDealloc implements PoolObjectAllocDealloc<TestObject> {

    private int objCnt;

    public TestObjectAllocDealloc() {
      objCnt = 0;
    }

    @Override
    public synchronized TestObject allocate() throws IlardiSystemsException {
      StringBuilder sb = new StringBuilder();
      String tmp;

      sb.append("TestObject-");

      sb.append(objCnt);
      objCnt++;

      sb.append("-");

      sb.append(System.currentTimeMillis());

      tmp = sb.toString();
      sb = null;

      return new TestObject(tmp);
    }

    @Override
    public synchronized void deallocate(TestObject obj) throws IlardiSystemsException {}

  }

  private ObjectPool<TestObject> testPool = null;

  static class PoolTestRunner implements Runnable {
    private ObjectPool<TestObject> testPool;

    private int maxSleepSecs;

    private volatile boolean doTesting;

    private final Object runLock;

    public PoolTestRunner(ObjectPool<TestObject> testPool, int maxSleepSecs) {
      this.testPool = testPool;

      runLock = new Object();

      doTesting = true;
    }

    public boolean isDoTesting() {
      synchronized (runLock) {
        return doTesting;
      }
    }

    public void setDoTesting(boolean doTesting) {
      synchronized (runLock) {
        this.doTesting = doTesting;
        runLock.notifyAll();
      }
    }

    @Override
    public void run() {
      try {
        TestObject to;

        while (doTesting) {
          //Reserve Object ----------->
          to = testPool.reserve();

          //Do Something ----------->
          to.increment();

          //Wait Around ----------->
          SystemUtils.SleepRandom(maxSleepSecs);

          //Release Object ----------->
          testPool.release(to);

          to = null;
        } //End while doTesting
      } //End try block
      catch (Exception e) {
        e.printStackTrace();
        fail(e);
      }
    }
  }

  public ObjectPoolTest() {}

  @Test
  void testPool() {
    ArrayList<Thread> tLst;
    ArrayList<PoolTestRunner> rLst;
    long start, end, cur;
    int minObjs, maxObjs, tCnt, maxThreadSleepSecs;
    Thread t;
    PoolTestRunner ptr;

    try {
      minObjs = 0;
      maxObjs = 10;
      tCnt = 25;
      maxThreadSleepSecs = 5;

      testPool = new ObjectPool<TestObject>(minObjs, maxObjs, new TestObjectAllocDealloc());

      tLst = new ArrayList<Thread>();
      rLst = new ArrayList<PoolTestRunner>();

      for (int i = 0; i < tCnt; i++) {
        ptr = new PoolTestRunner(testPool, maxThreadSleepSecs);
        t = new Thread(ptr);

        tLst.add(t);
        rLst.add(ptr);

        t.start();
      }

      start = System.currentTimeMillis();
      end = start + (1 * 60 * 1000);
      cur = System.currentTimeMillis();

      while (cur <= end) {
        Thread.sleep(100);
        cur = System.currentTimeMillis();
      } //End while loop

      for (int i = 0; i < tLst.size(); i++) {
        ptr = rLst.get(i);
        ptr.setDoTesting(false);
      }

      for (int i = 0; i < tLst.size(); i++) {
        t = tLst.get(i);
        t.join();
      }

      testPool.deallocatePool(true);
    } //End try block
    catch (Exception e) {
      e.printStackTrace();
      fail(e);
    }
  }

}
