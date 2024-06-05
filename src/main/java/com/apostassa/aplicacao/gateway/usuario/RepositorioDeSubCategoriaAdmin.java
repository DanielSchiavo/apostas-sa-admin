package com.apostassa.aplicacao.gateway.usuario;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.AlterarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.DeletarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;

import java.util.List;

public interface RepositorioDeSubCategoriaAdmin {

	public void cadastrarSubCategoria(SubCategoria subCategoria) throws ValidacaoException;

	public void alterarSubCategoria(SubCategoria subCategoria) throws AlterarSubCategoriaException, ValidacaoException;

	public void deletarSubCategoria(String subCategoriaId) throws DeletarSubCategoriaException, ValidacaoException;

	public boolean verificarSeNomeSubCategoriaJaExiste(String nome) throws ValidacaoException;


	public SubCategoria pegarSubCategoriaPorId(String subCategoriaId) throws ValidacaoException;

	public List<SubCategoria> pegarTodasSubCategorias() throws ValidacaoException;

	public List<SubCategoria> pegarTodasSubCategoriasPorCategoriaId(String string);


	public boolean verificarSeSubCategoriaIdExiste(String subCategoriaId) throws ValidacaoException;

}
