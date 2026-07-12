package com.gossamercms.mvc.config;

import com.gossamercms.mvc.decorators.MdcTaskDecorator;
import com.gossamercms.mvc.filters.CorrelationFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

}