
package it.back.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.back.common.utils.TimeFormatUtils;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"status", "date", "content"})
public class ApiResponse<T> {

    private String date;
    private int status;
    private T content;

    public ApiResponse(int status, T content) {
        this.status = status;
        this.date = TimeFormatUtils.getDateTime();
        this.content = content;
    }

    public static <T> ApiResponse<T> ok(T content) {
        return new ApiResponse<>(200, content);
    }

   
}
