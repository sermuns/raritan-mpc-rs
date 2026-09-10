/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import com.raritan.plaf.border.ButtonBorder;
import com.raritan.plaf.border.MenuBarBorder;
import com.raritan.plaf.border.MenuItemBorder;
import com.raritan.plaf.border.TextFieldBorder;
import com.raritan.plaf.border.ToolBarBorder;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class RaritanLookAndFeel
extends MetalLookAndFeel {
    private static final long serialVersionUID = -750982554607584958L;
    public static final ColorUIResource BASE_BACKGROUND = new ColorUIResource(234, 230, 211);

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    @Override
    public String getDescription() {
        return "MPC Look And Feel";
    }

    @Override
    public String getID() {
        return "Bianor";
    }

    @Override
    public String getName() {
        return "Bianor LF";
    }

    protected void putDefault(UIDefaults uIDefaults, String string) {
        try {
            String string2 = "com.raritan.plaf.Raritan" + string;
            Class<?> clazz = Class.forName(string2);
            uIDefaults.put(string, string2);
            uIDefaults.put(string2, clazz);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void initClassDefaults(UIDefaults uIDefaults) {
        super.initClassDefaults(uIDefaults);
        this.putDefault(uIDefaults, "ButtonUI");
        this.putDefault(uIDefaults, "MenuBarUI");
        this.putDefault(uIDefaults, "MenuUI");
        this.putDefault(uIDefaults, "MenuItemUI");
        this.putDefault(uIDefaults, "ScrollBarUI");
        this.putDefault(uIDefaults, "SplitPaneUI");
        this.putDefault(uIDefaults, "TableUI");
        this.putDefault(uIDefaults, "TabbedPaneUI");
        this.putDefault(uIDefaults, "ToolBarSeparatorUI");
        this.putDefault(uIDefaults, "ToolBarUI");
        this.putDefault(uIDefaults, "DesktopPaneUI");
    }

    @Override
    protected void initComponentDefaults(UIDefaults uIDefaults) {
        super.initComponentDefaults(uIDefaults);
        Object[] objectArray = new Object[]{"control", BASE_BACKGROUND, "Button.border", new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(), new BasicBorders.MarginBorder()), "Button.background", BASE_BACKGROUND, "CheckBox.background", BASE_BACKGROUND, "CheckBoxMenuItem.background", BASE_BACKGROUND, "ColorChooser.background", BASE_BACKGROUND, "ComboBox.background", BASE_BACKGROUND, "ComboBox.disabledBackground", BASE_BACKGROUND, "Desktop.background", BASE_BACKGROUND, "DesktopPane.background", BASE_BACKGROUND, "Frame.background", BASE_BACKGROUND, "InternalFrame.activeTitleBackground", new ColorUIResource(197, 195, 191), "InternalFrame.inactiveTitleBackground", BASE_BACKGROUND, "List.border", new BorderUIResource(new TextFieldBorder()), "Menu.background", BASE_BACKGROUND, "Menu.border", new BorderUIResource(new MenuItemBorder()), "Menu.selectionBackground", new ColorUIResource(197, 195, 191), "Menu.selectionForeground", new ColorUIResource(Color.BLACK), "MenuBar.background", BASE_BACKGROUND, "MenuBar.border", new BorderUIResource(new MenuBarBorder()), "MenuItem.background", BASE_BACKGROUND, "MenuItem.border", new BorderUIResource(new MenuItemBorder()), "MenuItem.selectionBackground", new ColorUIResource(197, 195, 191), "OptionPane.background", BASE_BACKGROUND, "OptionPane.errorIcon", LookAndFeel.makeIcon(MetalLookAndFeel.class, "icons/Error.gif"), "OptionPane.informationIcon", LookAndFeel.makeIcon(MetalLookAndFeel.class, "icons/Inform.gif"), "OptionPane.warningIcon", LookAndFeel.makeIcon(MetalLookAndFeel.class, "icons/Warn.gif"), "OptionPane.questionIcon", LookAndFeel.makeIcon(MetalLookAndFeel.class, "icons/Question.gif"), "Panel.background", BASE_BACKGROUND, "PasswordField.border", new BorderUIResource(new TextFieldBorder()), "PopupMenu.background", BASE_BACKGROUND, "PopupMenu.border", new BorderUIResource(new CompoundBorder(new LineBorder(new ColorUIResource(92, 86, 90), 1), new EmptyBorder(2, 0, 2, 0))), "ProgressBar.background", BASE_BACKGROUND, "ProgressBar.selectionForeground", new ColorUIResource(197, 195, 191), "RadioButton.background", BASE_BACKGROUND, "RadioButtonMenuItem.background", BASE_BACKGROUND, "ScrollBar.background", Color.WHITE, "ScrollBar.foreground", BASE_BACKGROUND, "ScrollBar.width", new Integer(20), "ScrollPane.background", BASE_BACKGROUND, "ScrollPane.border", new BorderUIResource(new EmptyBorder(0, 0, 0, 0)), "ScrollPane.viewportBorder", new BorderUIResource(new EmptyBorder(0, 0, 0, 0)), "Separator.background", BASE_BACKGROUND, "Separator.foreground", new ColorUIResource(92, 86, 90), "Slider.background", BASE_BACKGROUND, "Spinner.background", BASE_BACKGROUND, "SplitPane.background", BASE_BACKGROUND, "SplitPane.border", new EmptyBorder(1, 1, 1, 1), "SplitPaneDivider.border", new EmptyBorder(1, 1, 1, 1), "SplitPane.dividerSize", new Integer(4), "TabbedPane.background", BASE_BACKGROUND, "TabbedPane.selected", BASE_BACKGROUND, "TabbedPane.tabAreaBackground", BASE_BACKGROUND, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{"ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "PAGE_UP", "scrollUpChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "HOME", "selectFirstColumn", "END", "selectLastColumn", "shift PAGE_UP", "scrollUpExtendSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "shift HOME", "selectFirstColumnExtendSelection", "shift END", "selectLastColumnExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "ctrl HOME", "selectFirstRow", "ctrl END", "selectLastRow", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "TAB", "tabNext", "shift TAB", "tabPrevious", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ESCAPE", "cancel", "F2", "startEditing"}), "TableHeader.background", BASE_BACKGROUND, "TextField.inactiveBackground", BASE_BACKGROUND, "TextField.border", new BorderUIResource(new TextFieldBorder()), "TextArea.border", new BorderUIResource(new TextFieldBorder()), "ToggleButton.background", BASE_BACKGROUND, "ToolBar.background", BASE_BACKGROUND, "ToolBar.border", new BorderUIResource(new ToolBarBorder()), "ToolBar.separatorSize", new Dimension(18, 2), "Tree.line", new ColorUIResource(Color.BLACK), "Tree.hash", new ColorUIResource(Color.BLACK), "Viewport.background", BASE_BACKGROUND};
        uIDefaults.putDefaults(objectArray);
    }
}

