FROM node:22-alpine3.22 as builder

WORKDIR /app

COPY package.json package-lock.json* ./

RUN npm install

COPY .env.production .env

COPY . .

RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Copy built assets from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 (default for nginx)
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]