package ch.olmero.httpmethodallowed.intercept;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class HttpRequestEvaluationContext extends StandardEvaluationContext {

	private final MethodInvocation methodInvocation;
	private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	public HttpRequestEvaluationContext(MethodInvocation methodInvocation) {
		this.methodInvocation = methodInvocation;
	}

	private boolean argumentsAdded;

	@Override
	public Object lookupVariable(String name) {
		Object variable = super.lookupVariable(name);

		if (variable != null) {
			return variable;
		}

		if (!this.argumentsAdded) {
			addArgumentsAsVariables();
			this.argumentsAdded = true;
		}

		variable = super.lookupVariable(name);

		if (variable != null) {
			return variable;
		}

		return null;
	}

	private void addArgumentsAsVariables() {
		Object[] args = this.methodInvocation.getArguments();

		if (args.length == 0) {
			return;
		}

		Class<?> targetClass = AopProxyUtils.ultimateTargetClass(this.methodInvocation.getThis());

		Method method = AopUtils.getMostSpecificMethod(this.methodInvocation.getMethod(), targetClass);
		Parameter[] parameters = method.getParameters();

		String[] paramNames = this.parameterNameDiscoverer.getParameterNames(method);

		for (int i = 0; i < args.length; i++) {
			String paramName = paramNames[i];

			if (parameters[i].getAnnotation(PathVariable.class) != null) {
				setVariable(paramName, args[i]);
			}
		}
	}
}