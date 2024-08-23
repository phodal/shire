---
layout: default
title: Secondary Research (TBC)
parent: Shire Scene
nav_order: 1
---


## WebResource

```shire
---
variables:
  "website": { extract(".md") | crawl() | thread("summary.shire") }
  "confluence": { thread("confluence.bash", param1, param2) }  
---

[website]: it will extract all the .md files and crawl them, then thread them with summary.shire, then return the result.
```

## API Resource by Bash

- Confluence API
- Jira API

### Confluence API

https://developer.atlassian.com/cloud/confluence/using-the-rest-api/

```bash
curl --request <method> '/rest/api/content/search?limit=1&cql=id!=0 order by lastmodified desc' \
--header 'Accept: application/json' \
--header 'Authorization: Basic <encoded credentials>'
```

### Jira API

https://developer.atlassian.com/cloud/jira/platform/rest/v3/intro/

```bash
curl --request <method> '<url>?<parameters>' \
--header 'Accept: application/json' \
Authorization: Basic <encoded credentials>'
```