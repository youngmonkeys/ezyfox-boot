package com.tvd12.ezyfox.boot.test.domain;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.bean.impl.EzyBeanKey;
import com.tvd12.ezyfox.boot.jpa.EzyJpaConfiguration;
import com.tvd12.ezyfox.boot.test.domain.plugin.DomainDatabaseConfiguration;
import com.tvd12.ezyfox.boot.test.domain.plugin.DomainDatabaseContextsFactory;
import com.tvd12.ezyfox.boot.test.domain.service.DomainService;
import com.tvd12.ezyfox.boot.test.ecommerce.entity.Product;
import com.tvd12.ezyfox.boot.test.ecommerce.entity.ProductBook;
import com.tvd12.ezyfox.boot.test.ezyplatform.entity.DataIndex;
import com.tvd12.ezyfox.boot.test.ezyplatform.entity.User;
import com.tvd12.properties.file.reader.BaseFileReader;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DomainDatabaseContextsTest {

    private static final String CORE_DB = "ezyfox_boot_test_core";
    private static final String DATA_INDEX_DB = "ezyfox_boot_test_data_index";
    private static final String PRODUCT_DB = "ezyfox_boot_test_product";
    private static final String[] DATABASES = {
        CORE_DB,
        DATA_INDEX_DB,
        PRODUCT_DB
    };
    private static final Set<String> PACKAGES_TO_SCAN = new HashSet<>(
        Arrays.asList(
            "com.tvd12.ezyfox.boot.test.ezyplatform",
            "com.tvd12.ezyfox.boot.test.ecommerce",
            "com.tvd12.ezyfox.boot.test.domain.service"
        )
    );
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
        "CREATE TABLE IF NOT EXISTS `([^`]+)`"
    );
    private static final Properties PROPERTIES = new BaseFileReader()
        .read("application-domain_test.properties");

    private EzyBeanContext beanContext;

    @BeforeClass
    public void setUp() throws Exception {
        createDatabasesAndTables();
        beanContext = newBeanContext();
    }

    @AfterClass
    public void tearDown() {
        DomainDatabaseContextsFactory.closeDataSources();
    }

    @Test
    public void findDatabaseContextsFactoryByObjectType() {
        assertTrue(
            beanContext.getSingletonFactory().getSingleton(
                EzyBeanKey.of("sharedDatabaseContextsFactory", Object.class)
            ) != null,
            "sharedDatabaseContextsFactory must be found by Object.class"
        );
    }

    private static void createDatabasesAndTables() throws Exception {
        try (
            Connection connection = newConnection("");
            Statement statement = connection.createStatement()
        ) {
            for (String database : DATABASES) {
                if (databaseExists(connection, database)) {
                    System.out.println("database " + database + " existed");
                } else {
                    statement.execute(
                        "CREATE DATABASE `" + database + "` " +
                            "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci"
                    );
                    System.out.println("database " + database + " created");
                }
                for (String sql : readSqlStatements("domain-test/" + database + ".sql")) {
                    Matcher matcher = CREATE_TABLE_PATTERN.matcher(sql);
                    assertTrue(matcher.find(), "invalid create table sql: " + sql);
                    String table = matcher.group(1);
                    if (tableExists(connection, database, table)) {
                        System.out.println("table " + database + "." + table + " existed");
                        continue;
                    }
                    statement.execute("USE `" + database + "`");
                    statement.execute(sql);
                    System.out.println("table " + database + "." + table + " created");
                }
            }
        }
    }

    private static boolean databaseExists(
        Connection connection,
        String database
    ) throws SQLException {
        try (
            PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM information_schema.schemata " +
                    "WHERE schema_name = ?"
            )
        ) {
            statement.setString(1, database);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1) > 0;
            }
        }
    }

    private static boolean tableExists(
        Connection connection,
        String database,
        String table
    ) throws SQLException {
        try (
            PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = ? AND table_name = ?"
            )
        ) {
            statement.setString(1, database);
            statement.setString(2, table);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1) > 0;
            }
        }
    }

    private static List<String> readSqlStatements(String resource) throws Exception {
        URL url = DomainDatabaseContextsTest.class
            .getClassLoader()
            .getResource(resource);
        assertTrue(url != null, "missing resource: " + resource);
        String content = new String(
            Files.readAllBytes(Paths.get(url.toURI())),
            StandardCharsets.UTF_8
        );
        List<String> answer = new ArrayList<>();
        for (String sql : content.split(";")) {
            if (!sql.trim().isEmpty()) {
                answer.add(sql.trim());
            }
        }
        return answer;
    }

    private static EzyBeanContext newBeanContext() {
        return EzyBeanContext.builder()
            .disableAutoConfiguration()
            .scan(PACKAGES_TO_SCAN)
            .addConfigurationBeforeClasses(
                DomainDatabaseConfiguration.class,
                EzyJpaConfiguration.class
            )
            .activeProfiles("domain_test")
            .build();
    }

    @Test
    public void saveAndFindThroughDomainService() throws SQLException {
        DomainService domainService = beanContext.getSingleton(DomainService.class);
        assertTrue(domainService != null, "missing DomainService");
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String suffix = String.valueOf(System.currentTimeMillis());

        User user = new User();
        user.setUuid("uuid-" + suffix);
        user.setUsername("user-" + suffix);
        user.setPassword("password-" + suffix);
        user.setStatus("ACTIVATED");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        domainService.saveUser(user);

        Product product = new Product();
        product.setProductName("product-" + suffix);
        product.setProductCode("code-" + suffix);
        product.setAmount(BigInteger.TEN);
        product.setSoldAmount(BigInteger.ZERO);
        product.setProductType("BOOK");
        product.setCreatorUserId(user.getId());
        product.setStatus("PUBLISHED");
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        domainService.saveProduct(product);

        ProductBook productBook = new ProductBook();
        productBook.setProductId(product.getId());
        productBook.setAuthor("author-" + suffix);
        productBook.setAuthorUserId(user.getId());
        productBook.setBookType("PAPER");
        productBook.setCoverType("HARD");
        productBook.setPages(100);
        productBook.setReleasedAt(now);
        domainService.saveProductBook(productBook);

        DataIndex dataIndex = new DataIndex();
        dataIndex.setDataType("product");
        dataIndex.setDataId(product.getId());
        dataIndex.setKeyword("keyword-" + suffix);
        dataIndex.setPriority(1);
        dataIndex.setCreatedAt(now);
        dataIndex.setUpdatedAt(now);
        domainService.saveDataIndex(dataIndex);

        assertTrue(user.getId() > 0, "user id must be generated");
        assertTrue(product.getId() > 0, "product id must be generated");
        assertTrue(dataIndex.getId() > 0, "data index id must be generated");

        User foundUser = domainService.getUserById(user.getId());
        System.out.println("user: " + foundUser);
        assertEquals(foundUser.getId(), user.getId(), "user.id");
        assertEquals(foundUser.getUuid(), user.getUuid(), "user.uuid");
        assertEquals(foundUser.getUsername(), user.getUsername(), "user.username");
        assertEquals(foundUser.getStatus(), user.getStatus(), "user.status");
        assertEquals(foundUser.getCreatedAt(), user.getCreatedAt(), "user.createdAt");

        Product foundProduct = domainService.getProductById(product.getId());
        System.out.println("product: " + foundProduct);
        assertEquals(foundProduct.getId(), product.getId(), "product.id");
        assertEquals(foundProduct.getProductName(), product.getProductName(), "product.productName");
        assertEquals(foundProduct.getProductCode(), product.getProductCode(), "product.productCode");
        assertEquals(foundProduct.getAmount(), product.getAmount(), "product.amount");
        assertEquals(foundProduct.getSoldAmount(), product.getSoldAmount(), "product.soldAmount");
        assertEquals(foundProduct.getCreatorUserId(), user.getId(), "product.creatorUserId");
        assertEquals(foundProduct.getStatus(), product.getStatus(), "product.status");

        ProductBook foundProductBook = domainService.getProductBookByProductId(product.getId());
        System.out.println("product book: " + foundProductBook);
        assertEquals(foundProductBook.getProductId(), product.getId(), "productBook.productId");
        assertEquals(foundProductBook.getAuthor(), productBook.getAuthor(), "productBook.author");
        assertEquals(foundProductBook.getAuthorUserId(), user.getId(), "productBook.authorUserId");
        assertEquals(foundProductBook.getPages(), productBook.getPages(), "productBook.pages");
        assertEquals(foundProductBook.getReleasedAt(), productBook.getReleasedAt(), "productBook.releasedAt");

        DataIndex foundDataIndex = domainService.getDataIndexById(dataIndex.getId());
        System.out.println("data index: " + foundDataIndex);
        assertEquals(foundDataIndex.getId(), dataIndex.getId(), "dataIndex.id");
        assertEquals(foundDataIndex.getDataType(), dataIndex.getDataType(), "dataIndex.dataType");
        assertEquals(foundDataIndex.getDataId(), product.getId(), "dataIndex.dataId");
        assertEquals(foundDataIndex.getKeyword(), dataIndex.getKeyword(), "dataIndex.keyword");
        assertEquals(foundDataIndex.uniqueKey(), dataIndex.uniqueKey(), "dataIndex.uniqueKey");

        checkRowIn(CORE_DB, "ezy_users", "id", user.getId());
        checkRowIn(DATA_INDEX_DB, "ezy_data_indices", "id", dataIndex.getId());
        checkRowIn(PRODUCT_DB, "ecommerce_products", "id", product.getId());
        checkRowIn(PRODUCT_DB, "ecommerce_product_books", "product_id", product.getId());
    }

    @Test
    public void createTablesInTheirOwnDatabases() throws SQLException {
        for (String database : DATABASES) {
            System.out.println("tables of " + database + ": " + getTables(database));
        }
        assertTrue(
            getTables(CORE_DB).equals(Arrays.asList("ezy_users")),
            "core database must only have ezy_users"
        );
        assertTrue(
            getTables(DATA_INDEX_DB).equals(Arrays.asList("ezy_data_indices")),
            "data index database must only have ezy_data_indices"
        );
        assertTrue(
            getTables(PRODUCT_DB).equals(
                Arrays.asList("ecommerce_product_books", "ecommerce_products")
            ),
            "product database must only have ecommerce_product_books, ecommerce_products"
        );
    }

    @Test
    public void shareDataSourcesBetweenModules() {
        DataSource coreDataSource = DomainDatabaseContextsFactory
            .getSharedDataSource(DomainDatabaseContextsFactory.DEFAULT_DOMAIN);
        DataSource productDataSource = DomainDatabaseContextsFactory
            .getSharedDataSource("product");

        DomainService otherModuleDomainService = newBeanContext()
            .getSingleton(DomainService.class);
        assertTrue(
            otherModuleDomainService.getUserById(Long.MAX_VALUE) == null,
            "other module must query the core database"
        );

        assertTrue(
            DomainDatabaseContextsFactory.getSharedDataSource(
                DomainDatabaseContextsFactory.DEFAULT_DOMAIN
            ) == coreDataSource,
            "core data source must be shared between modules"
        );
        assertTrue(
            DomainDatabaseContextsFactory.getSharedDataSource("product")
                == productDataSource,
            "product data source must be shared between modules"
        );
        System.out.println("data sources are shared between modules");
    }

    private static List<String> getTables(String database) throws SQLException {
        List<String> answer = new ArrayList<>();
        try (
            Connection connection = newConnection(CORE_DB);
            PreparedStatement statement = connection.prepareStatement(
                "SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = ? ORDER BY table_name"
            )
        ) {
            statement.setString(1, database);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    answer.add(resultSet.getString(1));
                }
            }
        }
        return answer;
    }

    private static void checkRowIn(
        String database,
        String table,
        String idColumn,
        long id
    ) throws SQLException {
        try (
            Connection connection = newConnection(database);
            PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM " + table + " WHERE " + idColumn + " = ?"
            )
        ) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                assertTrue(
                    resultSet.getLong(1) == 1,
                    "missing row " + id + " in " + database + "." + table
                );
            }
        }
    }

    private static Connection newConnection(String database) throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mariadb://localhost:3306/" + database +
                "?allowPublicKeyRetrieval=true&useSSL=false",
            PROPERTIES.getProperty("datasource.username"),
            PROPERTIES.getProperty("datasource.password")
        );
    }

}
