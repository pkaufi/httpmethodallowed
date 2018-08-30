package ch.olmero.httpmethodallowed.intercept;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpMethodAllowedInterceptor implements MethodInterceptor {

	private final ExpressionParser parser = new SpelExpressionParser();
	private final RequestMappingHandlerMapping requestMappingHandlerMapping;
	private final HttpMethodAllowedExpressionOperations httpMethodAllowedExpressionOperations;
	private final Class<HttpMethodAllowed> allowMethodAnnotationType = HttpMethodAllowed.class;

	public HttpMethodAllowedInterceptor(RequestMappingHandlerMapping requestMappingHandlerMapping, HttpMethodAllowedExpressionOperations httpMethodAllowedExpressionOperations) {
		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
		this.httpMethodAllowedExpressionOperations = httpMethodAllowedExpressionOperations;
	}

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		HttpMethodAllowed httpMethodAllowed = methodInvocation.getMethod().getAnnotation(allowMethodAnnotationType);
		String expression = httpMethodAllowed.value();

		StandardEvaluationContext rootContext = new HttpRequestEvaluationContext(methodInvocation);
		rootContext.setRootObject(this.httpMethodAllowedExpressionOperations);

		if (!this.parser.parseExpression(expression).getValue(rootContext, Boolean.class)) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			throw new MethodNotAllowedException(request.getMethod(), getAllowedMethods(request));
		}

		return methodInvocation.proceed();
	}

	private Set<HttpMethod> getAllowedMethods(HttpServletRequest request) {
		return requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
			.filter(info -> info.getPatternsCondition().getMatchingCondition(request) != null)
			.flatMap(match -> match.getMethodsCondition().getMethods().stream())
			.map(RequestMethod::name)
			.filter(method -> !method.equals(request.getMethod()))
			.map(HttpMethod::resolve)
			.collect(Collectors.toSet());
	}
}