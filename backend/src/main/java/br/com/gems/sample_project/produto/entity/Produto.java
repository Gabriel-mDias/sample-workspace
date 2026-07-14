package br.com.gems.sample_project.produto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table( name = "PRODUTO" )
@SQLDelete( sql = "UPDATE PRODUTO SET DT_EXCLUSAO = NOW() WHERE ID_PRODUTO = ?" )
@SQLRestriction( "DT_EXCLUSAO IS NULL" )
public class Produto extends EntidadeAuditavel {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "ID_PRODUTO" )
    private UUID id;

    @Column( name = "NM_NOME", nullable = false )
    private String nome;

    @Column( name = "DS_DESCRICAO", length = 4000 )
    private String descricao;

    @Column( name = "VL_PRECO", nullable = false )
    private BigDecimal preco;

}
