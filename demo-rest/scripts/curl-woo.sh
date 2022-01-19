#!/bin/sh
# curl -H "Authorization:Basic base64encoded<username:password>" \
#     -X GET https://www.crbclean.com/wp-json/wp/v2/posts

curl https://www.crbclean.com/wp-json/wc/v3/settings \
  -v \
  -u ck_XXXXXX:cs_XXXXXXXXXXXX
