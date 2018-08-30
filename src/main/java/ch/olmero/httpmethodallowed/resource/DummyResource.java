package ch.olmero.httpmethodallowed.resource;

import ch.olmero.httpmethodallowed.intercept.HttpMethodAllowed;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyResource {

	@GetMapping(value = "/task/{name}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String putWithoutMethodAllowed(@PathVariable String name) {
		return name;
	}

	@HttpMethodAllowed("!isCompleted(#name)")
	@PutMapping(value = "/task/{name}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String putWithMethodAllowed(@PathVariable String name) {
		return name;
	}

}
