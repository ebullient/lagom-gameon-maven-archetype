#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api.protocol;

import ${package}.api.protocol.GameOnMessage.ParsedMessage;
import com.lightbend.lagom.javadsl.api.deser.DeserializationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameOnMessageTest {
    // Use embedded commas in the JSON to ensure the parser doesn't split in the middle of it
    private static final String PAYLOAD = "{${symbol_escape}"payload${symbol_escape}":${symbol_escape}"value${symbol_escape}",${symbol_escape}"key${symbol_escape}":${symbol_escape}"value${symbol_escape}"}";

    @Test(expected = DeserializationException.class)
    public void deserializeInvalidMessageWithoutPayload() {
        ParsedMessage message = GameOnMessage.parse("invalid,message");
        fail("Expected DeserializationException but got: " + message);
    }

    @Test(expected = DeserializationException.class)
    public void deserializeInvalidMessageWithOnlyPayload() {
        ParsedMessage message = GameOnMessage.parse(PAYLOAD);
        fail("Expected DeserializationException but got: " + message);
    }

    @Test(expected = DeserializationException.class)
    public void deserializeInvalidMessageWithNoCommaSeparator() {
        ParsedMessage message = GameOnMessage.parse("invalid" + PAYLOAD);
        fail("Expected DeserializationException but got: " + message);
    }

    @Test
    public void deserializeMessageWithTargetAndPayload() {
        ParsedMessage message = GameOnMessage.parse("target," + PAYLOAD);
        assertEquals("target", message.getTarget());
        assertFalse(message.getRecipient().isPresent());
        assertEquals(PAYLOAD, message.getPayload());
    }

    @Test
    public void deserializeMessageWithTargetRecipientAndPayload() {
        ParsedMessage message = GameOnMessage.parse("target,recipient," + PAYLOAD);
        assertEquals("target", message.getTarget());
        assertTrue(message.getRecipient().isPresent());
        assertEquals("recipient", message.getRecipient().get());
        assertEquals(PAYLOAD, message.getPayload());
    }

    @Test
    public void ignoresInvalidJSON() {
        // This parser doesn't deal with JSON parsing
        ParsedMessage message = GameOnMessage.parse("target,recipient,{invalid,JSON");
        assertEquals("target", message.getTarget());
        assertTrue(message.getRecipient().isPresent());
        assertEquals("recipient", message.getRecipient().get());
        assertEquals("{invalid,JSON", message.getPayload());
    }
}
