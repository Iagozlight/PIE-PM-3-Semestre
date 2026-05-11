package view.telas;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal{



    public TelaPrincipal(){
        //--------- DEFINIÇÕES ---------
        JFrame frame = new JFrame("Tela Principal");
        frame.setSize(800, 600);
        frame.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));


        //------ ADDS -------

        frame.add(panel);



        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
