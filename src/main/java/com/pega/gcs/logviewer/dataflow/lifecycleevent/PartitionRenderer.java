
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.awt.Paint;

import org.jfree.chart.renderer.xy.XYBarRenderer;

public class PartitionRenderer extends XYBarRenderer {

    private static final long serialVersionUID = 7557673046403658449L;

    // private static final Log4j2Helper LOG = new Log4j2Helper(PartitionRenderer.class);

    public PartitionRenderer() {
        super();
    }

    @Override
    public Paint getItemPaint(int row, int col) {

        Paint paint = super.getItemPaint(row, col);
        // LOG.info(row + " " + col + " " + paint);
        return paint;
    }
}
