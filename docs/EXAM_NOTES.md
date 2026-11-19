# AI Smart City Issue Management - Viva Notes

## Simple component explanation
- React/Vite provides the responsive citizen and administrator interface.
- Spring Boot exposes REST APIs and performs validation and authorization.
- Spring Security with JWT authenticates users and grants role-specific access.
- JPA/Hibernate maps Java entities to MySQL tables.
- Gemini REST integration analyzes reports; keyword fallback keeps the system usable offline.
- Local/S3 storage stores evidence images.
- Docker Compose runs MySQL, backend, and frontend as one application.
- Prometheus and Grafana provide operational monitoring.

## Data flow
1. Citizen registers or logs in and receives a JWT.
2. Citizen submits title, description, location, optional coordinates, and image.
3. Backend validates and stores the issue, then calls Gemini.
4. Gemini or fallback analysis assigns category, priority, summary, and suggested department.
5. Admin verifies and assigns the issue.
6. Status changes are written as issue history records.
7. Admin resolves/rejects the issue and citizens see the timeline.

## 25 likely viva questions
1. **What is this system?** A role-based civic issue reporting and resolution platform.
2. **Why Spring Boot?** Rapid REST API development with embedded server and dependency injection.
3. **Why React?** Component-based, responsive single-page UI.
4. **Why JWT?** Stateless authentication suitable for APIs.
5. **How are passwords secured?** BCrypt hashes, never plain text.
6. **What are CITIZEN and ADMIN roles?** Citizen submits/tracks issues; admin manages workflows.
7. **How does Gemini return structured data?** A strict JSON prompt is used and the response is parsed safely.
8. **What happens if Gemini fails?** The local keyword fallback runs automatically.
9. **How is priority calculated?** Dangerous keywords raise priority; otherwise default/low/urgent rules apply.
10. **What is JPA?** Java Persistence API for ORM-based database access.
11. **Why MySQL 8?** Reliable relational storage with transactions and foreign keys.
12. **What is an issue timeline?** A list of status transitions stored in issue_updates.
13. **Why store old_status and new_status?** It provides a complete audit trail.
14. **How is image validation done?** MIME type and 5 MB size limits are checked.
15. **How does S3 storage work?** AWS SDK uploads objects and stores public-ready URLs.
16. **What is CORS?** Controls browser origins permitted to call the API.
17. **What is an actuator?** Production health and metrics endpoints.
18. **What is Prometheus?** A metrics scraper that stores time-series operational data.
19. **What is Grafana?** A dashboard tool for visualizing Prometheus metrics.
20. **Why Docker?** Consistent packaging and isolation across environments.
21. **What does Docker Compose do?** Builds and connects all application services.
22. **Why RDS in AWS?** Managed MySQL backups, patching, availability, and scaling.
23. **Why EC2?** A host for containers and application services.
24. **Why IAM roles?** Secure AWS service access without embedding credentials.
25. **How is deployment automated?** GitHub Actions builds images and SSH deploys the Compose stack.
