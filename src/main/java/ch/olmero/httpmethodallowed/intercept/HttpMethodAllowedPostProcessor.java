package ch.olmero.httpmethodallowed.intercept;

import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;

@Component
public class HttpMethodAllowedPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor implements InitializingBean {

	private final RequestMappingHandlerMapping requestMappingHandlerMapping;
	private final HttpMethodAllowedExpressionOperations httpMethodAllowedExpressionOperations;
	private final Class<? extends Annotation> allowMethodAnnotationType = HttpMethodAllowed.class;

	public HttpMethodAllowedPostProcessor(RequestMappingHandlerMapping requestMappingHandlerMapping, HttpMethodAllowedExpressionOperations httpMethodAllowedExpressionOperations) {
		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
		this.httpMethodAllowedExpressionOperations = httpMethodAllowedExpressionOperations;
	}

	@Override
	public void afterPropertiesSet() {
		Pointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(this.allowMethodAnnotationType);
		this.advisor = new DefaultPointcutAdvisor(pointcut, new HttpMethodAllowedInterceptor(requestMappingHandlerMapping, httpMethodAllowedExpressionOperations));
	}
}
