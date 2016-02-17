# Wingz maze test

## Description

Allow to resolve shorter path from a point A to B.
The data are provided from https://wingz-hiring-test.herokuapp.com.
The graph shorter path resolver is a simple implementation of the Dijkstra algorithm.

## Report

Tested with a coverage of 89.9%
Fault of time the Javadoc is not present.

## Requirements

- java8
- gradle

## Build the project

```
gradle build
```

## Run the project

```
gradle bootRun
```

## Test

```
gradle bootRun
```

Tested with a coverage of 89.9%

## Doc

Fault of time the Javadoc is not present.

## API

```
GET /map
```

List the available maps loaded from https://wingz-hiring-test.herokuapp.com

````
GET /map/{mapId}
````

- @mapId is mandatory

Return the map loaded from https://wingz-hiring-test.herokuapp.com

```
GET /solve?start={startId}&end={endId}&mapId={mapId}
````

- @startId is mandatory
- @endId is mandatory
- @mapId is not mandatory, the default value is map1.json
