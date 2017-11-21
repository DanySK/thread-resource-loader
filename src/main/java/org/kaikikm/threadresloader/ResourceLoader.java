package org.kaikikm.threadresloader;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * Static class that offers a thead-dependent resource loading. Each thread has independent, configurable, runtime-modifiable classpath for resource loading.
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
     * Adds given urls to default.
     * 
     *  @param urls Urls to add
     */
    public static void setURLs(final URL... urls) {
        ResourceLoader.CLASS_LOADER.set(new AccessibleURLClassLoader(urls));
    }

    /**
    * Set current thread resource loader with default thread resource loading settings, specified by {@link Thread#setContextClassLoader(ClassLoader)}.
    * Adds given urls to default.
    * 
    *  @param urls Urls to add
    */
    public static void setURLs(final Collection<URL> urls) {
        setURLs(urls.toArray(new URL[urls.size()]));
    }

    /**
     * Set current thread resource loader with parent thread resource loading settings(default {@link ResourceLoader} behavior).
     * Adds given urls to default.
     * 
     *  @param urls Urls to add
     */
    public static void injectURLs(final URL... urls) {
        ResourceLoader.CLASS_LOADER.set(new AccessibleURLClassLoader(CLASS_LOADER.get(), urls));
    }

    /**
     * Set current thread resource loader with parent thread resource loading settings(default {@link ResourceLoader} behavior).
     * Adds given urls to default.
     * 
     *  @param urls Urls to add
     */
    public static void injectURLs(final Collection<URL> urls) {
        injectURLs(urls.toArray(new URL[urls.size()]));
    }

    /**
     * Get a classpath resource's URL. If resource loader settings aren't previously specified it uses the parent thread settings.
     * 
     * @param path Resource's path
     * @return Resource's URL
     */
    public static URL getResource(final String path) {
        return CLASS_LOADER.get().getResource(path);
    }

    /**
    * Get a classpath resource's URL. If resource loader settings aren't previously specified it uses the parent thread settings.
    * 
    * @param path Resource's path
    * @return Resource's URL
    */
    public static InputStream getResourceAsStream(final String path) {
        return CLASS_LOADER.get().getResourceAsStream(path);
    }

    /**
     * Add url to serach resources for current thread.
     * @param url 
     */
    public static void addURL(final URL url) {
        ResourceLoader.CLASS_LOADER.get().addURL(url);
    }

    /**
     * 
     * @return Class loader that library uses for current thread
     */
    public static ClassLoader getClassLoader() {
        return ResourceLoader.CLASS_LOADER.get();
    }

    /**
     * 
     * @param name Class name
     * @throws ClassNotFoundException 
     */
    public static void classForName(final String name) throws ClassNotFoundException {
        Class.forName(name, true, ResourceLoader.CLASS_LOADER.get());
    }

}
