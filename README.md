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
