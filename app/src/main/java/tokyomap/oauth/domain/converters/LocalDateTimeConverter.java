package tokyomap.oauth.domain.converters;

import java.sql.Timestamp;

import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
    return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
  }

  @Override
  public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
    return timestamp == null ? null : timestamp.toLocalDateTime();
  }
}
