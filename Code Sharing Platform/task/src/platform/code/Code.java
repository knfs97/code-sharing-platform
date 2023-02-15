package platform.code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Code implements DateFormatter {

    public enum Restriction {
        TIME,
        VIEWS,
        BOTH,
        NON
    }

    @GeneratedValue
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Id
    @Column(length = 36, unique = true, nullable = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private UUID id;

    @JsonProperty("code")
    private String snippet;
    @JsonProperty("date")
    // @Transient
    private LocalDateTime loadedAt;
    @JsonProperty("time")
    private Long expirationTime;
    @JsonProperty("views")
    private Long views;
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Restriction restriction;

    public Code(String snippet) {
        this.snippet = snippet;
    }

    public Code(String snippet, Restriction restriction) {
        this.snippet = snippet;
        this.restriction = restriction;
        this.loadedAt = LocalDateTime.now();
    }

    public Code(UUID id) {
        this.id = id;
    }

    public void checkAndSetRestriction() {
        if (getViews() != 0 && getExpirationTime() != 0) {
            setRestriction(Restriction.BOTH);
        } else if (getViews() != 0) {
            setRestriction(Restriction.VIEWS);
        } else if (getExpirationTime() != 0) {
            setRestriction(Restriction.TIME);
        } else {
            setRestriction(Restriction.NON);
        }
    }
    public String getLoadedAt() {
        if (loadedAt == null) {
            return null;
        }
        return formatDate(loadedAt);
    }

}
