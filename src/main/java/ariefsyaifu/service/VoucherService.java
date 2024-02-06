package ariefsyaifu.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Stream;

import org.jboss.resteasy.reactive.server.StreamingOutputStream;
import org.jboss.resteasy.reactive.server.providers.serialisers.StreamingOutputMessageBodyWriter;

import ariefsyaifu.dao.VoucherDao;
import ariefsyaifu.dto.voucher.FailedImportDto;
import ariefsyaifu.dto.voucher.VoucherDto;
import ariefsyaifu.model.Voucher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VoucherService {

    @Inject
    public VoucherService(VoucherDao voucherDao) {
        this.voucherDao = voucherDao;
    }

    private VoucherDao voucherDao;

    public List<FailedImportDto> importCsv(FileInputStream file, String delimiter) throws IOException {
        return voucherDao.importCsv(file, delimiter.isBlank() ? "," : delimiter);
    }

    public List<VoucherDto> getVouchers() {
        List<Voucher> vouchers = voucherDao.getVouchers();
        return vouchers.stream().map(VoucherDto::valueOf).toList();
    }

    public StreamingOutputStream export() throws IOException {
        List<Voucher> vouchers = Voucher.findAll().list();
        try (StreamingOutputStream out = new StreamingOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(out);) {
            writer.write("id,name,amount,prefixCode\n");
            for (Voucher v : vouchers) {
                writer.write(String.format("%s,%s,%s,%s\n", v.id, v.name, v.amount, v.prefixCode));
            }
            return out;
        }
    }

}
