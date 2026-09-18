# JC Portal & POS - Senior Engineering Coding Standards

## 1. Backend Standards (Java 8 + Spring Boot 2.7.3 + Maven)

### 1.1 Java 8 Language & API Constraints
- **Target Java Version**: **Java 1.8** strictly.
- **Forbidden Features (Java 9+)**:
  - Do **NOT** use `var` keyword.
  - Do **NOT** use `List.of()`, `Set.of()`, or `Map.of()` (use `Arrays.asList()`, `Collections.singletonList()`, or `new ArrayList<>()`).
  - Do **NOT** use Records, sealed classes, or pattern matching `instanceof`.
  - Do **NOT** use switch expressions with arrows `->`.
  - Do **NOT** use `Optional.isEmpty()` (use `!optional.isPresent()`).
  - Do **NOT** use text blocks (`"""`).
- **Permitted Idioms**:
  - Standard Java 8 Streams API (`.stream().filter().map().collect(Collectors.toList())`).
  - Lambdas and method references.
  - Java 8 date/time (`java.time.LocalDateTime`, `LocalDate`, `LocalTime`) or `java.util.Date`.

### 1.2 Spring Boot & Java EE Framework Conventions
- **Namespace**: Use **`javax.*`** packages exclusively (`javax.persistence.*`, `javax.validation.*`, `javax.servlet.*`).
  - Never import `jakarta.*` packages as they will break the Spring Boot 2.7 runtime.
- **Layered Architecture**:
  - `Controller`: Handles HTTP requests, validations (`@Valid`), and returns standard `ResponseEntity<Map<String, Object>>` or typed response DTOs.
  - `Service` & `ServiceImpl`: Encapsulates all domain and business calculations.
  - `Repository`: Extends `JpaRepository<Entity, Long>` with Spring Data query methods.
  - `Entity`: Annotated with `@Entity`, `@Table(name = "pos_...")`, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
  - `Dto`: Dedicated `RequestDto` and `ResponseDto` classes with Lombok annotations (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`).
- **Error Handling**: Use consistent try-catch blocks with `GlobalExceptionHandler` or standard API responses (`success: true/false`, `msg: String`, `data: Object`).

---

## 2. Frontend Standards (React 18 + Vite 7 + TailwindCSS 4)

### 2.1 Component Architecture
- **Functional Components**: Use pure React 18 functional components with explicit prop typing or JSDoc.
- **Hook Conventions**:
  - Custom hooks prefixed with `use` (e.g. `usePosStore`, `usePosData`).
  - Memoization (`useMemo`, `useCallback`) applied where calculations or callbacks could trigger heavy re-renders.
- **Folder Structure**:
  - `src/pages/pos/`:
    - `PosApp.jsx`: Main POS container with tab views.
    - `PosLogin.jsx`: Dedicated POS authentication page.
    - `components/`: Modular view components (`TablesView`, `OrderDeskView`, `KotKanbanView`, `BillingView`, `ReservationsView`, `MastersView`).
    - `modals/`: Modal dialogs (`VariantModal`, `PickerModal`, `InvoiceModal`, `MasterModal`, `ReservationModal`).
    - `context/` or `store/`: POS state management.
  - `src/services/posService.js`: API client wrapping Axios for all POS endpoints.

### 2.2 Styling & Theming
- **TailwindCSS 4**:
  - Adhere to the CRM design system and theme variables (`--primary: #017A9C`).
  - Dark mode support using `dark` class toggle on the document root.
  - Clean responsive grid and flexbox layouts.
- **Accessibility & UX**:
  - Visual feedback on user actions (toasts, loading spinners).
  - Clear confirmation dialogs for destructive actions (e.g. voiding kitchen items, cancelling orders).

---

## 3. Git & Workspace Hygiene
- Keep `target/`, `bin/`, and `node_modules/` strictly excluded in `.gitignore`.
- Feature branch workflow (`feature/*`) cut from `development`.
- Commit messages formatted with clear intent:
  - `feat(pos): add POS floor and table management components`
  - `fix(kot): adjust kitchen ticket transition logic`
