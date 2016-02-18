# Wingz maze test

## Description

Allow to resolve shorter path from a point A to B.
The data are provided from https://wingz-hiring-test.herokuapp.com.
The graph shorter path resolver is a simple implementation of the Dijkstra algorithm.

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

## Test report

19 green tests
Tested with a coverage of 89.9%

## JavaDoc

The Javadoc is not present.

## UI

http://localhost:8080/

![Sreenshot](https://raw.githubusercontent.com/fdumay/wingz-maze/master/src/main/resources/static/images/sreenshot.png)

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
