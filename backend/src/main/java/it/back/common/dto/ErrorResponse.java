package it.back.common.dto;

import it.back.common.utils.TimeFormatUtils;
import lombok.Data;

@Data
public class ErrorResponse {

    private int status;
    private String nowTime;
    private String message;

    public ErrorResponse(String message, int status) {

        this.setStatus(status);
        this.setNowTime(TimeFormatUtils.getDateTime());
        this.setMessage(message);

    }
}
