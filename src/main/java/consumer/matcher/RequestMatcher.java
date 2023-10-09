package consumer.matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import uk.gov.companieshouse.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class RequestMatcher {

    private final String expectedOutput;
    private final String expectedUrl;
    private final List<String> fieldsToIgnore;
    private Logger logger;

    public RequestMatcher(Logger logger, String output, String expectedUrl) {
        this.expectedOutput = output;
        this.logger = logger;
        this.expectedUrl = expectedUrl;
        this.fieldsToIgnore = new ArrayList<>();
    }

    public RequestMatcher(Logger logger, String output, String expectedUrl, List<String> fieldsToIgnore) {
        this.expectedOutput = output;
        this.logger = logger;
        this.expectedUrl = expectedUrl;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    public boolean match(String actualUrl, String actualMethod, String actualBody) {
        return matchUrl(actualUrl) && matchMethod(actualMethod) && matchBody(actualBody);
    }

    private boolean matchUrl(String actualUrl) {
        boolean urlResult = expectedUrl.equals(actualUrl);

        if (!urlResult) {
            logger.error("URL does not match expected: <" + expectedUrl + "> actual: <" + actualUrl + ">");
        }

        return urlResult;
    }

    private boolean matchMethod(String actualMethod) {
        String expectedMethod = "PUT"; // Change as needed for your use case

        boolean typeResult = expectedMethod.equals(actualMethod);

        if (!typeResult) {
            logger.error("Method does not match expected: <" + expectedMethod + "> actual: <" + actualMethod + ">");
        }

        return typeResult;
    }

    private boolean matchBody(String actualBody) {
        try {
            JSONObject expectedBody = new JSONObject(expectedOutput);
            JSONObject actual = new JSONObject(actualBody);
            fieldsToIgnore.forEach(fieldName -> {
                try {
                    removeField(actual, fieldName);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            ObjectMapper mapper = new ObjectMapper();
            JsonNode expectedNode = mapper.readTree(expectedBody.toString());
            JsonNode actualNode = mapper.readTree(actual.toString());

            boolean bodyResult = expectedNode.equals(actualNode);

            if (!bodyResult) {
                logger.error("Body does not match expected: <" + expectedBody + "> actual: <" + actualBody + ">");
            }

            return bodyResult;
        } catch (JSONException | JsonProcessingException ex) {
            logger.error("Error processing JSON: " + ex);
            return false;
        }
    }

    public JSONObject removeField(JSONObject json, String fieldName) throws JSONException {
        String key = fieldName.split("\\.")[0];
        if (json.has(fieldName)) {
            json.remove(key);
        } else if (json.has(key)) {
            if (json.get(key) instanceof JSONObject) {
                JSONObject value = json.getJSONObject(key);
                removeField(value, fieldName.substring(fieldName.indexOf(".") + 1));
            } else {
                json.remove(key);
            }
        }
        return json;
    }
}
