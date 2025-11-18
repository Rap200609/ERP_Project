package edu.univ.erp.ui.theme;

import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * Custom FlatLaf theme to provide a cohesive visual identity.
 */
public class UniversityThemeLaf extends FlatLightLaf {

    public static boolean setup() {
        return setup(new UniversityThemeLaf());
    }

    @Override
    public String getName() {
        return "University ERP Theme";
    }

    @Override
    public UIDefaults getDefaults() {
        UIDefaults defaults = super.getDefaults();

        defaults.put("@accentColor", new ColorUIResource(AppColors.PRIMARY));
        defaults.put("@background", new ColorUIResource(AppColors.BACKGROUND));
        defaults.put("@foreground", new ColorUIResource(AppColors.TEXT_PRIMARY));

        defaults.put("Component.arc", 18);
        defaults.put("Button.arc", 22);
        defaults.put("TextComponent.arc", 16);
        defaults.put("Component.focusWidth", 1);
        defaults.put("Component.innerFocusWidth", 0);

        defaults.put("Button.startBackground", new ColorUIResource(AppColors.PRIMARY));
        defaults.put("Button.endBackground", new ColorUIResource(AppColors.PRIMARY_LIGHT));
        defaults.put("Button.focusedBorderColor", new ColorUIResource(AppColors.PRIMARY_DARK));
        defaults.put("Button.hoverBackground", new ColorUIResource(AppColors.PRIMARY_LIGHT));
        defaults.put("Button.pressedBackground", new ColorUIResource(AppColors.PRIMARY_DARK));
        defaults.put("Button.foreground", new ColorUIResource(Color.WHITE));

        defaults.put("Panel.background", new ColorUIResource(AppColors.BACKGROUND));
        defaults.put("ScrollPane.background", new ColorUIResource(AppColors.BACKGROUND));
        defaults.put("SplitPaneDivider.draggingColor", new ColorUIResource(AppColors.PRIMARY));

        defaults.put("TableHeader.background", new ColorUIResource(AppColors.PRIMARY));
        defaults.put("TableHeader.foreground", new ColorUIResource(Color.WHITE));
        defaults.put("TableHeader.separatorColor", new ColorUIResource(AppColors.PRIMARY_LIGHT));
        defaults.put("Table.selectionBackground", new ColorUIResource(AppColors.PRIMARY_LIGHT));
        defaults.put("Table.selectionForeground", new ColorUIResource(Color.WHITE));

        defaults.put("TitlePane.background", new ColorUIResource(AppColors.PRIMARY));
        defaults.put("TitlePane.foreground", new ColorUIResource(Color.WHITE));

        return defaults;
    }
}


