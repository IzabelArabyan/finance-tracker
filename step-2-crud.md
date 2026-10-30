# Step 2: CRUD API, DTOs, layering, first tests

**Estimated time:** 1 week

## Why this step

Accounts, categories and transactions each get a controller, a service and an in-memory store. Clients see DTOs, so the model classes can change later without breaking anyone. Tests start here. Each service gets unit tests and each controller gets a MockMvc test, and that stays the rule for the rest of the plan.

## Read

1. [The Spring @Controller and @RestController Annotations](https://www.baeldung.com/spring-controller-vs-restcontroller) (Baeldung)
2. [Entity To DTO Conversion for a Spring REST API](https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application) (Baeldung)
3. Java records: [dev.java tutorial, Records](https://dev.java/learn/records/) and Baeldung, [Java 14 Record Keyword](https://www.baeldung.com/java-record-keyword)
4. [Mockito annotations: @Mock, @InjectMocks and @Captor](https://www.baeldung.com/mockito-annotations) (Baeldung)
5. [Testing the Web Layer](https://spring.io/guides/gs/testing-web/) (official Spring guide), only the `@WebMvcTest` part
6. Spring Boot reference, [Testing Spring Boot Applications](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html), sections on `@WebMvcTest` and `@MockitoBean`

**Further reading:** REST API design, [Zalando RESTful API Guidelines](https://opensource.zalando.com/restful-api-guidelines/) (chapters on resources, naming, HTTP methods, status codes).

## Topics to understand

- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` ([Baeldung](https://www.baeldung.com/spring-new-requestmapping-shortcuts))
- `@PathVariable` ([Baeldung](https://www.baeldung.com/spring-pathvariable)) and `@RequestParam` ([Baeldung](https://www.baeldung.com/spring-request-param))
- `@RequestBody` ([Baeldung](https://www.baeldung.com/spring-request-response-body)) and `ResponseEntity` ([Baeldung](https://www.baeldung.com/spring-response-entity))
- Layering: controllers call services, services call repositories; a controller never holds business logic
- DTOs: why the API must not expose internal classes directly, and why records fit DTOs (Baeldung)
- Unit test of a service with Mockito: `@Mock`, `@InjectMocks`, `when(...).thenReturn(...)`, `verify` (Baeldung)
- Controller test with `@WebMvcTest`, `MockMvc` and `@MockitoBean` (Spring guide and reference)
- AssertJ assertions (`assertThat`), which Spring Boot test includes

## In the project

- [ ] Add `Category` (id, name, type) and `TransactionServiceTest` (id, accountId, categoryId, amount, type, date, description), enum `TransactionType` (`INCOME`, `EXPENSE`). Money as `BigDecimal`, dates as `LocalDate`.
- [ ] Request and response DTOs as records, separate from the model classes, for example `CreateAccountRequest`, `AccountResponse`.
- [ ] Full CRUD for accounts, categories and transactions under `/api/accounts`, `/api/categories`, `/api/transactions`.
- [ ] Keep the data in a `Map` per entity, inside an `InMemoryXxxRepository` class in the `repository` sub-package. The service calls the repository, so step 4 can replace it with Spring Data without touching the service.
- [ ] Adding an `INCOME` transaction increases the account balance, `EXPENSE` decreases it. Deleting a transaction reverses the change.
- [ ] Tests: Mockito unit tests for every service with mocked repositories, one `@WebMvcTest` per controller covering at least create, get by id and delete.
- [ ] Export a Postman collection to `postman/finance-tracker.json`.
- [ ] Pull request `step-2-crud`.

## Check questions

1. Which status code should `POST /api/accounts` return, and what header or body tells the client where the new resource is?
2. What is the difference between `@RequestParam` and `@PathVariable`? Give an example of each from your API.
3. Why do we use DTOs instead of returning the model class directly? What breaks later if we do not?
4. Why mock the repository in a service test? What would a test without the mock be testing?
5. What does `@WebMvcTest` load, and what does it not load?

## Result after this step

### In the repository

- Models `Category`, `Category`, `TransactionServiceTest`, enum `TransactionType`, money as `BigDecimal`
- Request and response DTOs as records, separate from the model classes
- `AccountController`, `CategoryController`, `TransactionController`
- Services that depend on `InMemoryXxxRepository` classes (a `Map` per entity)
- Mockito unit tests for every service
- One `@WebMvcTest` per controller
- Postman collection at `postman/finance-tracker.json`
- Pull request `step-2-crud` merged after mentor review

### The app can

- Create, list, get by id, update and delete accounts, categories and transactions
- Return the right status codes: **201** on create, **200** on read and update, **204** on delete
- Update an account's balance when a transaction is added or deleted
- Pass `mvn test` with all tests green
