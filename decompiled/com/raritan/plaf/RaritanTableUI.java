/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellEditor;

public class RaritanTableUI
extends BasicTableUI {
    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanTableUI();
    }

    @Override
    protected void installKeyboardActions() {
        ActionMap actionMap = this.getThisActionMap();
        SwingUtilities.replaceUIActionMap(this.table, actionMap);
        InputMap inputMap = this.getThisInputMap(1);
        SwingUtilities.replaceUIInputMap(this.table, 1, inputMap);
    }

    InputMap getThisInputMap(int n) {
        if (n == 1) {
            InputMap inputMap;
            InputMap inputMap2 = (InputMap)UIManager.get("Table.ancestorInputMap");
            if (this.table.getComponentOrientation().isLeftToRight() || (inputMap = (InputMap)UIManager.get("Table.ancestorInputMap.RightToLeft")) == null) {
                return inputMap2;
            }
            inputMap.setParent(inputMap2);
            return inputMap;
        }
        return null;
    }

    ActionMap getThisActionMap() {
        ActionMap actionMap = (ActionMap)UIManager.get("Table.actionMap");
        if (actionMap == null && (actionMap = this.createThisActionMap()) != null) {
            UIManager.getLookAndFeelDefaults().put("Table.actionMap", actionMap);
        }
        return actionMap;
    }

    ActionMap createThisActionMap() {
        ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        actionMapUIResource.put("selectNextColumn", new NavigationalAction(1, 0, false, false, false));
        actionMapUIResource.put("selectPreviousColumn", new NavigationalAction(-1, 0, false, false, false));
        actionMapUIResource.put("selectNextRow", new NavigationalAction(0, 1, false, false, false));
        actionMapUIResource.put("selectPreviousRow", new NavigationalAction(0, -1, false, false, false));
        actionMapUIResource.put("selectNextColumnExtendSelection", new NavigationalAction(1, 0, false, true, false));
        actionMapUIResource.put("selectPreviousColumnExtendSelection", new NavigationalAction(-1, 0, false, true, false));
        actionMapUIResource.put("selectNextRowExtendSelection", new NavigationalAction(0, 1, false, true, false));
        actionMapUIResource.put("selectPreviousRowExtendSelection", new NavigationalAction(0, -1, false, true, false));
        actionMapUIResource.put("scrollUpChangeSelection", new PagingAction(false, false, true, false));
        actionMapUIResource.put("scrollDownChangeSelection", new PagingAction(false, true, true, false));
        actionMapUIResource.put("selectFirstColumn", new PagingAction(false, false, false, true));
        actionMapUIResource.put("selectLastColumn", new PagingAction(false, true, false, true));
        actionMapUIResource.put("scrollUpExtendSelection", new PagingAction(true, false, true, false));
        actionMapUIResource.put("scrollDownExtendSelection", new PagingAction(true, true, true, false));
        actionMapUIResource.put("selectFirstColumnExtendSelection", new PagingAction(true, false, false, true));
        actionMapUIResource.put("selectLastColumnExtendSelection", new PagingAction(true, true, false, true));
        actionMapUIResource.put("selectFirstRow", new PagingAction(false, false, true, true));
        actionMapUIResource.put("selectLastRow", new PagingAction(false, true, true, true));
        actionMapUIResource.put("selectFirstRowExtendSelection", new PagingAction(true, false, true, true));
        actionMapUIResource.put("selectLastRowExtendSelection", new PagingAction(true, true, true, true));
        actionMapUIResource.put("selectNextColumnCell", new NavigationalAction(1, 0, true, false, true));
        actionMapUIResource.put("selectPreviousColumnCell", new NavigationalAction(-1, 0, true, false, true));
        actionMapUIResource.put("selectNextRowCell", new NavigationalAction(0, 1, true, false, true));
        actionMapUIResource.put("selectPreviousRowCell", new NavigationalAction(0, -1, true, false, true));
        actionMapUIResource.put("selectAll", new SelectAllAction());
        actionMapUIResource.put("cancel", new CancelEditingAction());
        actionMapUIResource.put("startEditing", new StartEditingAction());
        actionMapUIResource.put("tabNext", new TabNextAction());
        actionMapUIResource.put("tabPrevious", new TabPreviousAction());
        actionMapUIResource.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
        actionMapUIResource.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
        actionMapUIResource.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
        if (this.table.getComponentOrientation().isLeftToRight()) {
            actionMapUIResource.put("scrollLeftChangeSelection", new PagingAction(false, false, false, false));
            actionMapUIResource.put("scrollRightChangeSelection", new PagingAction(false, true, false, false));
            actionMapUIResource.put("scrollLeftExtendSelection", new PagingAction(true, false, false, false));
            actionMapUIResource.put("scrollRightExtendSelection", new PagingAction(true, true, false, false));
        } else {
            actionMapUIResource.put("scrollLeftChangeSelection", new PagingAction(false, true, false, false));
            actionMapUIResource.put("scrollRightChangeSelection", new PagingAction(false, false, false, false));
            actionMapUIResource.put("scrollLeftExtendSelection", new PagingAction(true, true, false, false));
            actionMapUIResource.put("scrollRightExtendSelection", new PagingAction(true, false, false, false));
        }
        return actionMapUIResource;
    }

    private static class TabPreviousAction
    extends AbstractAction {
        private static final long serialVersionUID = 6582574981741021581L;

        private TabPreviousAction() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JTable jTable = (JTable)actionEvent.getSource();
            jTable.transferFocusBackward();
        }
    }

    private static class TabNextAction
    extends AbstractAction {
        private static final long serialVersionUID = -3868790363838289292L;

        private TabNextAction() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JTable jTable = (JTable)actionEvent.getSource();
            jTable.transferFocus();
        }
    }

    private static class StartEditingAction
    extends AbstractAction {
        private static final long serialVersionUID = 4291641785422799756L;

        private StartEditingAction() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JTable jTable = (JTable)actionEvent.getSource();
            if (!jTable.hasFocus()) {
                TableCellEditor tableCellEditor = jTable.getCellEditor();
                if (tableCellEditor != null && !tableCellEditor.stopCellEditing()) {
                    return;
                }
                jTable.requestFocus();
                return;
            }
            ListSelectionModel listSelectionModel = jTable.getSelectionModel();
            int n = listSelectionModel.getAnchorSelectionIndex();
            ListSelectionModel listSelectionModel2 = jTable.getColumnModel().getSelectionModel();
            int n2 = listSelectionModel2.getAnchorSelectionIndex();
            jTable.editCellAt(n, n2);
            Component component = jTable.getEditorComponent();
            if (component != null) {
                component.requestFocus();
            }
        }
    }

    private static class CancelEditingAction
    extends AbstractAction {
        private static final long serialVersionUID = -3449118993873310245L;

        private CancelEditingAction() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JTable jTable = (JTable)actionEvent.getSource();
            jTable.removeEditor();
        }
    }

    private static class SelectAllAction
    extends AbstractAction {
        private static final long serialVersionUID = -2793523430798554295L;

        private SelectAllAction() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JTable jTable = (JTable)actionEvent.getSource();
            jTable.selectAll();
        }
    }

    private static class PagingAction
    extends NavigationalAction {
        private static final long serialVersionUID = 1199054984536998883L;
        private boolean forwards;
        private boolean vertically;
        private boolean toLimit;

        private PagingAction(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
            super(0, 0, false, bl, false);
            this.forwards = bl2;
            this.vertically = bl3;
            this.toLimit = bl4;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JTable jTable = (JTable)actionEvent.getSource();
            if (this.toLimit) {
                if (this.vertically) {
                    int n = jTable.getRowCount();
                    this.dx = 0;
                    this.dy = this.forwards ? n : -n;
                } else {
                    int n = jTable.getColumnCount();
                    this.dx = this.forwards ? n : -n;
                    this.dy = 0;
                }
            } else {
                int n;
                if (!(jTable.getParent().getParent() instanceof JScrollPane)) {
                    return;
                }
                Dimension dimension = jTable.getParent().getSize();
                ListSelectionModel listSelectionModel = this.vertically ? jTable.getSelectionModel() : jTable.getColumnModel().getSelectionModel();
                int n2 = n = this.extend ? listSelectionModel.getLeadSelectionIndex() : listSelectionModel.getAnchorSelectionIndex();
                if (this.vertically) {
                    Rectangle rectangle = jTable.getCellRect(n, 0, true);
                    rectangle.y = rectangle.y + (this.forwards ? dimension.height : -dimension.height);
                    this.dx = 0;
                    int n3 = jTable.rowAtPoint(rectangle.getLocation());
                    if (n3 == -1 && this.forwards) {
                        n3 = jTable.getRowCount();
                    }
                    this.dy = n3 - n;
                } else {
                    Rectangle rectangle = jTable.getCellRect(0, n, true);
                    rectangle.x = rectangle.x + (this.forwards ? dimension.width : -dimension.width);
                    int n4 = jTable.columnAtPoint(rectangle.getLocation());
                    if (n4 == -1 && this.forwards) {
                        n4 = jTable.getColumnCount();
                    }
                    this.dx = n4 - n;
                    this.dy = 0;
                }
            }
            super.actionPerformed(actionEvent);
        }
    }

    private static class NavigationalAction
    extends AbstractAction {
        private static final long serialVersionUID = 6394247547613967826L;
        protected int dx;
        protected int dy;
        protected boolean toggle;
        protected boolean extend;
        protected boolean inSelection;
        protected int anchorRow;
        protected int anchorColumn;
        protected int leadRow;
        protected int leadColumn;

        protected NavigationalAction(int n, int n2, boolean bl, boolean bl2, boolean bl3) {
            this.dx = n;
            this.dy = n2;
            this.toggle = bl;
            this.extend = bl2;
            this.inSelection = bl3;
        }

        private int clipToRange(int n, int n2, int n3) {
            return Math.min(Math.max(n, n2), n3 - 1);
        }

        private void moveWithinTableRange(JTable jTable, int n, int n2, boolean bl) {
            if (bl) {
                this.leadRow = this.clipToRange(this.leadRow + n2, 0, jTable.getRowCount());
                this.leadColumn = this.clipToRange(this.leadColumn + n, 0, jTable.getColumnCount());
            } else {
                this.anchorRow = this.clipToRange(this.anchorRow + n2, 0, jTable.getRowCount());
                this.anchorColumn = this.clipToRange(this.anchorColumn + n, 0, jTable.getColumnCount());
            }
        }

        private int selectionSpan(ListSelectionModel listSelectionModel) {
            return listSelectionModel.getMaxSelectionIndex() - listSelectionModel.getMinSelectionIndex() + 1;
        }

        private int compare(int n, ListSelectionModel listSelectionModel) {
            return this.compare(n, listSelectionModel.getMinSelectionIndex(), listSelectionModel.getMaxSelectionIndex() + 1);
        }

        private int compare(int n, int n2, int n3) {
            return n < n2 ? -1 : (n >= n3 ? 1 : 0);
        }

        private boolean moveWithinSelectedRange(JTable jTable, int n, int n2, boolean bl) {
            int n3;
            int n4;
            int n5;
            boolean bl2;
            ListSelectionModel listSelectionModel = jTable.getSelectionModel();
            ListSelectionModel listSelectionModel2 = jTable.getColumnModel().getSelectionModel();
            int n6 = this.anchorRow + n2;
            int n7 = this.anchorColumn + n;
            int n8 = this.selectionSpan(listSelectionModel);
            boolean bl3 = bl2 = n8 * (n5 = this.selectionSpan(listSelectionModel2)) > 1;
            if (bl2) {
                n4 = this.compare(n6, listSelectionModel);
                n3 = this.compare(n7, listSelectionModel2);
            } else {
                n8 = jTable.getRowCount();
                n5 = jTable.getColumnCount();
                n4 = this.compare(n6, 0, n8);
                n3 = this.compare(n7, 0, n5);
            }
            this.anchorRow = n6 - n8 * n4;
            this.anchorColumn = n7 - n5 * n3;
            if (!bl) {
                return this.moveWithinSelectedRange(jTable, n4, n3, true);
            }
            return bl2;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JTable jTable = (JTable)actionEvent.getSource();
            ListSelectionModel listSelectionModel = jTable.getSelectionModel();
            this.anchorRow = listSelectionModel.getAnchorSelectionIndex();
            this.leadRow = listSelectionModel.getLeadSelectionIndex();
            ListSelectionModel listSelectionModel2 = jTable.getColumnModel().getSelectionModel();
            this.anchorColumn = listSelectionModel2.getAnchorSelectionIndex();
            this.leadColumn = listSelectionModel2.getLeadSelectionIndex();
            if (jTable.isEditing() && !jTable.getCellEditor().stopCellEditing()) {
                return;
            }
            if (!this.inSelection) {
                this.moveWithinTableRange(jTable, this.dx, this.dy, this.extend);
                if (!this.extend) {
                    jTable.changeSelection(this.anchorRow, this.anchorColumn, false, this.extend);
                } else {
                    jTable.changeSelection(this.leadRow, this.leadColumn, false, this.extend);
                }
            } else if (this.moveWithinSelectedRange(jTable, this.dx, this.dy, false)) {
                jTable.changeSelection(this.anchorRow, this.anchorColumn, true, true);
            } else {
                jTable.changeSelection(this.anchorRow, this.anchorColumn, false, false);
            }
        }
    }
}

