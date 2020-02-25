package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryHelper.class);
    private String query;
    private ArrayList<String> projectionAttributes;

    public QueryHelper(String queryPath) {
        this.query = readAndformatQuery(queryPath);
        this.projectionAttributes = inferProjectionAttributes(this.query);
    }

    public String getQuery() {
        return query;
    }

    public ArrayList<String> getProjectionAttributes() {
        return projectionAttributes;
    }

    private String readAndformatQuery(String queryPath) {
        String queryContent = new FileUtils().getFileContent(queryPath);
        int semicolonPosition = queryContent.lastIndexOf(";");
        if (semicolonPosition == queryContent.length() - 1)
            queryContent = queryContent.substring(0, queryContent.length() - 1);

        queryContent = queryContent.replaceAll("\n"," ").toUpperCase();
        LOGGER.debug("SUBMITTED QUERY: " + queryContent);
        return queryContent;
    }

    public String getCountQuery() {
        String countQuery = "SELECT COUNT(1) " + this.query.substring(this.query.indexOf(" FROM "));
        if(this.query.lastIndexOf("ORDER BY") > -1)
            countQuery = countQuery.substring(0, countQuery.indexOf("ORDER BY"));

        LOGGER.debug("COUNT QUERY: " + countQuery);
        return countQuery;
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

        ArrayList<String> projAttrs = new ArrayList<>(Arrays.asList(attrs));
        LOGGER.debug("INFERRED PROJECTION ATTRIBUTES: " + projAttrs.toString());
        return projAttrs;
    }


    public String getOracleRangedQuery(int[] range) {
        String rangedQuery = query.substring(0, query.indexOf(" FROM ")) + ", ROWNUM r " + query.substring(query.indexOf(" FROM "));
        rangedQuery = "SELECT * FROM ( " + rangedQuery + " ) WHERE r >= " + range[0] + " and r < " + range[1] + " order by r";

        LOGGER.debug("RANGED QUERY: " + rangedQuery);
        return rangedQuery;
    }

    public String getMySqlRangedQuery(int[] range) {
        String rangedQuery =  this.query + " LIMIT " + range[0] + "," + (range[1] - range[0]);
        LOGGER.debug("RANGED QUERY: " + rangedQuery);
        return rangedQuery;
    }

    public String getPostgresRangedQuery(int[] range) {
        String rangedQuery =   this.query + " OFFSET " + range[0] + " LIMIT " + (range[1] - range[0]);
        LOGGER.debug("RANGED QUERY: " + rangedQuery);
        return rangedQuery;
    }
}
