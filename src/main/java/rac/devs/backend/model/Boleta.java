package rac.devs.backend.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "boletas")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "boleta_seq")
    @SequenceGenerator(name = "boleta_seq", sequenceName = "boleta_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "fecha_emision", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEmision;

    @Column(name = "total", nullable = false)
    private Double total;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "boleta_id")
    private List<Producto> productos;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
