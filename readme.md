#Twitter Count Application

Reads from a stream of timestamped tweets and allows querying the number
 of tweets in a given time range containing keywords from a pre-specified
  set of keywords.

config: ```src/main/resources/application.conf``` has keywords and twitter keys

run: ```sbt "run-main lab.tca.Server"```

test: ```sbt clean test```

#Design Considerations:
* Queries have end of `hh:mm`. At `hh:mm:ss` the application returns correct
 results for `hh:mm`. It is anyway impossible to return correct count 
 for `hh:mm+1` at time `hh:mm:ss`.
* **Writer flow**: Twitter -> Spark Stream -> counts -> Storage(optimized for reads)
* **Reader flow**: Finch endpoint <- get by key on Map, then use `from` 
and `to` on TreeMap <- Map (O(1)) of TreeMap (O(log(n)) red-black tree)
* List of keywords is pre-configured in application.conf
* **Future work**: orderly shutdown, logging, micro benchmarks, more test coverage
