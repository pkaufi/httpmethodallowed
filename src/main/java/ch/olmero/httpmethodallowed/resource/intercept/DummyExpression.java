package ch.olmero.httpmethodallowed.resource.intercept;

import ch.olmero.httpmethodallowed.intercept.HttpMethodAllowedExpressionOperations;
import org.springframework.stereotype.Component;

@Component
class DummyExpression implements HttpMethodAllowedExpressionOperations {
	public boolean isCompleted(String name) {
		return "completed".equals(name);
	}
}