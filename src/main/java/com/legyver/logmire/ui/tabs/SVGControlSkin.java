package com.legyver.logmire.ui.tabs;

import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.scene.control.SkinBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SVGControlSkin extends SkinBase<SVGControl> {
	private static final Logger logger = LogManager.getLogger(SVGControlSkin.class);

	private SVGGlyph glyph;

	public SVGControlSkin(SVGControl svgControl) {
		super(svgControl);

		try {
			glyph = SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg" + "." + svgControl.getSvgIcon());
			glyph.setFill(svgControl.getSvgIconPaint());
			glyph.setSize(svgControl.getSvgIconSize());
		} catch (Exception exception) {
			logger.error("Error loading control: " + svgControl.getSvgIcon(), exception);
		}

		getChildren().add(glyph);
	}
}
