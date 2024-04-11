package edu.illinois.library.cantaloupe.image;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import edu.illinois.library.cantaloupe.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Serializes an {@link Info}.
 *
 * @since 5.0
 */
final class InfoSerializer extends JsonSerializer<Info> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(InfoSerializer.class);

    static final String APPLICATION_VERSION_KEY   = "applicationVersion";
    static final String IDENTIFIER_KEY            = "identifier";
    static final String IMAGES_KEY                = "images";
    static final String MEDIA_TYPE_KEY            = "mediaType";
    static final String METADATA_KEY              = "metadata";
    static final String NUM_RESOLUTIONS_KEY       = "numResolutions";
    static final String SERIALIZATION_VERSION_KEY = "serializationVersion";

    @Override
    public void serialize(Info info,
                          JsonGenerator generator,
                          SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        // application version
        generator.writeStringField(APPLICATION_VERSION_KEY, Application.getVersion());
        // serialization version
        generator.writeNumberField(SERIALIZATION_VERSION_KEY, Info.Serialization.CURRENT.getVersion());
        // identifier
        if (info.getIdentifier() != null) {
            generator.writeStringField(IDENTIFIER_KEY, info.getIdentifier().toString());
        }
        // mediaType
        if (info.getMediaType() != null) {
            generator.writeStringField(MEDIA_TYPE_KEY, info.getMediaType().toString());
        }
        // numResolutions
        generator.writeNumberField(NUM_RESOLUTIONS_KEY, info.getNumResolutions());
        // images
        generator.writeArrayFieldStart(IMAGES_KEY);
        info.getImages().forEach(image -> {
            try {
                generator.writeObject(image);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        generator.writeEndArray();
        // metadata
        try {
            generator.writeObjectField(METADATA_KEY, info.getMetadata());
        } catch (IOException e) {
            if (e.getMessage().contains("rdf:RDF is not allowed as an element tag here")) {
                LOGGER.warn("Ignoring RDF metadata because Jena Riot can't handle it in certain situations");
            }
        }
        generator.writeEndObject();
    }

}
