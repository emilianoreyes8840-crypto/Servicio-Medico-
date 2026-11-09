# =============================================================================
# Imagen 3 - Capa de Presentacion / Frontend (Vista)
# -----------------------------------------------------------------------------
# OBSERVACION: ninguno de los 5 paquetes de codigo fuente provistos incluia
# un frontend propio (SPA en React/Angular/Vue, etc.). Lo unico "visual" que
# existe son dos vistas Thymeleaf server-side dentro de billing-service
# (/facturas) y report-analyst-service (/reportes).
#
# Al faltar ese codigo critico, se genera aqui un stub funcional: un
# dashboard estatico (HTML/CSS/JS, sin frameworks, mismo estilo visual que
# las 2 vistas Thymeleaf existentes) servido por Nginx, que además actúa
# como reverse proxy /api/* hacia cada microservicio para evitar problemas
# de CORS y dar UN SOLO punto de entrada local: http://localhost
# =============================================================================
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY html/ /usr/share/nginx/html/
EXPOSE 80
