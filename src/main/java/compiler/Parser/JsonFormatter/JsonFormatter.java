package compiler.Parser.JsonFormatter;
import org.json.JSONObject;

public class JsonFormatter {

    public String formatStringBuilder(StringBuilder sb) {
        JSONObject jsonObject = new JSONObject(sb.toString());
        return jsonObject.toString(4);
    }
}