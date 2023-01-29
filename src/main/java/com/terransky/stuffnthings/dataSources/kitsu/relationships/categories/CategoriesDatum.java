package com.terransky.stuffnthings.dataSources.kitsu.relationships.categories;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.Datum;
import java.util.Objects;
import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "type", "links", "attributes", "relationships"})
@Generated("jsonschema2pojo")
public class CategoriesDatum extends Datum {

  @JsonProperty("attributes")
  private CategoriesAttributes attributes;

  @JsonProperty("relationships")
  private CategoriesRelationships relationships;

  @JsonProperty("attributes")
  public CategoriesAttributes getAttributes() {
    return attributes;
  }

  @JsonProperty("attributes")
  public void setAttributes(CategoriesAttributes attributes) {
    this.attributes = attributes;
  }

  @JsonProperty("relationships")
  public CategoriesRelationships getRelationships() {
    return relationships;
  }

  @JsonProperty("relationships")
  public void setRelationships(CategoriesRelationships relationships) {
    this.relationships = relationships;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CategoriesDatum that = (CategoriesDatum) o;
    return getAttributes().equals(that.getAttributes())
        && getRelationships().equals(that.getRelationships());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getAttributes(), getRelationships());
  }
}
