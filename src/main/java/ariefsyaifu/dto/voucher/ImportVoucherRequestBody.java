package ariefsyaifu.dto.voucher;

import java.io.File;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.RestForm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ImportVoucherRequestBody {

    @RestForm
    @Schema(required = true)
    @NotNull(message = "file required")
    public File file;

    @RestForm
    @Schema(defaultValue = ",", required = true)
    @NotBlank(message = "delimiter required")
    public String delimiter;
}
