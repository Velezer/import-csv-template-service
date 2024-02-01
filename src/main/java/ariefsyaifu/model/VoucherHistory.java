package ariefsyaifu.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "voucher_history", indexes = {
        @Index(name = "idx_voucher_history_voucher_code", columnList = "voucher_code"),
})
public class VoucherHistory extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Column(name = "voucher_code")
    public String voucherCode;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    public Voucher voucher;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    public Type type;

    public enum Type {
        REDEEMED, FAILED, AVAILABLE, CLAIMED
    }

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public Instant updatedAt;

}