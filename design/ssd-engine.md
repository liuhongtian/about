# Semi-Structured Data Engine

## 1. Overview

The core of all semi-structured data services.

- config
- log
- persistence
- script

## 2. Program Structure

```plantuml
@startuml
ditaa
+-------------------------------------+
|        interface / log / ...        | framework
+-------------------------------------+
| config / log / persistence / script | engine
+-------------------------------------+
@enduml
```

## File

```
/configs/
/configs/default.yaml
/configs/last.yaml
/logs/
/logs/2021-07-28T22-30-02.log
/logs/2021-07-28T23-00-01.log
/persistences/
/persistences/default.yaml
/persistences/2021-07-28T22-00-00.proto
/persistences/2021-07-28T23-00-06.proto
/scripts/
/scripts/default.lua
/scripts/last.lua
```