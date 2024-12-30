# -------------------------------------------------------------------------
# Stage 1: Build the Angular (Node)
# -------------------------------------------------------------------------
FROM node:18 AS build

WORKDIR /app
COPY package*.json ./
RUN npm install

# Copy your Angular source code
COPY . .

# Build Angular in production mode
RUN npm run build --prod
# If you’re using Angular Universal or prerender, you'll get "browser/" etc.

# -------------------------------------------------------------------------
# Stage 2: Serve via Nginx
# -------------------------------------------------------------------------
FROM nginx:alpine

# Copy your updated nginx.conf to override default
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy ALL dist output (including browser/, prerendered-routes.json, etc.)
COPY --from=build /app/dist/front_app_builder /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
