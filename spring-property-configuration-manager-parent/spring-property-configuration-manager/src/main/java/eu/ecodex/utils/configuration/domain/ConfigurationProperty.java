package eu.ecodex.utils.configuration.domain;

import org.springframework.core.style.ToStringCreator;

public class ConfigurationProperty {

    private String propertyName;

    private String description;

    private String label;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String toString() {
        return new ToStringCreator(this)
                .append("propertyName", propertyName)
                .append("label", label)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationProperty)) return false;

        ConfigurationProperty that = (ConfigurationProperty) o;

        return propertyName != null ? propertyName.equals(that.propertyName) : that.propertyName == null;
    }

    @Override
    public int hashCode() {
        return propertyName != null ? propertyName.hashCode() : 0;
    }
}
