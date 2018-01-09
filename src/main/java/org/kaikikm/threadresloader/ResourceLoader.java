package org.kaikikm.threadresloader;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * Static class that offers a thread-dependent resource loading. Each thread has independent, configurable, runtime-modifiable classpath for resource loading.
 *
 */
public final class ResourceLoader {

    private ResourceLoader() {
    }

    private static final InheritableThreadLocal<AccessibleURLClassLoader> CLASS_LOADER = new InheritableThreadLocal<AccessibleURLClassLoader>() {
        @Override
        protected AccessibleURLClassLoader initialValue() {
            return new AccessibleURLClassLoader();
        }
        @Override
        protected AccessibleURLClassLoader childValue(final AccessibleURLClassLoader parentValue) {
            return new AccessibleURLClassLoader(parentValue);
        }
    };

    /**
     * Set current thread resource loader with default thread resource loading settings, specified by {@link Thread#setContextClassLoader(ClassLoader)}. 
     */
    public static void setDefault() {
        ResourceLoader.CLASS_LOADER.set(new AccessibleURLClassLoader());
    }

    /**
     * Set current thread resource loader with default thread resource loading settings, specified by {@link Thread#setContextClassLoader(ClassLoader)}.
     * Adds given URLs to default.
     * 
     *  @param urls URLs that have to be added
     */
    public static void setURLs(final URL... urls) {
        ResourceLoader.CLASS_LOADER.set(new AccessibleURLClassLoader(urls));
    }

    /**
    * Set current thread resource loader with default thread resource loading settings, specified by {@link Thread#setContextClassLoader(ClassLoader)}.
    * Adds given URLs to default.
    * 
    *  @param urls URLs that have to be added
    */
    public static void setURLs(final Collection<URL> urls) {
        setURLs(urls.toArray(new URL[urls.size()]));
    }

    /**
     * Set current thread resource loader with parent thread resource loading settings (default {@link ResourceLoader} behavior).
     * Adds given URLs to default.
     * 
     *  @param urls URLs that have to be added
     */
    public static void injectURLs(final URL... urls) {
        ResourceLoader.CLASS_LOADER.set(new AccessibleURLClassLoader(CLASS_LOADER.get(), urls));
    }

    /**
     * Set current thread resource loader with parent thread resource loading settings (default {@link ResourceLoader} behavior).
     * Adds given URLs to default.
     * 
     *  @param urls URLs that have to be added
     */
    public static void injectURLs(final Collection<URL> urls) {
        injectURLs(urls.toArray(new URL[urls.size()]));
    }

    /**
     * Get resource's URL. If resource loader settings aren't previously specified it uses the parent thread settings.
     * 
     * @param path Resource's path
     * @return Resource's URL
     */
    public static URL getResource(final String path) {
        return CLASS_LOADER.get().getResource(path);
    }

    /**
    * Get resource's {@link InputStream}. If resource loader settings aren't previously specified it uses the parent thread settings.
    * 
    * @param path Resource's path
    * @return Resource's URL
    */
    public static InputStream getResourceAsStream(final String path) {
        return CLASS_LOADER.get().getResourceAsStream(path);
    }

    /**
     * Add URL to for resource searching for current thread.
     * @param url 
     */
    public static void addURL(final URL url) {
        ResourceLoader.CLASS_LOADER.get().addURL(url);
    }

    /**
     * 
     * @return Class Loader that library uses for current thread
     */
    public static ClassLoader getClassLoader() {
        return ResourceLoader.CLASS_LOADER.get();
    }

    /**
     * Returns the Class object associated with the class or interface with the given string name.
     * Invoking this method is equivalent to: 
     * <blockquote>
     *  {@code ResourceLoader.classForName(className, true)}
     * </blockquote>
     * 
     * @param name Fully qualified name of the desired class
     * @return Class object representing the desired class
     * @throws ClassNotFoundException 
     */
    public static Class<?> classForName(final String name) throws ClassNotFoundException {
        return classForName(name, true);
    }

    /**
     * Returns the Class object associated with the class or interface with the given string name. Classes on
     * local classpath will override classes with same name that are in the system classpath or in the parent thread
     * classpath (reverse behavior compared to standard java {@link ClassLoader})
     * 
     * @param initialize If true the class will be initialized
     * @param name Fully qualified name of the desired class
     * @return Class Object representing the desired class
     * @throws ClassNotFoundException 
     */
    public static Class<?> classForName(final String name, final boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, ResourceLoader.CLASS_LOADER.get());
    }

}
