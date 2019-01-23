package fr.mrmicky.factionrankup.utils;

import java.util.Optional;

/**
 * @author MrMicky
 */
public class VersionUtils {

    public static Optional<Class<?>> getClass(String className) {
        try {
           return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
