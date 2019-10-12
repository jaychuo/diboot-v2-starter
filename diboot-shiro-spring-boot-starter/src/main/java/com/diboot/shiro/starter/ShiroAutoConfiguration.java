package com.diboot.shiro.starter;

import com.diboot.core.util.V;
import com.diboot.shiro.authz.aop.CustomAuthorizationAttributeSourceAdvisor;
import com.diboot.shiro.authz.cache.MemoryCondition;
import com.diboot.shiro.authz.cache.RedisCacheManager;
import com.diboot.shiro.authz.cache.RedisCondition;
import com.diboot.shiro.authz.storage.AuthorizationStorage;
import com.diboot.shiro.authz.storage.StorageListener;
import com.diboot.shiro.jwt.BaseJwtAuthenticationFilter;
import com.diboot.shiro.jwt.BaseJwtRealm;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro自动配置框架
 * @author : wee
 * @version : v1.0
 * @Date 2019-10-11  10:54
 */
@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
@ComponentScan(basePackages={"com.diboot.shiro"})
@MapperScan(basePackages = {"com.diboot.shiro.mapper"})
@Order(1)
public class ShiroAutoConfiguration {

    @Autowired
    private ShiroProperties shiroProperties;

    /**
     * 注入权限存储
     * @return
     */
    @Bean
    public AuthorizationStorage authorizationStorage() {
        return new AuthorizationStorage(shiroProperties.getConfiguration().getAuth().getEnv().getEnv(),
                shiroProperties.getConfiguration().getAuth().isStorage());
    }

    /**
     * 自动存储类
     * @return
     */
    @DependsOn({"authorizationStorage"})
    @Bean
    public StorageListener storageListener(){
        StorageListener storageListener = new StorageListener();
        storageListener.setAuthorizationStorage(authorizationStorage());
        return storageListener;
    }

    /**
     * 将数据缓存到内存中
     * @return
     */
    @Bean("cacheManager")
    @Conditional(MemoryCondition.class)
    public CacheManager memoryCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    /**
     * 将数据存储到redis缓存
     * @return
     */
    @Bean("cacheManager")
    @Conditional(RedisCondition.class)
    public CacheManager redisCacheManager()  {
        return new RedisCacheManager();
    }

    @Bean
    public Realm realm(){
        BaseJwtRealm realm = new BaseJwtRealm();
        if (shiroProperties.getConfiguration().getCache().isPermissionCachingEnabled()) {
            //设置权限缓存
            realm.setCachingEnabled(true);
            CacheManager cacheManager = V.notEmpty(redisCacheManager())? redisCacheManager(): memoryCacheManager();
            realm.setCacheManager(cacheManager);
        }
        return realm;
    }

    @Bean
    public org.apache.shiro.mgt.SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(org.apache.shiro.mgt.SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //Shiro securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //用户访问未对其授权的资源时的错误提示页面
        shiroFilterFactoryBean.setUnauthorizedUrl(V.notEmpty(shiroProperties.getConfiguration().getErrorUrl()) ? shiroProperties.getConfiguration().getErrorUrl() : "/error");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinition());

        // 设置过滤器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("anon", new AnonymousFilter());
        filters.put("jwt", new BaseJwtAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(filters);

        return shiroFilterFactoryBean;
    }

    @Bean
    public Map filterChainDefinition(){
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/error/**", "anon");
        filterChainDefinitionMap.put("/*.html", "anon");
        filterChainDefinitionMap.put("/diboot/**", "anon");

        filterChainDefinitionMap.put("/auth/login", "anon");
        filterChainDefinitionMap.put("/auth/buildOAuthUrl", "anon");
        filterChainDefinitionMap.put("/auth/apply", "anon");
        filterChainDefinitionMap.put("/auth/register", "anon");
        filterChainDefinitionMap.put("/auth/static", "anon");
        //设置错误的统一跳转地址
        String errorUrl = V.notEmpty(shiroProperties.getConfiguration().getErrorUrl()) ? shiroProperties.getConfiguration().getErrorUrl() : "/error";
        filterChainDefinitionMap.put(errorUrl, "anon");
        filterChainDefinitionMap.put("/auth/logout", "logout");
        if (V.notEmpty(shiroProperties.getConfiguration().getIgnoreAuthUrls())) {
            String[] ignoreUrlArr = shiroProperties.getConfiguration().getIgnoreAuthUrls().split(",");
            for (String ignoreUrl : ignoreUrlArr) {
                filterChainDefinitionMap.put(ignoreUrl, "anon");
            }
        }
        filterChainDefinitionMap.put("/**", "jwt");

        return filterChainDefinitionMap;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /***
     * 以下两个为使用注解权限相关的配置
     * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public CustomAuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        CustomAuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new CustomAuthorizationAttributeSourceAdvisor(shiroProperties.getConfiguration().getAuth());
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }
}
