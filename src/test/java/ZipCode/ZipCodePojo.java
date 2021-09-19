package ZipCode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZipCodePojo implements Serializable {

    @JsonProperty("place name")
    private String placeName;
    private String state;
}
