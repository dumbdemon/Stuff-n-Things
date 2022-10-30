package com.terransky.StuffnThings.jacksonMapper.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "codename",
    "code",
    "symbol",
    "enterDate",
    "exitDate"
})
@Generated("jsonschema2pojo")
public class Set {

    @JsonProperty("name")
    private String name;
    @JsonProperty("codename")
    private String codename;
    @JsonProperty("code")
    private String code;
    @JsonProperty("symbol")
    private Symbol symbol;
    @JsonProperty("enterDate")
    private EnterDate enterDate;
    @JsonProperty("exitDate")
    private ExitDate exitDate;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("codename")
    public String getCodename() {
        return codename;
    }

    @JsonProperty("codename")
    public void setCodename(String codename) {
        this.codename = codename;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("symbol")
    public Symbol getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("enterDate")
    public EnterDate getEnterDate() {
        return enterDate;
    }

    @JsonProperty("enterDate")
    public void setEnterDate(EnterDate enterDate) {
        this.enterDate = enterDate;
    }

    @JsonProperty("exitDate")
    public ExitDate getExitDate() {
        return exitDate;
    }

    @JsonProperty("exitDate")
    public void setExitDate(ExitDate exitDate) {
        this.exitDate = exitDate;
    }

}
