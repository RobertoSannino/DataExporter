package util;

import java.util.ArrayList;
import java.util.Arrays;

public class QueryHelper {

    private String query;
    private ArrayList<String> projectionAttributes;

    public QueryHelper(String queryPath) {
        this.query = readAndformatQuery(queryPath);
        this.projectionAttributes = inferProjectionAttributes(this.query);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<String> getProjectionAttributes() {
        return projectionAttributes;
    }

    private String readAndformatQuery(String queryPath) {
        String queryContent = new FileUtils().getFileContent(queryPath);
        int semicolonPosition = queryContent.lastIndexOf(";");
        if (semicolonPosition == queryContent.length() - 1)
            queryContent = queryContent.substring(0, queryContent.length() - 1);

        return queryContent.replaceAll("\n"," ").toUpperCase();
    }

    public String getCountQuery() {
        return "SELECT COUNT(1) " + this.query.substring(this.query.indexOf(" FROM "));
    }

    private ArrayList<String> inferProjectionAttributes(String query) {
        String attrString = query.substring(query.indexOf("SELECT") + 6, query.indexOf("FROM")).trim();
        String[] attrs = attrString.split(",");
        for(int i = 0; i < attrs.length; i++) {
            if(attrs[i].contains(" AS ")) {
                attrs[i] = attrs[i].substring(attrs[i].indexOf(" AS ") + 4);
            }
            attrs[i] = attrs[i].replaceAll(" ","");
        }
        return new ArrayList<>(Arrays.asList(attrs));
    }


}
