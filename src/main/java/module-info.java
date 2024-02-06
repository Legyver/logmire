module com.legyver.logmire {
    requires javafx.graphics;
    requires com.legyver.fenxlib.api;
    requires com.legyver.core;
    requires com.legyver.fenxlib.core;
    requires com.legyver.fenxlib.widgets.about;
    requires com.legyver.fenxlib.extensions.tuktukfx;
    requires javafx.controls;

    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires com.legyver.tuktukfx;
    requires com.legyver.fenxlib.controls.svg;
    requires fenxlib.icons.standard;

    exports com.legyver.logmire to javafx.graphics;
    exports com.legyver.logmire.config to com.fasterxml.jackson.databind, com.legyver.utils.ruffles;
    opens com.legyver.logmire.config to com.legyver.fenxlib.config.json, org.apache.commons.lang3;
    opens com.legyver.logmire;
}