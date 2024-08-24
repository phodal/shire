curl --request <method> '/rest/api/content/search?limit=1&cql=id!=0 order by lastmodified desc' \
--header 'Accept: application/json' \
--header 'Authorization: Basic <encoded credentials>'