package bootiful.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

@Controller
@ResponseBody
class CustomerHttpController {

    private final CustomerRepository repository;
    private final ObservationRegistry registry;

    CustomerHttpController(CustomerRepository repository, ObservationRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }


    @GetMapping("/customers/{name}")
    Collection<Customer> customersByName(@PathVariable String name) {
        Assert.state(Character.isUpperCase(name.charAt(0)),
                "the name must start with a capital letter!");
        return Observation
                .createNotStarted("by-name", this.registry)
                .observe(() -> this.repository.findByName(name));
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.repository.findAll();
    }
}

@ControllerAdvice
class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    ProblemDetail handle(IllegalStateException ise, HttpServletRequest request) {
        request.getHeaderNames().asIterator().forEachRemaining(System.out::println);
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        pd.setDetail(ise.getMessage());
        return pd;
    }
}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
    Collection<Customer> findByName(String name);

}

// look ma, no Lombok!!
record Customer(@Id Integer id, String name) {
}
