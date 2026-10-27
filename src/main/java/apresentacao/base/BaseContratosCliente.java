package apresentacao.base;

import apresentacao.util.Formatador;
import entidades.ContratoAluguel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Locale;
import java.util.function.Predicate;


public abstract class BaseContratosCliente extends BaseControllerCliente {

    @FXML protected TableView<ContratoAluguel> tabela;
    @FXML protected TableColumn<ContratoAluguel, String> colId;
    @FXML protected TableColumn<ContratoAluguel, String> colItem;
    @FXML protected TableColumn<ContratoAluguel, String> colRetirada;
    @FXML protected TableColumn<ContratoAluguel, String> colPrevista;
    @FXML protected TableColumn<ContratoAluguel, String> colValor;
    @FXML protected Label lblDetalheContrato;
    @FXML protected Label lblDetalheItem;
    @FXML protected Label lblDetalheRetirada;
    @FXML protected Label lblDetalhePrevista;
    @FXML protected Label lblDetalheValor;

   
    protected final ObservableList<ContratoAluguel> contratos = FXCollections.observableArrayList();
   
    protected final FilteredList<ContratoAluguel> visiveis = new FilteredList<>(contratos);

  
    protected final void prepararTabelaContratos(String textoTabelaVazia) {
        colId.setCellValueFactory(c -> prop("#" + c.getValue().getId()));
        colItem.setCellValueFactory(c -> prop(nomeItem(c.getValue())));
        colRetirada.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataRetirada())));
        colPrevista.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataDevolucaoPrevista())));
        colValor.setCellValueFactory(c -> prop(Formatador.moeda(c.getValue().getValorTotal())));
        prepararTabela(tabela, visiveis, textoTabelaVazia, this::filtrar);
    }

    
    protected abstract void filtrar();

   
    protected final void aplicarFiltro(Predicate<ContratoAluguel> filtroDaTela) {
        String termo = termoBusca();
        visiveis.setPredicate(c -> filtroDaTela.test(c)
                && (termo.isEmpty() || String.valueOf(c.getId()).equals(termo)
                    || nomeItem(c).toLowerCase(Locale.ROOT).contains(termo)));
        lblResultado.setText("Exibindo " + visiveis.size() + " de " + contratos.size());
        tabela.getSelectionModel().clearSelection();
    }

 
    protected final ContratoAluguel contratoSelecionado() {
        ContratoAluguel contrato = tabela.getSelectionModel().getSelectedItem();
        if (contrato == null || !pertenceAoCliente(contrato)) {
            throw new SecurityException("Contrato indisponível para a sua conta.");
        }
        return contrato;
    }

    protected final boolean pertenceAoCliente(ContratoAluguel contrato) {
        return contrato.getCliente() != null && contrato.getCliente().getId() == cliente.getId();
    }

    protected final void preencherDetalhesComuns(ContratoAluguel contrato) {
        lblDetalheContrato.setText("Contrato #" + contrato.getId());
        lblDetalheItem.setText("Item: " + nomeItem(contrato));
        lblDetalheRetirada.setText("Retirada: " + Formatador.data(contrato.getDataRetirada()));
        lblDetalhePrevista.setText("Devolução prevista: " + Formatador.data(contrato.getDataDevolucaoPrevista()));
        lblDetalheValor.setText("Valor do aluguel: " + Formatador.moeda(contrato.getValorTotal()));
    }

    protected final String nomeItem(ContratoAluguel contrato) {
        return contrato.getItem() == null ? "Item não encontrado" : contrato.getItem().getNome();
    }
}
