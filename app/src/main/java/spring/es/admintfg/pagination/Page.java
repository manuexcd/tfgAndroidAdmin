
package spring.es.admintfg.pagination;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "content",
    "pageable",
    "last",
    "totalPages",
    "totalElements",
    "first",
    "numberOfElements",
    "sort",
    "size",
    "number"
})
public abstract class Page<E> implements Serializable
{

    @JsonProperty("content")
    private List<E> content = null;
    @JsonProperty("pageable")
    private Pageable pageable;
    @JsonProperty("last")
    private Boolean last;
    @JsonProperty("totalPages")
    private Integer totalPages;
    @JsonProperty("totalElements")
    private Integer totalElements;
    @JsonProperty("first")
    private Boolean first;
    @JsonProperty("numberOfElements")
    private Integer numberOfElements;
    @JsonProperty("sort")
    private Sort sort;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("number")
    private Integer number;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 9170165806301042074L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Page() {
    }

    /**
     * 
     * @param content
     * @param sort
     * @param numberOfElements
     * @param last
     * @param totalElements
     * @param pageable
     * @param number
     * @param first
     * @param totalPages
     * @param size
     */
    public Page(List<E> content, Pageable pageable, Boolean last, Integer totalPages, Integer totalElements, Boolean first, Integer numberOfElements, Sort sort, Integer size, Integer number) {
        super();
        this.content = content;
        this.pageable = pageable;
        this.last = last;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.first = first;
        this.numberOfElements = numberOfElements;
        this.sort = sort;
        this.size = size;
        this.number = number;
    }

    public abstract Class<E> getClazz();

    @JsonProperty("content")
    public List<E> getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(List<E> content) {
        this.content = content;
    }

    @JsonProperty("pageable")
    public Pageable getPageable() {
        return pageable;
    }

    @JsonProperty("pageable")
    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    @JsonProperty("last")
    public Boolean getLast() {
        return last;
    }

    @JsonProperty("last")
    public void setLast(Boolean last) {
        this.last = last;
    }

    @JsonProperty("totalPages")
    public Integer getTotalPages() {
        return totalPages;
    }

    @JsonProperty("totalPages")
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @JsonProperty("totalElements")
    public Integer getTotalElements() {
        return totalElements;
    }

    @JsonProperty("totalElements")
    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    @JsonProperty("first")
    public Boolean getFirst() {
        return first;
    }

    @JsonProperty("first")
    public void setFirst(Boolean first) {
        this.first = first;
    }

    @JsonProperty("numberOfElements")
    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    @JsonProperty("numberOfElements")
    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    @JsonProperty("sort")
    public Sort getSort() {
        return sort;
    }

    @JsonProperty("sort")
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    @JsonProperty("number")
    public Integer getNumber() {
        return number;
    }

    @JsonProperty("number")
    public void setNumber(Integer number) {
        this.number = number;
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
