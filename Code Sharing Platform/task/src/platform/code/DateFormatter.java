package platform.code;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface DateFormatter {
    default String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(dateTimeFormatter);
    }
}
