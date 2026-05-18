package projeto.views.componentes;

import projeto.models.Romaneios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TabelaRomaneios extends JScrollPane {

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private final Color corMarrom = new Color(60, 42, 33);
    private final Color corFundo = new Color(245, 240, 225);

    public TabelaRomaneios() {
        iniciar();
    }

    private void iniciar() {
        String[] colunas = {"ID", "Data", "Veículo", "Motorista"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Arial", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabela.getTableHeader().setBackground(new Color(239, 218, 186));
        tabela.getTableHeader().setForeground(corMarrom);
        tabela.setSelectionBackground(new Color(52, 152, 219));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setGridColor(new Color(200, 200, 200));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabela.setFillsViewportHeight(true);

        setBackground(corFundo);
        getViewport().setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setViewportView(tabela);
    }

    public void carregarDados(List<Romaneios> romaneios) {
        modeloTabela.setRowCount(0);
        for (Romaneios r : romaneios) {
            modeloTabela.addRow(new Object[]{
                    r.getId(),
                    r.getData(),
                    r.getVeiculo() != null ? r.getVeiculo().getNomeVeiculo() : "Sem veículo",
                    r.getMotorista() != null ? r.getMotorista().getNome() : "Sem motorista"
            });
        }
    }

    public int getLinhaSelecionada() { return tabela.getSelectedRow(); }
    public Object getValorColuna(int linha, int coluna) { return modeloTabela.getValueAt(linha, coluna); }
    public JTable getTabela() { return tabela; }
}
