package org.kaikikm.threadresloader.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.kaikikm.dummy.TestClass;
import org.kaikikm.threadresloader.ResourceLoader;

import com.google.common.io.Files;

/**
 * 
 *
 */
public class TestThreadResLoader {
    private static final String CREATED_FILE = "test_add.txt";
    /**
     * 
     */
    @Test
    public void testClasspathResourceLoading() {
        assertNotNull(ResourceLoader.getResource("test.txt"));
        assertNotNull(ResourceLoader.getResource("testfolder"));
        assertNotNull(ResourceLoader.getResource("testfolder/test1.txt"));
    }

    /**
     * 
     * @throws IOException 
     */
    @Test
    public void testCustomClasspathResourceLoading() throws IOException {
        final File dir = Files.createTempDir();
        final File testFile = new File(dir.getAbsolutePath() + File.separator + CREATED_FILE);
        assertTrue(testFile.createNewFile());
        ResourceLoader.setURLs(new URL[]{dir.toURI().toURL()});
        assertNotNull(ResourceLoader.getResource("test_add.txt"));
        FileUtils.deleteDirectory(dir);
    }

    /**
     * 
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Test
    public void testParentThreadClasspathResourceLoading() throws IOException, InterruptedException {
        final File dir = Files.createTempDir();
        assertTrue(new File(dir.getAbsolutePath() + File.separator + CREATED_FILE).createNewFile());
        ResourceLoader.setURLs(new URL[]{dir.toURI().toURL()});
        assertNotNull(ResourceLoader.getResource("test_add.txt"));
        TestThread t = new TestThread() {
            @Override
            public void run() {
                setResource(ResourceLoader.getResource(CREATED_FILE));
            }
        };
        t.start();
        t.join();
        assertNotNull(t.getResource());
        t = new TestThread() {
            @Override
            public void run() {
                ResourceLoader.setDefault();
                setResource(ResourceLoader.getResource(CREATED_FILE));
            }
        };
        t.start();
        t.join();
        assertNull(t.getResource());
        assertNotNull(ResourceLoader.getResource(CREATED_FILE));
        FileUtils.deleteDirectory(dir);
    }

    /**
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws MalformedURLException 
     * 
     */
    @Test
    public void testClassLoading() throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, MalformedURLException {
        try {
            ResourceLoader.classForName("TestClass");
            fail();
        } catch (ClassNotFoundException e) {
            assertNotNull(e.getMessage());
        }
        /*
         * add new folder to classpath and check JAR existence
         */
        final File root = new File(System.getProperty("user.dir"));
        final File jarLocation = new File(root, "externTestResources");
        ResourceLoader.addURL(jarLocation.toURI().toURL());
        assertNotNull("Exists dummy jar file on: " + jarLocation.getPath(), ResourceLoader.getResource("DummyTestClasses.jar"));
        /*
         * Add JAR
         */
        ResourceLoader.addURL(new File(jarLocation, "DummyTestClasses.jar").toURI().toURL());
        /*
         * test new class
         */
        Class<?> c = ResourceLoader.classForName("org.kaikikm.dummy.TestClass2");
        Object o =  c.getDeclaredConstructor().newInstance();
        Method method = c.getDeclaredMethod("dummyMethod2");
        assertEquals(2, method.invoke(o));
        /*
         * test old class (must override old class)
         */
        assertEquals(1, new TestClass().dummyMethod1());
        c = ResourceLoader.classForName("org.kaikikm.dummy.TestClass");
        o = c.getDeclaredConstructor().newInstance();
        method = c.getDeclaredMethod("dummyMethod3");
        assertEquals(3, method.invoke(o));
    }

    private static class TestThread extends Thread {
        private URL resource;
        public URL getResource() {
            return this.resource;
        }
        public void setResource(final URL resource) {
            this.resource = resource;
        }
    }
}
