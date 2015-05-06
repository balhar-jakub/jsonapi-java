/**
 * It is just simple library for wrapping any Object into the JSON API envelope.
 *
 * Is it possible to mark using interface that some object can be included. It means that the object contains the uuid.
 *
 * Generate included ideally based on the resource with possibility to also add more of them. Probably by adding
 * included as annotation.
 * Generate linkage based on this information. Type can be inferred, Id will also have annotation. By default try id,
 * uuid in this order if nothing is specified.
 */
package net.balhar.jsonapi;