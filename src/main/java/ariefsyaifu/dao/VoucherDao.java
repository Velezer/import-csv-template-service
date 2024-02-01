package ariefsyaifu.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ariefsyaifu.dto.voucher.FailedImportDto;
import ariefsyaifu.model.Voucher;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VoucherDao {

    @Transactional
    public List<FailedImportDto> importCsv(FileInputStream file, String delimiter) throws IOException {
        List<FailedImportDto> r = new ArrayList<>();
        InputStreamReader ir = new InputStreamReader(file);
        try (BufferedReader br = new BufferedReader(ir)) {
            String[] headers = br.readLine().split(delimiter);
            if (headers.length != 3) { // we want 3 columns
                throw new HttpException(HttpResponseStatus.BAD_REQUEST.code(), "MUST_3_COLUMNS");
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] contents = line.split(delimiter);

                int i = 0;
                String name = contents[i++];
                String amount = contents[i++];
                String prefixCode = contents[i++];
                boolean isPresent = Voucher.find("prefixCode = ?1", prefixCode).firstResultOptional().isPresent();
                if (isPresent) {
                    r.add(FailedImportDto.valueOf(prefixCode, "prefixCode already exists"));
                    continue;
                }
                Voucher voucher = new Voucher();
                voucher.name = name;
                voucher.amount = BigDecimal.valueOf(Double.valueOf(amount));
                voucher.prefixCode = prefixCode;
                voucher.persist();
            }
        }
        return r;

    }

    public List<Voucher> getVouchers() {
        return Voucher.findAll().list();
    }

}
