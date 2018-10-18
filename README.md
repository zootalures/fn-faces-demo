# Functions Face recognition demo

This is a demo of Oracle Functions integrated with Triggers in the Oracle Cloud Object Store 

## Running the demo 

This is just the demo UI. It can operate in different modes. To make the whole
thing run, you need to provide the following:

1. A Fn Function that reads from a source Object Store bucket and writes to an output Object Store bucket.
2. Fill in the `outputImageURL()` function for your mode.

The demo runs in two modes, boring (grayscale) mode and exciting (sombrero)
mode. To run in sombrero mode, append *?sombrero* to the end of the URL.

- [Boring Gray demo](https://objectstorage.us-phoenix-1.oraclecloud.com/n/ocimiddleware/b/ui/o/index.html)
- [Exciting Sombrero demo](https://objectstorage.us-phoenix-1.oraclecloud.com/n/ocimiddleware/b/ui/o/index.html?sombrero)

We also have a [short url](http://bit.ly/openworld-cloud-events-function)

The demo is deployed as a single HTML page hosted in a public Casper bucket in
the "ocimiddleware" tenancy, region PHX, compartment "EventsDevTeam", bucket =
"ui".

To push the demo page:

    oci os object put -ns ocimiddleware -bn ui --name index.html --file dumbui/index.html --content-type "text/html" --force --profile $PROFILE

## Stolen Resources

Spinner was #2 from this site, http://tobiasahlin.com/spinkit/
