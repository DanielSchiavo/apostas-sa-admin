package com.apostassa.infra.aposta;


import com.apostassa.dominio.aposta.Aposta;
import com.apostassa.dominio.aposta.RepositorioDeAposta;
import com.apostassa.dominio.aposta.StatusAposta;
import com.apostassa.dominio.aposta.TipoAposta;
import com.apostassa.dominio.equipe.Equipe;
import com.apostassa.dominio.evento.Evento;
import com.apostassa.infra.evento.RepositorioDeEventoComJdbcPostgres;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeApostaComJdbcPostgres implements RepositorioDeAposta {

	private final Connection connection;
	
	public RepositorioDeApostaComJdbcPostgres(Connection connection, RepositorioDeEventoComJdbcPostgres repositorioDeEventos) {
		this.connection = connection;
	}
	
	@Override
	public void fazerAposta(Evento evento, Equipe equipe, TipoAposta tipoAposta, BigDecimal valor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Aposta> buscarTodasApostasPorClienteId(String id) {
		try {
			String sql = "SELECT * FROM apostas WHERE usuario_id = ? ORDER BY data_e_hora ASC";
			PreparedStatement ps = connection.prepareStatement(sql);
			
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			List<Aposta> apostas = new ArrayList<>();
			while (rs.next()) {
				String usuarioCpf = rs.getString("usuario_id");
				String eventoId = rs.getString("evento_id");
				String equipeEventoApostadaId = rs.getString("equipe_evento_apostada_id");
				String tipoAposta = rs.getString("tipo_aposta");
				BigDecimal valorApostado = rs.getBigDecimal("valor_apostado");
				double chancesNoMomentoDaAposta = rs.getDouble("chances_no_momento_da_aposta");
				double chancesNoEncerramentoDaAposta = rs.getDouble("chances_no_encerramento_da_aposta");
				String status = rs.getString("status");
				LocalDateTime dataEHora = rs.getTimestamp("data_e_hora").toLocalDateTime();
				
				Aposta.ApostaBuilder apostaBuilder = Aposta.builder();
				Aposta aposta = apostaBuilder.usuarioId(usuarioCpf)
											 .evento(new Evento(eventoId))
											 .equipeEventoApostadaId(equipeEventoApostadaId)
											 .tipoAposta(TipoAposta.valueOf(tipoAposta))
											 .valorApostado(valorApostado)
											 .chancesNoMomentoDaAposta(chancesNoMomentoDaAposta)
											 .chancesNoEncerramentoDaAposta(chancesNoEncerramentoDaAposta)
											 .statusAposta(StatusAposta.valueOf(status))
											 .dataEHora(dataEHora).build();
				apostas.add(aposta);
			}
			
			ps.close();
			rs.close();
			connection.close();
			return apostas;
		} catch (SQLException e) {
			throw new RuntimeException("Não foi possivel recuperar as apostas");
		}
	}

	@Override
	public Aposta buscarApostaPorApostaIdEClienteId(String apostaId, String clienteId) {
		// TODO Auto-generated method stub
		return null;
	}

}
