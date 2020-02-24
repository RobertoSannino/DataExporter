package design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GenericRecord {

    private HashMap<String, String> attributes;

    public GenericRecord(GenericRecord gr) {
        this.attributes = new HashMap<>(gr.getAttributes());
    }

    public GenericRecord() {}

    public void addAttribute(String key, String value) {
        if(this.attributes == null)
            attributes = new HashMap<>();
        attributes.put(key, value);
    }

    private HashMap<String, String> getAttributes() { return this.attributes; }

    public String getValueOf(String attr) {
        return this.attributes.get(attr);
    }

    public String toStringValues(ArrayList<String> order) {
        StringBuilder values = new StringBuilder();
        for(String attr : order) {
            values.append("\"").append(this.attributes.get(attr)).append("\"").append(", ");
        }
        values = new StringBuilder(values.substring(0, values.lastIndexOf(",")));
        return values.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericRecord record = (GenericRecord) o;
        return Objects.equals(attributes, record.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes);
    }

    public String toString() {
        return this.attributes.toString();
    }
}
