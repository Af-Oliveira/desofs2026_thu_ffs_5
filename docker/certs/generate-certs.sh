#!/bin/bash
# Usage: ./docker/certs/generate-certs.sh
# Generates self-signed CA, server cert, and machine client cert for mTLS dev

set -e

CERTS_DIR="$(cd "$(dirname "$0")" && pwd)"
mkdir -p "$CERTS_DIR"

echo "=== Generating CA key and certificate ==="
openssl req -x509 -newkey rsa:4096 -days 365 -nodes \
  -keyout "$CERTS_DIR/ca-key.pem" \
  -out "$CERTS_DIR/ca-cert.pem" \
  -subj "/C=PT/ST=Porto/L=Porto/O=VendNet/OU=Dev/CN=VendNet-CA"

echo "=== Generating Server key and certificate ==="
openssl req -newkey rsa:4096 -nodes \
  -keyout "$CERTS_DIR/server-key.pem" \
  -out "$CERTS_DIR/server-req.pem" \
  -subj "/C=PT/ST=Porto/L=Porto/O=VendNet/OU=Dev/CN=localhost"

openssl x509 -req -days 365 \
  -in "$CERTS_DIR/server-req.pem" \
  -CA "$CERTS_DIR/ca-cert.pem" \
  -CAkey "$CERTS_DIR/ca-key.pem" \
  -CAcreateserial \
  -out "$CERTS_DIR/server-cert.pem" \
  -extfile <(printf "subjectAltName=DNS:localhost,IP:127.0.0.1")

echo "=== Generating Machine client key and certificate ==="
openssl req -newkey rsa:4096 -nodes \
  -keyout "$CERTS_DIR/machine-key.pem" \
  -out "$CERTS_DIR/machine-req.pem" \
  -subj "/C=PT/ST=Porto/L=Porto/O=VendNet/OU=Dev/CN=machine-001"

openssl x509 -req -days 365 \
  -in "$CERTS_DIR/machine-req.pem" \
  -CA "$CERTS_DIR/ca-cert.pem" \
  -CAkey "$CERTS_DIR/ca-key.pem" \
  -CAcreateserial \
  -out "$CERTS_DIR/machine-cert.pem"

echo "=== Creating PKCS12 keystores ==="
openssl pkcs12 -export \
  -in "$CERTS_DIR/server-cert.pem" \
  -inkey "$CERTS_DIR/server-key.pem" \
  -out "$CERTS_DIR/server-keystore.p12" \
  -name vendnet-server \
  -passout pass:changeit

openssl pkcs12 -export \
  -in "$CERTS_DIR/machine-cert.pem" \
  -inkey "$CERTS_DIR/machine-key.pem" \
  -out "$CERTS_DIR/machine-keystore.p12" \
  -name machine-001 \
  -passout pass:changeit

echo "=== Creating truststore ==="
keytool -import -trustcacerts -noprompt \
  -alias vendnet-ca \
  -file "$CERTS_DIR/ca-cert.pem" \
  -keystore "$CERTS_DIR/truststore.jks" \
  -storepass changeit

rm -f "$CERTS_DIR/server-req.pem" "$CERTS_DIR/machine-req.pem"

echo ""
echo "Certificates generated in: $CERTS_DIR"
echo ""
echo "Files created:"
echo "  ca-cert.pem          - CA certificate"
echo "  server-cert.pem      - Server certificate"
echo "  server-keystore.p12  - Server PKCS12 keystore (password: changeit)"
echo "  machine-cert.pem     - Machine client certificate"
echo "  machine-keystore.p12 - Machine PKCS12 keystore (password: changeit)"
echo "  truststore.jks       - Truststore with CA cert (password: changeit)"
echo ""
echo "To enable mTLS, add to application.properties:"
echo "  server.ssl.key-store=classpath:../../../docker/certs/server-keystore.p12"
echo "  server.ssl.key-store-password=changeit"
echo "  server.ssl.trust-store=classpath:../../../docker/certs/truststore.jks"
echo "  server.ssl.trust-store-password=changeit"
echo "  server.ssl.client-auth=need"
