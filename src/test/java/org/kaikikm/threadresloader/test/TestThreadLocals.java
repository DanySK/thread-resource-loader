package org.kaikikm.threadresloader.test;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import org.junit.Test;

/**
 * 
 *
 */
public class TestThreadLocals {

    private static InheritableThreadLocal<Integer> val = new InheritableThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
        @Override
        protected Integer childValue(final Integer parentValue) {
            return parentValue + 1;
        }
    };

    /**
     * 
     * @throws InterruptedException 
     */
    @Test
    public void testThreadLocals() throws InterruptedException {
        final ArrayList<Integer> arr = new ArrayList<>(10);
        arr.add(0, val.get());
        assertEquals(Integer.valueOf(0), arr.get(0));
        final CountDownLatch cl = new CountDownLatch(2);
        new TestThread(1, cl, arr, () -> null) {
            @Override
            public void operation() {
                new TestThread(2, cl, arr, () -> val.get()) {
                    @Override
                    public void operation() {
                    }
                }.start();
            }
        }.start();
        cl.await();
        assertEquals(Integer.valueOf(2), arr.get(2));
    }

    /**
     * 
     * @throws InterruptedException 
     */
    @Test
    public void testThreadLocalsInit() throws InterruptedException {
        final ArrayList<Integer> arr = new ArrayList<>(10);
        arr.add(0, null);
        final CountDownLatch cl = new CountDownLatch(2);
        new TestThread(1, cl, arr, () -> val.get()) {
            @Override
            public void operation() {
                new TestThread(2, cl, arr, () -> val.get()) {
                    @Override
                    public void operation() {
                    }
                }.start();
            }
        }.start();
        cl.await();
        assertEquals(Integer.valueOf(1), arr.get(2));
    }

    private abstract class TestThread extends Thread {
        private final int id;
        private final CountDownLatch cl;
        private final List<Integer> res;
        private final Supplier<Integer> sup;

        /**
         * 
         * @param id 
         * @param cl 
         * @param res 
         * @param sup 
         */
        TestThread(final int id, final CountDownLatch cl, final List<Integer> res, final Supplier<Integer> sup) {
            this.id = id;
            this.cl = cl;
            this.res = res;
            this.sup = sup;
        }

        @Override
        public void run() {
            this.res.add(id, sup.get());
            this.operation();
            cl.countDown();
        }

        public abstract void operation();
    }
}
