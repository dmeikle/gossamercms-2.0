package com.gossamercms.mvc.config;

import com.gossamercms.mvc.decorators.MdcTaskDecorator;
import com.gossamercms.mvc.filters.CorrelationFilter;
import com.gossamercms.mvc.filters.DefaultResponseStatusFilter;
import com.gossamercms.mvc.handlers.DefaultResponseStatusAdvice;
import com.gossamercms.mvc.interceptors.DefaultResponseStatusMetadataInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@AutoConfiguration
@Import(GossamerModuleImportSelector.class)
public class GossamerUnifiedAutoConfig {


    @Bean("taskExecutor")
    @ConditionalOnMissingBean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("app-async-");
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public Executor applicationTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        return taskExecutor;
    }

    @Bean
    public FilterRegistrationBean<CorrelationFilter> correlationFilter() {
        FilterRegistrationBean<CorrelationFilter> reg = new FilterRegistrationBean<>(new CorrelationFilter());
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<DefaultResponseStatusFilter> defaultResponseStatusFilter() {
        FilterRegistrationBean<DefaultResponseStatusFilter> reg =
                new FilterRegistrationBean<>(new DefaultResponseStatusFilter());
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return reg;
    }

    @Bean
    public DefaultResponseStatusAdvice defaultResponseStatusAdvice() {
        return new DefaultResponseStatusAdvice();
    }

    @Bean
    public DefaultResponseStatusMetadataInterceptor defaultResponseStatusMetadataInterceptor() {
        return new DefaultResponseStatusMetadataInterceptor();
    }

    @Bean
    public WebMvcConfigurer defaultResponseStatusWebMvcConfigurer(
            DefaultResponseStatusMetadataInterceptor defaultResponseStatusMetadataInterceptor
    ) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(defaultResponseStatusMetadataInterceptor);
            }
        };
    }

}