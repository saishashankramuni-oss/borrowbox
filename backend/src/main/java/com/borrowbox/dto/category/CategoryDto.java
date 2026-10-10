package com.borrowbox.dto.category;

public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String iconName;
    private Boolean active;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String slug, String description, String iconName, Boolean active) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.iconName = iconName;
        this.active = active;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private String iconName;
        private Boolean active;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder iconName(String iconName) { this.iconName = iconName; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }

        public CategoryDto build() {
            return new CategoryDto(id, name, slug, description, iconName, active);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
