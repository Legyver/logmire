package com.legyver.logmire.ui.tabs;

import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.css.PseudoClass;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SVGControlSkin extends SkinBase<SVGControl> {
	private static final Logger logger = LogManager.getLogger(SVGControlSkin.class);

	private static final PseudoClass ARMED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("armed");

	private SVGGlyph glyph;

	public SVGControlSkin(SVGControl svgControl) {
		super(svgControl);

		try {
			glyph = SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg" + "." + "file-text-o");
			glyph.setFill(svgControl.getSvgIconPaint());
			glyph.setSize(svgControl.getSvgIconSize());

			svgControl.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				logger.debug("armed");
				pseudoClassStateChanged(ARMED_PSEUDOCLASS_STATE, true);
				svgControl.requestFocus();
			});
			svgControl.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
				logger.debug("disarmed");
				pseudoClassStateChanged(ARMED_PSEUDOCLASS_STATE, false);
				svgControl.requestFocus();
			});
			svgControl.addEventHandler(MouseEvent.ANY, e -> {
//				logger.debug("any: " + e.getEventType());
			});
		} catch (Exception exception) {

		}

		getChildren().add(glyph);
	}
}
