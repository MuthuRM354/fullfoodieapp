# This repository is a multi-service monorepo (11 independent Spring Boot
# services under src/backend/*) plus a separate React frontend. There is no
# single "whole app" image to build from the repo root.
#
# Each backend service has its own working Dockerfile, e.g.:
#   docker build -t foodieapp-user-service     src/backend/user-service
#   docker build -t foodieapp-api-gateway      src/backend/api-gateway
#   docker build -t foodieapp-order-service    src/backend/order-service
#   ...(one per folder under src/backend)
#
# The frontend is built separately (see frontend/README.md) and is deployed
# as a static site (Vercel/Netlify), not as a container.
#
# The actual Render.com deployment (see render.yaml) does not use Docker at
# all — it builds each service directly with its own Maven wrapper
# (`./mvnw clean package`) and runs the resulting jar. These per-service
# Dockerfiles are provided only for local/manual container-based workflows.
FROM eclipse-temurin:17-jre-jammy
CMD ["sh", "-c", "echo 'Build an individual service instead, e.g.: docker build -t foodieapp-user-service src/backend/user-service' && exit 1"]
