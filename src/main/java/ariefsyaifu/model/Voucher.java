package ariefsyaifu.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "voucher", indexes = {
        @Index(name = "idx_voucher_prefix_code", columnList = "prefix_code"),
})
public class Voucher extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    public String id;

    @Column(name = "name")
    public String name;
    
    @Column(name = "amount")
    public BigDecimal amount;

    @Column(name = "prefix_code", unique = true)
    public String prefixCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public Instant updatedAt;

}