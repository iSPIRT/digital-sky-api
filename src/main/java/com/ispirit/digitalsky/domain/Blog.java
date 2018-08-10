package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Entity
@Table(name = "ds_blog")
public class Blog {

    private static String delimiter = "<=section=>";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "TITLE")
    @NotNull
    private String title;

    @Column(name = "CONTENT")
    @JsonIgnore
    private String content;

    @Column(name = "CREATED_BY_ID")
    @JsonIgnore
    private long createdBy;

    @Column(name = "UPDATED_BY_ID")
    @JsonIgnore
    private long updatedBy;

    @JsonIgnore
    @Column(name = "CREATED_TIMESTAMP")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime createdTime;

    @JsonIgnore
    @Column(name = "UPDATED_TIMESTAMP")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime updatedTime;

    @Transient
    private List<String> sections = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public long getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSections(List<String> newSections) {
        this.sections.clear();
        if (newSections == null || newSections.isEmpty()) return;
        this.sections.addAll(newSections);
    }

    public void resolveContentFromSections() {
        content = collectionToDelimitedString(sections, delimiter);
    }

    public void resolveSectionsFromContent() {
        String[] sectionsArray = StringUtils.delimitedListToStringArray(content, delimiter);
        sections.clear();
        sections.addAll(asList(sectionsArray));
    }
}
