## JsonApi java

The JsonApi aims at providing simple means of transforming any object into its hypermedia representation. The core
library is simple envelope on top any object which provides developers with convenient methods on top of JSON Api
document.

It allows developer to create JSON Api document from any object and add all needed relations into it. And by calling
transform receive HashMap object, which is possible to simply serialize into any of formats like JSON or XML to
receive valid output according to the JSON Api specification (http://jsonapi.org)

The API is fluent. Therefore every method called on document except for transform returns document to continue
working on.

## Usage

Simplest usage is to create simple document from existing object. You can do it this way:
Document simpleDocument = new SimpleDocument(objectForEnvelope);
Object toSerialize = simpleDocument.transform();

You can add links to the top level document:
Document simpleDocument = new SimpleDocument(objectForEnvelope).
    link("self", "http://api.test.com/person/1");

It is also possible to add links to the data collection itself instead of on top of the collection. This link is :
Document simpleDocument = new SimpleDocument(objectForEnvelope).
    resourceLink("person", "self", "http://api.test.com/person/1");

It is also possible to include objects in the top level document. Included objects is collection of different types
of objects
Document simpleDocument = new SimpleDocument(objectForEnvelope).
    include(objectToInclude);

Last possible thing is possibility to add meta data in format key/value
Document simpleDocument = new SimpleDocument(objectForEnvelope).
    meta("createdBy","Jakub Balhar");