package main.java.com.ubo.tp.message.ihm.template.component;

import main.java.com.ubo.tp.message.datamodel.Channel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Composant graphique représentant un canal.
 * Expose addSelectListener() — pas de référence vers le contrôleur.
 */
public class ChannelComponent extends JPanel {

    private final JButton selectButton;

    public ChannelComponent(Channel channel) {
        super(new GridBagLayout());
        setBackground(new Color(0x3A3A3A));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x555555)));

        Insets insets = new Insets(6, 10, 6, 10);

        // Icône "#"
        JLabel hashLabel = new JLabel("#");
        hashLabel.setFont(hashLabel.getFont().deriveFont(Font.BOLD, 16f));
        hashLabel.setForeground(new Color(0xAAAAAA));
        add(hashLabel, new GridBagConstraints(0, 0,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        // Nom du canal
        JLabel nameLabel = new JLabel(channel.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 13f));
        nameLabel.setForeground(Color.WHITE);
        add(nameLabel, new GridBagConstraints(1, 0,
                1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        // Créateur
        JLabel creatorLabel = new JLabel("par @" + channel.getCreator().getUserTag());
        creatorLabel.setFont(creatorLabel.getFont().deriveFont(Font.ITALIC, 10f));
        creatorLabel.setForeground(new Color(0x888888));
        add(creatorLabel, new GridBagConstraints(2, 0,
                1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        selectButton = new JButton();
        selectButton.setVisible(false);
        add(selectButton);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                selectButton.doClick();
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(new Color(0x505050));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(new Color(0x3A3A3A));
            }
        });
    }

    /** Le contrôleur s'abonne ici. */
    public void addSelectListener(ActionListener listener) {
        selectButton.addActionListener(listener);
    }
}