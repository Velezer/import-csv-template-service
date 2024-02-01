package ariefsyaifu.dto.voucher;

import java.math.BigDecimal;

import ariefsyaifu.model.Voucher;

public class VoucherDto {
    public String id;
    public String name;
    public BigDecimal amount;
    public String prefixCode;

    public static VoucherDto valueOf(Voucher v) {
        VoucherDto dto = new VoucherDto();
        dto.id = v.id;
        dto.name = v.name;
        dto.amount = v.amount;
        dto.prefixCode = v.prefixCode;
        return dto;
    }

}
