package com.tvd12.ezyfox.boot.test.domain.plugin;

import com.tvd12.ezydata.database.EzyDatabaseContext;
import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezydata.jpa.EzyJpaDatabaseContextBuilder;
import com.tvd12.ezydata.jpa.loader.EzyJpaDataSourceLoader;
import com.tvd12.ezydata.jpa.loader.EzyJpaEntityManagerFactoryLoader;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.boot.jpa.EzyJpaDatabaseContextsFactory;
import com.tvd12.ezyfox.reflect.EzyReflection;
import com.tvd12.ezyfox.reflect.EzyReflectionProxy;
import com.zaxxer.hikari.HikariDataSource;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DomainDatabaseContextsFactory implements EzyJpaDatabaseContextsFactory {

    public static final String DEFAULT_DOMAIN = "Default";
    private static final Map<String, DataSource> DATA_SOURCES =
        new ConcurrentHashMap<>();

    @Override
    public List<EzyDatabaseContext> createDatabaseContexts(
        Properties properties,
        Set<String> packagesToScan,
        EzySingletonFactory singletonFactory
    ) {
        System.out.println("\n========== [domain] createDatabaseContexts ==========");
        System.out.println("[domain] packagesToScan: " + packagesToScan);
        EzyReflection reflection = new EzyReflectionProxy(packagesToScan);
        printClasses("[domain] 1. scanned (no filter)", reflection);

        Map<String, Set<String>> packagesByDomain = getPackagesByDomain(properties);
        System.out.println("[domain] 2. config names by domain:");
        packagesByDomain.forEach((domain, names) ->
            System.out.println("    " + domain + " -> " + names)
        );

        Set<String> allDomainPackages = new HashSet<>();
        for (Set<String> names : packagesByDomain.values()) {
            allDomainPackages.addAll(names);
        }
        printDecisions(reflection, packagesByDomain);

        List<EzyDatabaseContext> answer = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : packagesByDomain.entrySet()) {
            String domain = entry.getKey();
            Set<String> domainPackages = entry.getValue();
            EzyReflection domainReflection = new DomainFilteredReflection(
                reflection,
                type -> isInPackages(type, domainPackages)
            );
            printClasses("[domain] 4. filtered reflection of '" + domain + "'", domainReflection);
            if (domainReflection.getAnnotatedClasses(Entity.class).isEmpty()) {
                System.out.println("[domain]    '" + domain + "' has no entity in this module -> skip");
                continue;
            }
            answer.add(
                newDatabaseContext(
                    domain,
                    properties,
                    getDataSource(
                        domain,
                        properties,
                        "domains." + domain + ".datasource"
                    ),
                    domainReflection
                )
            );
        }
        EzyReflection defaultReflection = new DomainFilteredReflection(
            reflection,
            type -> !isInPackages(type, allDomainPackages)
        );
        printClasses("[domain] 4. filtered reflection of '" + DEFAULT_DOMAIN + "'", defaultReflection);
        try {
            answer.add(
                newDatabaseContext(
                    DEFAULT_DOMAIN,
                    properties,
                    getDataSource(DEFAULT_DOMAIN, properties, "datasource"),
                    defaultReflection
                )
            );
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);
            throw e;
        }
        System.out.println("========== [domain] created " + answer.size() + " database contexts ==========\n");
        return answer;
    }

    private static void printClasses(String title, EzyReflection reflection) {
        System.out.println(title + ":");
        System.out.println(
            "    entities: " + simpleNames(reflection.getAnnotatedClasses(Entity.class))
        );
        System.out.println(
            "    repos   : " + simpleNames(reflection.getExtendsClasses(EzyDatabaseRepository.class))
        );
    }

    private static void printDecisions(
        EzyReflection reflection,
        Map<String, Set<String>> packagesByDomain
    ) {
        System.out.println("[domain] 3. filter decision for each class:");
        Set<Class<?>> classes = new HashSet<>(reflection.getAnnotatedClasses(Entity.class));
        classes.addAll(reflection.getExtendsClasses(EzyDatabaseRepository.class));
        List<Class<?>> sortedClasses = new ArrayList<>(classes);
        sortedClasses.sort((a, b) -> a.getName().compareTo(b.getName()));
        for (Class<?> type : sortedClasses) {
            String target = DEFAULT_DOMAIN;
            String reason = "not matched by any domain";
            for (Map.Entry<String, Set<String>> entry : packagesByDomain.entrySet()) {
                String matchedName = findMatchedName(type, entry.getValue());
                if (matchedName != null) {
                    target = entry.getKey();
                    reason = type.getName().equals(matchedName)
                        ? "equals class '" + matchedName + "'"
                        : "startsWith package '" + matchedName + ".'";
                    break;
                }
            }
            System.out.println("    " + type.getName() + " -> " + target + " (" + reason + ")");
        }
    }

    private static String findMatchedName(Class<?> type, Collection<String> names) {
        String className = type.getName();
        for (String name : names) {
            if (className.equals(name) || className.startsWith(name + ".")) {
                return name;
            }
        }
        return null;
    }

    private static List<String> simpleNames(Collection<?> classes) {
        List<String> answer = new ArrayList<>();
        for (Object type : classes) {
            answer.add(((Class<?>) type).getSimpleName());
        }
        Collections.sort(answer);
        return answer;
    }

    private EzyDatabaseContext newDatabaseContext(
        String domain,
        Properties properties,
        DataSource dataSource,
        EzyReflection reflection
    ) {
        EntityManagerFactory entityManagerFactory =
            new EzyJpaEntityManagerFactoryLoader()
                .entityClasses(reflection.getAnnotatedClasses(Entity.class))
                .dataSource(dataSource)
                .properties(properties)
                .load(domain);
        EzyDatabaseContext databaseContext = new EzyJpaDatabaseContextBuilder()
            .properties(properties)
            .entityManagerFactory(entityManagerFactory)
            .scan(reflection)
            .build();
        System.out.println(
            "[domain] 5. database context '" + domain + "' -> " +
                getJdbcUrl(dataSource) +
                "\n    entities in EMF: " +
                simpleNames(reflection.getAnnotatedClasses(Entity.class)) +
                "\n    repos in context: " +
                simpleNames(databaseContext.getRepositories().keySet())
        );
        return databaseContext;
    }

    private static String getJdbcUrl(DataSource dataSource) {
        if (dataSource instanceof HikariDataSource) {
            return ((HikariDataSource) dataSource).getJdbcUrl();
        }
        return String.valueOf(dataSource);
    }

    private DataSource getDataSource(
        String domain,
        Properties properties,
        String keyPrefix
    ) {
        DataSource existed = DATA_SOURCES.get(domain);
        if (existed != null) {
            System.out.println("[domain]    reuse shared data source of '" + domain + "'");
        }
        return DATA_SOURCES.computeIfAbsent(
            domain,
            k -> {
                System.out.println(
                    "[domain]    create data source of '" + domain + "' from '" + keyPrefix + ".*'"
                );
                return new EzyJpaDataSourceLoader()
                    .properties(properties, keyPrefix)
                    .load();
            }
        );
    }

    private static Map<String, Set<String>> getPackagesByDomain(
        Properties properties
    ) {
        Map<String, Set<String>> answer = new LinkedHashMap<>();
        for (String domain : splitByComma(properties.getProperty("domains", ""))) {
            Set<String> names = new HashSet<>();
            names.addAll(
                splitByComma(
                    properties.getProperty("domains." + domain + ".packages", "")
                )
            );
            names.addAll(
                splitByComma(
                    properties.getProperty("domains." + domain + ".classes", "")
                )
            );
            answer.put(domain, names);
        }
        return answer;
    }

    private static List<String> splitByComma(String value) {
        List<String> answer = new ArrayList<>();
        for (String item : value.split(",")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                answer.add(trimmed);
            }
        }
        return answer;
    }

    private static boolean isInPackages(
        Class<?> type,
        Collection<String> names
    ) {
        String className = type.getName();
        for (String name : names) {
            if (className.equals(name) || className.startsWith(name + ".")) {
                return true;
            }
        }
        return false;
    }

    public static DataSource getSharedDataSource(String domain) {
        return DATA_SOURCES.get(domain);
    }

    public static void closeDataSources() {
        for (DataSource dataSource : DATA_SOURCES.values()) {
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        }
        DATA_SOURCES.clear();
    }
}
