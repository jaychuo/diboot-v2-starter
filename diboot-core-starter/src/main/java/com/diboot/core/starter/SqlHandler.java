package com.diboot.core.starter;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.SqlExecutor;
import com.diboot.core.util.V;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * SQL处理类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/08/01
 */
public class SqlHandler {
    private static final Logger logger = LoggerFactory.getLogger(SqlHandler.class);

    // 数据字典SQL
    public static final String DICTIONARY_SQL = "SELECT id FROM dictionary WHERE id=0";

    /***
     * 初始化安装SQL
     * @return
     */
    public static void initBootstrapSql(Class inst, String jdbcUrl, String sqlPath){
        logger.info("diboot 初始化SQL执行开始:");
        extractAndExecuteSqls(inst, jdbcUrl, sqlPath);
        logger.info("diboot 初始化SQL执行完成！");
    }

    /**
     * 检查SQL文件是否已经执行过
     * @param sqlStatement
     * @return
     */
    public static boolean checkIsTableExists(String sqlStatement){
        // 获取SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) ContextHelper.getBean(SqlSessionFactory.class);
        if(sqlSessionFactory == null){
            logger.warn("无法获取SqlSessionFactory实例，安装SQL将无法执行，请手动安装！");
            return false;
        }
        try(SqlSession session = sqlSessionFactory.openSession(); Connection conn = session.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlStatement)){
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            if(meta.getColumnCount() > 0){
                rs.close();
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    /***
     * 从SQL文件读出的行内容中 提取SQL语句并执行
     * @param sqlPath
     * @return
     */
    public static boolean extractAndExecuteSqls(Class inst, String jdbcUrl, String sqlPath){
        List<String> sqlFileReadLines = readLinesFromResource(inst, sqlPath);
        if(V.isEmpty(sqlFileReadLines)){
            return false;
        }
        // 解析SQL
        List<String> sqlStatementList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(String line : sqlFileReadLines){
            if(line.contains("--")){
                line = line.substring(0, line.indexOf("--"));
            }
            sb.append(" ");
            if(line.contains(";")){
                // 语句结束
                sb.append(line.substring(0, line.indexOf(";")));
                String cleanSql = buildPureSqlStatement(sb.toString(), jdbcUrl);
                sqlStatementList.add(cleanSql);
                sb.setLength(0);
                if(line.indexOf(";") < line.length()-1){
                    String leftSql = line.substring(line.indexOf(";")+1);
                    if(V.notEmpty(leftSql)){
                        sb.append(leftSql);
                    }
                }
            }
            else if(V.notEmpty(line)){
                sb.append(line);
            }
        }
        if(sb.length() > 0){
            String cleanSql = buildPureSqlStatement(sb.toString(), jdbcUrl);
            sqlStatementList.add(cleanSql);
            sb.setLength(0);
        }
        // 返回解析后的SQL语句
        return executeMultipleUpdateSqls(sqlStatementList);
    }

    /**
     * 构建纯净可执行的SQL语句: 去除注释，替换变量
     * @param sqlStatement
     * @param jdbcUrl
     * @return
     */
    protected static String buildPureSqlStatement(String sqlStatement, String jdbcUrl){
        sqlStatement = clearComments(sqlStatement);
        // 替换sqlStatement中的变量，如{SCHEMA}
        if(sqlStatement.contains("${SCHEMA}")){
            String schema = extractSchema(jdbcUrl);
            sqlStatement = S.replace(sqlStatement, "${SCHEMA}", schema);
        }
        return sqlStatement;
    }

    /***
     * 执行多条批量更新SQL
     * @param sqlStatementList
     * @return
     */
    public static boolean executeMultipleUpdateSqls(List<String> sqlStatementList){
        if(V.isEmpty(sqlStatementList)){
            return false;
        }
        for(String sqlStatement : sqlStatementList){
            try{
                boolean success = SqlExecutor.executeUpdate(sqlStatement, null);
                if(success){
                    logger.info("初始化SQL执行完成: "+ S.substring(sqlStatement, 0, 60) + "...");
                }
            }
            catch (Exception e){
                logger.error("初始化SQL执行异常，请检查或手动执行", e);
            }
        }
        return true;
    }

    /***
     * 获取
     * @param inst
     * @return
     */
    protected static List<String> readLinesFromResource(Class inst, String sqlPath){
        List<String> lines = null;
        try{
            InputStream is = inst.getClassLoader().getResourceAsStream(sqlPath);
            lines = IOUtils.readLines(is, "UTF-8");
        }
        catch (FileNotFoundException fe){
            logger.warn("暂未发现数据库SQL: "+sqlPath + "， 请参考其他数据库定义DDL手动初始化。");
        }
        catch (Exception e){
            logger.warn("读取SQL文件异常: "+sqlPath, e);
        }
        return lines;
    }

    /***
     * 剔除SQL中的注释，提取可执行的实际SQL
     * @param inputSql
     * @return
     */
    private static String clearComments(String inputSql){
        String[] sqlRows = inputSql.split("\\n");
        List<String> cleanSql = new ArrayList();
        for(String row : sqlRows){
            // 去除行注释
            if(row.contains("--")){
                row = row.substring(0, row.indexOf("--"));
            }
            if(V.notEmpty(row.trim())){
                cleanSql.add(row);
            }
        }
        inputSql = S.join(cleanSql, " ");

        // 去除多行注释
        inputSql = removeMultipleLineComments(inputSql);
        // 去除换行
        return inputSql.replaceAll("\r|\n", " ");
    }

    /***
     * 去除多行注释
     * @param inputSql
     * @return
     */
    private static String removeMultipleLineComments(String inputSql){
        if(inputSql.contains("*/*")){
            //忽略此情况，避免死循环
            return inputSql;
        }
        if(inputSql.contains("/*") && inputSql.contains("*/")){
            inputSql = inputSql.substring(0, inputSql.lastIndexOf("/*")) + inputSql.substring(inputSql.indexOf("*/")+2, inputSql.length());
        }
        if(inputSql.contains("/*") && inputSql.contains("*/")){
            return removeMultipleLineComments(inputSql);
        }
        return inputSql;
    }

    /**
     * 提取数据库类型
     * @param jdbcUrl
     * @return
     */
    public static String extractDatabase(String jdbcUrl){
        DbType dbType = JdbcUtils.getDbType(jdbcUrl);
        String dbName = dbType.getDb();
        if(dbName.startsWith(DbType.SQL_SERVER.getDb()) && !dbName.equals(DbType.SQL_SERVER.getDb())){
            dbName = DbType.SQL_SERVER.getDb();
        }
        return dbName;
    }

    /**
     * 提取Schema信息
     * @param jdbcUrl
     * @return
     */
    private static String[] JDBCURL_KEYWORDS = {"DatabaseName=","database=", "/", ":"}, SUFFIX_KEYWORDS = {"?", ";"};
    public static String extractSchema(String jdbcUrl){
        for (String keyword : JDBCURL_KEYWORDS) {
            if(S.contains(jdbcUrl, keyword)){
                jdbcUrl = S.substringAfterLast(jdbcUrl, keyword);
                break;
            }
        }
        for (String keyword : SUFFIX_KEYWORDS) {
            if(S.contains(jdbcUrl, keyword)){
                jdbcUrl = S.substringBefore(jdbcUrl, keyword);
            }
        }
        return jdbcUrl;
    }

}