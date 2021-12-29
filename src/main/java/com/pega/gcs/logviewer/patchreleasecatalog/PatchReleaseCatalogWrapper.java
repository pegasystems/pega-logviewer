
package com.pega.gcs.logviewer.patchreleasecatalog;

import com.pega.gcs.logviewer.PluginWrapper;

public class PatchReleaseCatalogWrapper extends PluginWrapper {

    private static PatchReleaseCatalogWrapper _INSTANCE;

    private PatchReleaseCatalogWrapper() {
        super("com.pega.gcs.patchreleasecatalog.ui.PatchReleaseCatalogViewerFrame");
    }

    public static PatchReleaseCatalogWrapper getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new PatchReleaseCatalogWrapper();
        }

        return _INSTANCE;
    }

}
