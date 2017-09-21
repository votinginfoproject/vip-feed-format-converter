# vip-feed-format-converter

Converts VIP feeds from one format to another.

Currently only supports 5.1 XML -> 5.1 CSV.

## Usage

    $ lein uberjar
    $ mkdir csv-out-dir
    $ java -jar target/vip-feed-format-converter.jar input.xml csv-out-dir
    
## Conversion Process

### XML -> CSV

XML processing uses `clojure.data.xml`'s low-level event API. This is for
efficiency and speed reasons (given the size of a typical VIP XML feed). For
each tag, a "start" event is generated containing attributes and their values,
the tag name, etc. Then any text value children will generate a "chars" event
which will be a map with a `:str` key and a `:prior` key (containing the
parent element's start event), descendant elements will generate their
events, and finally when the closing tag is encountered, an "end" event is
generated.

The way we process those events and turn them into CSV data is via event
handler functions in nested maps representing the tag-paths for each event.
These handler functions take the current context map and the event as their
arguments (in that order) and return the new context map to pass to later
handlers.

This is easiest to explain by example:

Let's say you have the following XML:

```xml
<?xml version="1.0"?>

<VipObject>
  <Election id="election001">
    <Name>
      <Text language="en">Best Election</Text>
    </Name>
  </Election>
</VipObject>
```

You would pass `vip-feed-format-converter.xml/parse-file` a context map with a
`:handlers` key set to something like:

```clojure
{:VipObject {:Election {:start (fn [ctx event]
                                 (assoc-in ctx [:tmp :election :id]
                                           (get-in event [:attrs :id])))
                        :Name 
                        {:Text 
                         {:chars (fn [ctx event]
                                   (if (= "en" 
                                          (get-in event
                                                  [:prior :attrs :language])
                                      (assoc-in ctx [:tmp :election :name]
                                                (:str event)))
                                      ctx))}}
                        :end (fn [ctx _]
                               (-> ctx
                                   (update-in [:csv-data :election :data]
                                              conj (get-in ctx 
                                                           [:tmp :election]))
                                   (update :tmp dissoc :election)))}}}
```

This would build up the data structure you want in the context map under the
`[:tmp :election]` key path until the election tag's `:end` event fires, at
which point it will be conj'd onto the `[:csv-data :election :data]` collection
and the `[:tmp :election]` key path reset for any other `Election` elements
that might be in the XML.

The `vip-feed-format-converter.csv/write-files` function can then take over and
output that context as CSV.

You can find examples of these kinds of handlers and their related data and
code in the `xml2csv` namespaces.

## License

Copyright © 2017, The Pew Charitable Trusts. All rights reserved.

Distributed under the BSD 3-Clause License. See the `LICENSE` file for details.