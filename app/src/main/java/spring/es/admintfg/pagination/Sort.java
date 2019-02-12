
package spring.es.admintfg.pagination;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sorted",
    "unsorted"
})
public class Sort implements Serializable
{

    @JsonProperty("sorted")
    private Boolean sorted;
    @JsonProperty("unsorted")
    private Boolean unsorted;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 7283602637799012934L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Sort() {
    }

    /**
     * 
     * @param unsorted
     * @param sorted
     */
    public Sort(Boolean sorted, Boolean unsorted) {
        super();
        this.sorted = sorted;
        this.unsorted = unsorted;
    }

    @JsonProperty("sorted")
    public Boolean getSorted() {
        return sorted;
    }

    @JsonProperty("sorted")
    public void setSorted(Boolean sorted) {
        this.sorted = sorted;
    }

    @JsonProperty("unsorted")
    public Boolean getUnsorted() {
        return unsorted;
    }

    @JsonProperty("unsorted")
    public void setUnsorted(Boolean unsorted) {
        this.unsorted = unsorted;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
