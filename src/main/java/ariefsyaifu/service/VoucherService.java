package ariefsyaifu.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

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

}
