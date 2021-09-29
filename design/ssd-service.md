# Semi-Structured Data Service

## 1. Program Structure

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

## 2. Framework

- interface
- log

## 3. Engine

- config
- log
- persistence
- script

## 4. File

```plantuml
@startmindmap
* /
** configs/
***_ default.json
***_ last.json
** logs/
*** http/
****_ 2021-07-28T22-30-02.log
****_ 2021-07-28T23-00-01.log
*** .../
****_ 2021-07-28T22-30-02.log
****_ 2021-07-28T23-00-01.log
***_ 2021-07-28T22-30-02.log
***_ 2021-07-28T23-00-01.log
** persistences/
***_ default.json
***_ 2021-07-28T22-00-00.proto
***_ 2021-07-28T23-00-06.proto
** scripts/
***_ default.lua
***_ last.lua
@endmindmap
```
