#Twitter Count Application

Reads from a stream of timestamped tweets and allows querying the number of tweets in a given time range containing keywords from a pre-specified set of keywords.

config: ```src/main/resources/application.conf``` has keywords and twitter keys

run: ```sbt "run-main lab.tca.Server"```

test: ```sbt clean test```

#Design Considerations:
* **Writer flow**: Twitter -> Spark Stream -> windowed counts -> Storage (optimized for reads)
* **Reader flow**: Finch endpoint <- get by key on Map, then use from and to on TreeMap <- Map (O(1)) of TreeMap (O(log(n)) red-black tree)
* List of keywords is pre-configured in application.conf
* **Future work**: stream test, back-pressure, orderly shutdown, logging, micro benchmarks, code coverage
