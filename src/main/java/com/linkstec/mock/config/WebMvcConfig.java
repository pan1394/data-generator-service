package com.linkstec.mock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by smlz on 2019/3/19.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	/**
	 * 注册拦截器
	 *
	 * @param registry
	 */
	public void addInterceptors(InterceptorRegistry registry) {

	}

	/**
	 * 注册一个filter
	 *
	 * @return
	 */
	/*
	 * @Bean public FilterRegistrationBean tulingFilter(){ FilterRegistrationBean
	 * filterRegistrationBean = new FilterRegistrationBean();
	 * filterRegistrationBean.setFilter(new TulingFilter());
	 * filterRegistrationBean.addUrlPatterns("
	 */
	/*
	 * "); return filterRegistrationBean; }
	 */

	/**
	 * 请求试图映射
	 *
	 * @param registry
	 */
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("data-generator");
		registry.addViewController("/success").setViewName("success");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/asserts/**").addResourceLocations("classpath:/asserts/");
		super.addResourceHandlers(registry);
	}


}
