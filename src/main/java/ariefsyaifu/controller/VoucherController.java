package ariefsyaifu.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import ariefsyaifu.dto.error.ErrorOas;
import ariefsyaifu.dto.voucher.FailedImportDto;
import ariefsyaifu.dto.voucher.ImportVoucherRequestBody;
import ariefsyaifu.service.VoucherService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/v1/voucher")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VoucherController {

    @Inject
    public VoucherController(VoucherService voucherService, Validator validator) {
        this.voucherService = voucherService;
        this.validator = validator;
    }

    private Validator validator;
    private VoucherService voucherService;

    @GET
    @Path("/")
    @Operation(description = "Get Vouchers")
    public Response get() {
        return Response.ok(new JsonArray(voucherService.getVouchers())).build();
    }

    @GET
    @Path("/export")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(description = "Export Vouchers")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM)),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response export() throws IOException {
        return Response.ok(voucherService.export())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exported-vouchers.csv")
                .build();
    }

    @POST
    @Path("/import")
    @Operation(summary = "Import Voucher")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = FailedImportDto[].class))),
            @APIResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class))),
            @APIResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorOas.class)))
    })
    public Response importCsv(ImportVoucherRequestBody params) throws IOException {
        Set<ConstraintViolation<ImportVoucherRequestBody>> violations = validator.validate(params);
        for (ConstraintViolation<ImportVoucherRequestBody> v : violations) {
            throw new HttpException(HttpResponseStatus.BAD_REQUEST.code(), v.getMessage());
        }
        List<FailedImportDto> r = voucherService.importCsv(
                new FileInputStream(params.file),
                Optional.ofNullable(params.delimiter).orElse(""));
        return Response.ok(new JsonArray(r)).build();
    }

}
