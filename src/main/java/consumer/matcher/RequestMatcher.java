package consumer.matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.ValueMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import uk.gov.companieshouse.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class RequestMatcher implements ValueMatcher<Request> {

    private final String expectedOutput;
    private final String expectedUrl;
    private final List<String> fieldsToIgnore;
    private Logger logger;

    public RequestMatcher(Logger logger, String output, String expectedUrl) {
        this.expectedOutput = output;
        this.logger = logger;
        this.expectedUrl = expectedUrl;
        this.fieldsToIgnore = new ArrayList<>();;
    }

    public RequestMatcher(Logger logger, String output, String expectedUrl, List<String> fieldsToIgnore) {
        this.expectedOutput = output;
        this.logger = logger;
        this.expectedUrl = expectedUrl;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    @Override
    public MatchResult match(Request value) {
        return MatchResult.aggregate(matchUrl(value.getUrl()), matchMethod(value.getMethod()),
                matchBody(value.getBodyAsString()));
    }

    private MatchResult matchUrl(String actualUrl) {

        MatchResult urlResult = MatchResult.of(expectedUrl.equals(actualUrl));


        if (! urlResult.isExactMatch()) {
            logger.error("url does not match expected: <" + expectedUrl + "> actual: <" + actualUrl + ">");
        }

        return urlResult;
    }

    private MatchResult matchMethod(RequestMethod actualMethod) {
        RequestMethod expectedMethod = RequestMethod.PUT;

        MatchResult typeResult = MatchResult.of(expectedMethod.equals(actualMethod));

        if (! typeResult.isExactMatch()) {
            logger.error("Method does not match expected: <" + expectedMethod + "> actual: <" + actualMethod + ">");
        }

        return typeResult;
    }

    private MatchResult matchBody(String actualBody) {

        MatchResult bodyResult;
        JSONObject expectedBody;
        try {
            expectedBody = new JSONObject(expectedOutput);
        } catch (JSONException e) {
            logger.error("Could not process expectedBody JSON: " + e);
            return MatchResult.of(false);
        }

        JSONObject actual;
        try {
            actual = new JSONObject(actualBody);
            fieldsToIgnore.forEach(fieldName ->{
                try {
                    removeField(actual, fieldName);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (JSONException e) {
            logger.error("Could not process actualBody JSON: " + e);
            return MatchResult.of(false);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode expectedNode = mapper.readTree(expectedBody.toString());
            JsonNode actualNode = mapper.readTree(actual.toString());
            bodyResult = MatchResult.of(expectedNode.equals(actualNode));

        } catch (JsonProcessingException ex) {
            return MatchResult.of(false);
        }

        if (! bodyResult.isExactMatch()) {
            logger.error("Body does not match expected: <" + expectedBody + "> actual: <" + actualBody + ">");
        }

        return bodyResult;
    }

    public JSONObject removeField(JSONObject json , String fieldName) throws JSONException {
        String key = fieldName.split("\\.")[0];
        if (json.has(fieldName)){
            json.remove(key);
        }
        else if (json.has(key)) {
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
