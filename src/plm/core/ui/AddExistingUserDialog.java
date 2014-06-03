/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plm.core.ui;

import plm.core.model.Game;
/**
 *
 * @author Ced
 */
public class AddExistingUserDialog extends javax.swing.JDialog {

	private String[] result = new String[2];

	/**
	 * Creates new form AddExistingUserDialog
	 *
	 * @param parent
	 * @param modal
	 */
	public AddExistingUserDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		result[0] = null;
		result[1] = null;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        titleLabel = new javax.swing.JLabel();
        uuidLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        uuidField = new javax.swing.JTextField();
        usernameField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add user with a UUID");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        titleLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        titleLabel.setText(Game.i18n.tr("Add user with an UUID"));
        titleLabel.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        getContentPane().add(titleLabel, gridBagConstraints);

        uuidLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        uuidLabel.setText(Game.i18n.tr("UUID :"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        getContentPane().add(uuidLabel, gridBagConstraints);

        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        usernameLabel.setText(Game.i18n.tr("Username :"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        getContentPane().add(usernameLabel, gridBagConstraints);

        uuidField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(uuidField, gridBagConstraints);

        usernameField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(usernameField, gridBagConstraints);

        addButton.setText(Game.i18n.tr("Add"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        getContentPane().add(addButton, gridBagConstraints);

        cancelButton.setText(Game.i18n.tr("Cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        getContentPane().add(cancelButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        getContentPane().add(filler3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        getContentPane().add(filler4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        getContentPane().add(filler5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 7;
        getContentPane().add(filler6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        getContentPane().add(filler7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        getContentPane().add(filler8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        getContentPane().add(filler1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
		result[0] = usernameField.getText();
		result[1] = uuidField.getText();
		setVisible(false);
		dispose();
    }//GEN-LAST:event_addButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		result[0] = null;
		result[1] = null;
		setVisible(false);
		dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

	public String[] showDialog() {
		setVisible(true);
		return result;
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField uuidField;
    private javax.swing.JLabel uuidLabel;
    // End of variables declaration//GEN-END:variables
}
