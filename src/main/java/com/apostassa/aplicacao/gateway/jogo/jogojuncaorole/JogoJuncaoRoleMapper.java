package com.apostassa.aplicacao.gateway.jogo.jogojuncaorole;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JogoJuncaoRoleMapper {
}
