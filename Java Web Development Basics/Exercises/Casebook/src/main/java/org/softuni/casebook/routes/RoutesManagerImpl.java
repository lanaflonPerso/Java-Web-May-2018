package org.softuni.casebook.routes;

import org.softuni.casebook.annotations.Controller;
import org.softuni.casebook.annotations.GetMapping;
import org.softuni.casebook.annotations.PostMapping;
import org.softuni.casebook.controllers.BaseController;
import org.softuni.javache.http.HttpSessionStorage;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RoutesManagerImpl implements RoutesManager {
    private static final String CONTROLLERS_FOLDER_PATH = "C:\\SoftUni\\Java-Web-May-2018\\Java Web Development Basics\\Exercises\\Casebook\\src\\main\\java\\org\\softuni\\casebook\\controllers\\";
    private static final String FILE_PATH_DELIMITER = "\\\\src\\\\main\\\\java\\\\";
    private static final String JAVA_FILE_EXTENSION = ".java";

    private Map<String, ControllerMethodEntry<Method, BaseController>> getMappingRoutes;
    private Map<String, ControllerMethodEntry<Method, BaseController>> postMappingRoutes;

    public RoutesManagerImpl() {
        this.getMappingRoutes = new HashMap<>();
        this.postMappingRoutes = new HashMap<>();
    }

    private void initializeMappingRoutes(File[] files, HttpSessionStorage sessionStorage) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String classPath;
        for (File file : files) {
            if (file.isDirectory()) {
                File[] currentFiles = file.listFiles();
                if (currentFiles != null) {
                    initializeMappingRoutes(currentFiles, sessionStorage);
                }
            } else {
                if (file.getName().endsWith(".java")) {
                    classPath = file.getPath().split(FILE_PATH_DELIMITER)[1].replace("\\", ".").replace(JAVA_FILE_EXTENSION, "");
                    Class<?> clazz = Class.forName(classPath);
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        Constructor<?> controllerConstructor = clazz.getDeclaredConstructor(HttpSessionStorage.class);
                        BaseController controller = (BaseController) controllerConstructor.newInstance(sessionStorage);
                        Method[] methods = clazz.getMethods();
                        for (Method method : methods) {
                            ControllerMethodEntry<Method, BaseController> entry = new ControllerMethodEntry<>(method, controller);
                            method.setAccessible(true);
                            if (method.isAnnotationPresent(GetMapping.class)) {
                                String route = method.getAnnotation(GetMapping.class).route();
                                this.getMappingRoutes.putIfAbsent(route, entry);
                            } else if (method.isAnnotationPresent(PostMapping.class)) {
                                String route = method.getAnnotation(PostMapping.class).route();
                                this.postMappingRoutes.putIfAbsent(route, entry);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initializeRoots(HttpSessionStorage sessionStorage) {
        try {
            File[] files = new File(CONTROLLERS_FOLDER_PATH).listFiles();
            if (files != null) {
                initializeMappingRoutes(files, sessionStorage);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, ControllerMethodEntry<Method, BaseController>> getGetMappingRoutes() {
        return Collections.unmodifiableMap(this.getMappingRoutes);
    }

    @Override
    public Map<String, ControllerMethodEntry<Method, BaseController>> getPostMappingRoutes() {
        return Collections.unmodifiableMap(this.postMappingRoutes);
    }
}
