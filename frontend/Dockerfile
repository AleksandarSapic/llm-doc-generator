# ─── Stage 1: Build ───────────────────────────────────────────────────────────
# node:22-alpine: Node 22 LTS on Alpine Linux (~70 MB compressed).
# Alpine saves ~280 MB over the debian-based variant with no functional difference
# for a pure build stage.
FROM node:22-alpine AS builder

WORKDIR /app

# Copy only the dependency manifests first.
# Docker caches this layer independently of source changes, so npm ci is only
# re-run when package.json or package-lock.json actually change — not on every
# source edit. This is the single biggest build-time caching win.
COPY package.json package-lock.json ./

# npm ci: strict, reproducible install from the lock file (faster than npm install).
# --ignore-scripts: prevents post-install scripts from running in an untrusted
# build environment (a common supply-chain attack vector).
RUN npm ci --ignore-scripts

# Copy source after dependencies so a source-only change hits the layer above
# the cached node_modules, not the npm install layer.
COPY . .

# Production build. angular.json sets defaultConfiguration = "production", so
# this runs with full optimisations: tree-shaking, minification, and content-
# hashing of all output filenames.
#
# Output path: dist/llm-doc-generator-frontend/browser/
# (Angular's @angular/build:application builder places compiled assets in a
# browser/ subdirectory — different from the legacy @angular-devkit/build-angular)
RUN npm run build


# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
# nginx:alpine: the official nginx image on Alpine (~5 MB compressed).
# Only the compiled static assets are copied here; the Node.js toolchain,
# source files, and node_modules are left behind in the build stage.
FROM nginx:alpine AS runtime

# Replace the default nginx virtual host config with our custom one.
# This config handles: SPA fallback routing, /api proxy to backend,
# content-hash asset caching, SSE buffering, and security headers.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy only the browser-ready assets produced by the build stage.
# The browser/ subdirectory is the document root served by nginx.
COPY --from=builder /app/dist/llm-doc-generator-frontend/browser/ /usr/share/nginx/html/

# nginx:alpine runs worker processes as the unprivileged built-in `nginx` user.
# Only the master process runs as root to bind port 80, which is standard nginx
# practice. No explicit USER instruction is needed.
EXPOSE 80

# wget is available in alpine without an extra install step and is lighter than
# curl for a simple liveness probe.
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1
