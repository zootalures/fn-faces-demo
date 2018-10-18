# Functions Face recognition demo

This is a demo of Oracle Functions integrated with Triggers in the Oracle Cloud Object Store 
##  Talking points


This is a demo that demonstrates a completely serverless use case integrating a browser-based app (captures images, and shows results) OCI Object storage (stores incoming images and results), OCI Events (triggers function calls when new objects arrive) and OCI Functions (transforms images using face recognition and uploads results back to object storage)

The function uses the Functions java runtime and also uses a relatively complex build that takes advantage of the functions container-based model (including using native  and system libraries and local data) - internally the Java calls to the open source openCV image processing system to detect faces before using standard java operations to paint the results. 

Events are exchanged using the open cloud-events protocol, this makes handling events easy and standards-based as well as enabling inter-cloud communication for customers applications. 


## Setting up the demo 

rough notes, may not be complete 

Example walk through - this assumes you already have an account with access to functions and events ,  

I suggest setting up an OCI account with only read/write access to the appropriate buckets (the account with private key /Users/OCliffe/.oci/oci_faces_key.pem )

Create the function: 
```
fn create app faces-demo --annotation 'oracle.com/oci/subnetIds=["ocid1.subnet.oc1.phx.aaaaaaaabvua55yccnh4lxoqn7xkyzgk6wlqwusfnwm4tdb3nmkwknlzdhhq","ocid1.subnet.oc1.phx.aaaaaaaab3njfvicu7jbscr5hjkjudlayxt4crkd4c7cesj77tzagwv527ja","ocid1.subnet.oc1.phx.aaaaaaaap36kfznrzylxxqqd6f2mlh23rbok5envi7sofypabgwdyskqz5pa"]'fn config app faces-demo OCI_PRIVATE_KEY $(cat /Users/OCliffe/.oci/oci_faces_key.pem |base64)  
fn config app faces-demo OCI_KEY_FINGERPRINT a5:28:34:aa:02:a1:c1:2b:5a:d8:f5:c9:cc:9f:ca:35
fn config app faces-demo OCI_TENANCY ocid1.tenancy.oc1..aaaaaaaaltbr5bobenjcbaa3qsuvds6lowqokqzdjllfbwxk5ypjj2e7d23a
fn config app faces-demo OCI_USER ocid1.user.oc1..aaaaaaaakauoquug7zbv6llqz5ga3ewmzsxmy6t5r3xc7txfukub7twy5asq
fn config app faces-demo OCI_REGION us-phoenix-1
fn config app faces-demo OBJECT_NAMESPACE "ocimiddleware"
fn config app faces-demo OUTPUT_BUCKET "facedetection-results"
```

Deploy the function 
```
fn deploy -v   --app faces-demo 
```

Set up the events rules: 
```
oci --profile ocimiddleware cloud-events rule create --display-name detect-faces-in-uplaods --is-enabled true --condition '{"eventType":"oci.objectstorage.object.create"}' --compartment-id ocid1.compartment.oc1..aaaaaaaa7cay2qardzscbfz3xvwad775zdwa2tdio6tvydnocwffkkmvvveq --actions file://actions.json --endpoint https://cloudevents-stage.us-phoenix-1.oraclecloud.com/20181201
```

open the UI: 
```
 open  dumbui/index.html
```
Update the browser url to add `?sombrero=true` to the end 

## Developing/deploying  the demo 

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
