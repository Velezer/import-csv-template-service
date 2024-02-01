package ariefsyaifu.dto.voucher;

public class FailedImportDto {
    public String prefixCode;
    public String reason;

    public static FailedImportDto valueOf(String prefixCode, String reason) {
        FailedImportDto dto = new FailedImportDto();
        dto.prefixCode = prefixCode;
        dto.reason = reason;
        return dto;

    }
}
