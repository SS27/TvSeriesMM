package com.spstanchev.tvseries.models;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Person {
    private Integer id;
    private String url;
    private String name;
    private Image image;
    private com.spstanchev.tvseries.models.Links Links;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The image
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return The Links
     */
    public com.spstanchev.tvseries.models.Links getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(com.spstanchev.tvseries.models.Links Links) {
        this.Links = Links;
    }
}
