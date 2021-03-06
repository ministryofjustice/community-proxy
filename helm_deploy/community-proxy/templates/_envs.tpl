{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for web and worker containers
*/}}
{{- define "deployment.envs" }}
env:
  - name: SERVER_PORT
    value: {{ .Values.image.port | quote }}
  - name: SPRING_PROFILES_ACTIVE
    value: "logstash"
  - name: DELIUS_ENDPOINT_URL
    value: {{ .Values.env.DELIUS_ENDPOINT_URL | quote }}
  - name: DELIUS_API_USERNAME
    value: {{ .Values.env.DELIUS_API_USERNAME | quote }}
  - name: JWT_PUBLIC_KEY
    value: {{ .Values.env.JWT_PUBLIC_KEY | quote }}
  - name: APPLICATION_INSIGHTS_IKEY
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: APPLICATION_INSIGHTS_IKEY
{{- end -}}
