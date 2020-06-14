# Load Balancer

This load balancer implementation spins a number of instances returning a standard get response. 

The load balancer itself is single threaded, but it is able to manage multiple concurrent calls up to the number of available instances.

A single instance has a probability to not be available and it will be put  


## Setup

Install [gradle](https://gradle.org/), and at least [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) and do:

- `gradle run`

If everything goes accordingly, it is possible to test it with:

`curl localhost:7777`

And it should return a plain text response like:

- `Ok for 50b37a71-c8a0-4adb-b627-e8ffd591d74b`

It is possible to run tests with:

`gradle test`


### Spin up the load balancer with custom parameretes


- Spin up a new Load Balancer with a Random balancing algorithm:

`gradle run --args="--algo Random"`

- Spin up a new Load Balancer instance with a Round Robin balancing algorith, 5 instances, a healthcheck being run every 5 seconds and a healthcheck failure chance of 99% and a delay of 5 ms.
`gradle run --args="--algo RoundRobin --instances 5 --healthcheck_seconds 5 --healthcheck_failure_chance 0.99 --service_simulate_delay 5"`
