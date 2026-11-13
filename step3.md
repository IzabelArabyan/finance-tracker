# Step 3: Validation and error handling

## Read

1. [Validation in Spring Boot](https://www.baeldung.com/spring-boot-bean-validation) (Baeldung)
2. [Error Handling for REST with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring) (Baeldung), the `@RestControllerAdvice` and `ProblemDetail` parts
3. [RFC 9457, Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457.html) (skim the example in section 3)

## Topics to understand

- `@Valid` on request bodies, and `@Validated` on the controller for path and query parameters (Baeldung)
- Constraints: `@NotNull`, `@NotBlank`, `@Positive`, `@Size`, `@PastOrPresent`, `@DecimalMin` (Baeldung)
- `@RestControllerAdvice` and `@ExceptionHandler` (Baeldung)
- One error format for the whole API using Spring's `ProblemDetail` (Baeldung)
- Which exception Spring throws when `@Valid` fails (`MethodArgumentNotValidException`) and how to turn it into a field list
- Testing error responses with MockMvc: `status().isBadRequest()`, `jsonPath(...)`

## In the project

- Validate input: name not blank, amount positive, date not in the future, category required for an expense, account and category ids not null.
- Custom exceptions in `common`: `NotFoundException` (404, used for every entity), `InsufficientFundsException` (409), `ValidationException` for rules that annotations cannot express (400).
- One `GlobalExceptionHandler` with `@RestControllerAdvice` that maps each exception to a `ProblemDetail`. Validation errors add an `errors` list with field name and message.
- Unknown exceptions return 500 with a generic message. The full stack trace goes to the log at `error` level, where only we can read it.
- Tests: MockMvc tests for 400 with the field list, 404 for a missing account, 409 for an expense larger than the balance.
- Pull request `step-3-validation`.

## Check questions

- What status code does Spring return when `@Valid` fails by default, and how do you change the response body?
- Why is one global handler better than try-catch in every controller?
- Why must the 500 response not include the exception message?
- Where should the rule "an expense needs a category" live: in an annotation, in the service, or in both? Why?

## Result after this step

In the repository
- [ ] Validation annotations on all request DTOs
- [ ] `NotFoundException`, `InsufficientFundsException`, `ValidationException` in `common`
- [ ] `GlobalExceptionHandler` with `@RestControllerAdvice` returning `ProblemDetail`
- [ ] MockMvc tests for 400, 404 and 409
- [ ] Pull request `step-3-validation` merged after mentor review

The app can
- [ ] Reject invalid input with 400 and a list of the fields that failed
- [ ] Return 404 for an account, category or transaction that does not exist
- [ ] Return 409 when an expense is larger than the account balance
- [ ] Return 500 without leaking internal details, and log the stack trace
- [ ] Return every error in the same JSON format
