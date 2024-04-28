package com.apostassa.infra.evento;


import com.apostassa.dominio.evento.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepositorioDeEventoComJdbcPostgres implements RepositorioDeEvento {

	private final Connection connection;
	
	public RepositorioDeEventoComJdbcPostgres(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public Evento pegarEventoPorId(UUID id) {
		try {
			ResultSet rs = buscarEventoNoBancoPorId(id);
			if (!rs.next()) {
				throw new EventoNaoEcontradoException(id);
			}
			Evento evento = mapearEventoResultSetParaEvento(id, rs);
			
			String equipeEventoAId = rs.getString("equipe_evento_a_id");
			String equipeEventoBId = rs.getString("equipe_evento_b_id");
			EquipeEvento equipeEventoA = buscarEquipeEventoPorId(equipeEventoAId, rs);
			EquipeEvento equipeEventoB = buscarEquipeEventoPorId(equipeEventoBId, rs);
			evento.setEquipeEventoA(equipeEventoA);
			evento.setEquipeEventoB(equipeEventoB);
			
			String equipeVencedora = rs.getString("equipe_vencedora_id");
			String equipePerdedora = rs.getString("equipe_perdedora_id");
			if (equipeVencedora != null && equipePerdedora != null) {
				definirEquipesVencedoraEPerdedora(evento, equipeEventoA, equipeEventoB, equipeVencedora, equipePerdedora);
			}

			rs.close();
			connection.close();
			return evento;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<Evento> pegarTodosEventos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Evento> pegarTodosEventosPorSubCategoriaId(String subCategoriaId) {
		// TODO Auto-generated method stub
		return null;
	}

	private void definirEquipesVencedoraEPerdedora(Evento evento, EquipeEvento equipeEventoA,
			EquipeEvento equipeEventoB, String equipeVencedora, String equipePerdedora) {
		if (equipeVencedora != null && equipePerdedora != null) {
			if (equipeEventoA.getId().compareTo(UUID.fromString(equipeVencedora)) == 0) {
				evento.setEquipeEventoVencedora(equipeEventoA);
				evento.setEquipeEventoPerdedora(equipeEventoB);
			}
			else {
				evento.setEquipeEventoVencedora(equipeEventoB);
				evento.setEquipeEventoPerdedora(equipeEventoA);
			}
		}
	}

	private Evento mapearEventoResultSetParaEvento(UUID id, ResultSet rs) throws SQLException {
		String nome = rs.getString("nome");
		String jogoId = rs.getString("jogo_id");
		String formato = rs.getString("formato");
		String status = rs.getString("status");
		String imagem = rs.getString("imagem");
		LocalDateTime dataEHoraInicioEvento = rs.getTimestamp("data_e_hora_inicio_evento").toLocalDateTime();
		LocalDateTime dataEHoraFimEvento = rs.getTimestamp("data_e_hora_fim_evento").toLocalDateTime();
		LocalDateTime dataEHoraCriacaoEvento = rs.getTimestamp("data_e_hora_criacao_evento").toLocalDateTime();
		
		Evento.EventoBuilder eventoBuilder = Evento.builder();
		Evento evento = eventoBuilder.id(id)
									 .nome(nome)
//									 .jogo(new Jogo(jogoId))
									 .formato(Formato.valueOf(formato))
									 .status(StatusEvento.valueOf(status))
									 .imagem(imagem)
									 .dataEHoraInicioEvento(dataEHoraInicioEvento)
									 .dataEHoraFimEvento(dataEHoraFimEvento)
									 .dataEHoraCriacaoEvento(dataEHoraCriacaoEvento)
									 .build();
		return evento;
	}

	private ResultSet buscarEventoNoBancoPorId(UUID id) throws SQLException {
		String sql = "SELECT * FROM eventos WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sql);
		
		ps.setString(1, id.toString());
		ResultSet rs = ps.executeQuery();
		return rs;
	}
	
	private EquipeEvento buscarEquipeEventoPorId(String id, ResultSet rs) {
		try {
			String sql = "SELECT nome_no_jogo, foto, jogador_id FROM eventos_jogadores WHERE equipe_evento_id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			List<JogadorEvento> jogadoresEquipeA = new ArrayList<>();
			while (rs.next()) {
				String nomeNoJogo = rs.getString("nome_no_jogo");
				String foto = rs.getString("foto");
				String jogadorId = rs.getString("jogador_id");
				jogadoresEquipeA.add(new JogadorEvento(UUID.fromString(id), nomeNoJogo, foto, UUID.fromString(jogadorId)));
			}
	
			sql = "SELECT * FROM eventos_equipes WHERE id = ?";
			ps = connection.prepareStatement(sql);
				ps.setString(1, id);
			rs = ps.executeQuery();
			
			String equipeId = rs.getString("equipe_id");
			String sigla = rs.getString("sigla");
			String nomeEquipe = rs.getString("nome");
			String imagemEquipe = rs.getString("imagem");
			
			EquipeEvento equipeEvento = new EquipeEvento(UUID.fromString(imagemEquipe), UUID.fromString(equipeId), sigla, nomeEquipe, imagemEquipe, jogadoresEquipeA);
			
			ps.close();
			return equipeEvento;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
