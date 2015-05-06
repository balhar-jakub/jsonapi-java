/**
 * This package contains core interfaces and annotations for creating library supporting JSON Api in unobtrusive form
 * . You create document passing object to be serialized into JSON API. This object is envelope providing fluent api
 * to add further json api specific implementation. By calling transform on the document you receive fully featured
 * JSON Api document with all relevnat data in valid structure.
 *
 * Only thing which object to be wrapped must fulfill is to implement interface Identifiable and therefore provide
 * method to retrieve its identity.
 *
 * Furthermore you can provide some annotations on the wrapped object, which the wrapper uses to modify generated
 * document.
 *
 * Ignored - Ignores given property in the output.
 * Included - Includes either Object or Collection as included and also to the linkage part of the links with correct
 *  type.
 * Type - Override default inferred type with specified type.
 */
package net.balhar.jsonapi;