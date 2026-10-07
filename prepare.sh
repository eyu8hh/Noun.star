#!/usr/bin/env bash
# يحمّل Lucide وخط Cairo مرة واحدة ويضعهما داخل التطبيق ليعمل دون إنترنت
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"; W="$ROOT/app/src/main/assets/www"; mkdir -p "$W/fonts"
T="$(mktemp -d)"; cd "$T"
npm pack lucide@0.383.0 --silent; mkdir l; tar xzf lucide-*.tgz -C l; cp l/package/dist/umd/lucide.min.js "$W/"
npm pack @fontsource-variable/cairo --silent; mkdir f; tar xzf fontsource-variable-cairo-*.tgz -C f
cp f/package/files/cairo-arabic-wght-normal.woff2 f/package/files/cairo-latin-wght-normal.woff2 "$W/fonts/"
cat > "$W/fonts/fonts.css" <<CSS
@font-face{font-family:Cairo;font-weight:200 1000;font-display:swap;src:url(cairo-arabic-wght-normal.woff2) format("woff2");unicode-range:U+0600-06FF,U+0750-077F,U+08A0-08FF,U+FB50-FDFF,U+FE70-FEFF,U+200C-200F}
@font-face{font-family:Cairo;font-weight:200 1000;font-display:swap;src:url(cairo-latin-wght-normal.woff2) format("woff2");unicode-range:U+0000-00FF,U+0131,U+0152-0153,U+02BB-02BC,U+02C6,U+02DA,U+02DC,U+2000-206F,U+20AC,U+2122,U+2212,U+FEFF}
CSS
echo "OK: assets ready"
