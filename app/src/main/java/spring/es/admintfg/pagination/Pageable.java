
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
    "sort",
    "pageSize",
    "pageNumber",
    "offset",
    "paged",
    "unpaged"
})
public class Pageable implements Serializable
{

    @JsonProperty("sort")
    private Sort sort;
    @JsonProperty("pageSize")
    private Integer pageSize;
    @JsonProperty("pageNumber")
    private Integer pageNumber;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("paged")
    private Boolean paged;
    @JsonProperty("unpaged")
    private Boolean unpaged;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -8512128238239869505L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Pageable() {
    }

    /**
     * 
     * @param paged
     * @param sort
     * @param pageSize
     * @param pageNumber
     * @param offset
     * @param unpaged
     */
    public Pageable(Sort sort, Integer pageSize, Integer pageNumber, Integer offset, Boolean paged, Boolean unpaged) {
        super();
        this.sort = sort;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.offset = offset;
        this.paged = paged;
        this.unpaged = unpaged;
    }

    @JsonProperty("sort")
    public Sort getSort() {
        return sort;
    }

    @JsonProperty("sort")
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @JsonProperty("pageSize")
    public Integer getPageSize() {
        return pageSize;
    }

    @JsonProperty("pageSize")
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @JsonProperty("pageNumber")
    public Integer getPageNumber() {
        return pageNumber;
    }

    @JsonProperty("pageNumber")
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    @JsonProperty("offset")
    public Integer getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @JsonProperty("paged")
    public Boolean getPaged() {
        return paged;
    }

    @JsonProperty("paged")
    public void setPaged(Boolean paged) {
        this.paged = paged;
    }

    @JsonProperty("unpaged")
    public Boolean getUnpaged() {
        return unpaged;
    }

    @JsonProperty("unpaged")
    public void setUnpaged(Boolean unpaged) {
        this.unpaged = unpaged;
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
