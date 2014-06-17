/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2014 Ludwig M Brinckmann
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.rendertheme.renderinstruction;

import java.io.IOException;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.renderer.PolylineContainer;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.reader.PointOfInterest;
import org.mapsforge.map.rendertheme.RenderCallback;
import org.mapsforge.map.rendertheme.XmlUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Represents an icon along a polyline on the map.
 */
public class LineSymbol extends RenderInstruction {

	private static final float REPEAT_GAP_DEFAULT = 200f;
	private static final float REPEAT_START_DEFAULT = 30f;

	private boolean alignCenter;
	private Bitmap bitmap;
	private boolean bitmapInvalid;
	private float dy;
	private int priority;
	private final String relativePathPrefix;
	private boolean repeat;
	private float repeatGap;
	private float repeatStart;
	private boolean rotate;
	private String src;

	public LineSymbol(GraphicFactory graphicFactory, DisplayModel displayModel, String elementName,
	                         XmlPullParser pullParser, String relativePathPrefix) throws IOException, XmlPullParserException {
		super(graphicFactory, displayModel);

		this.rotate = true;
		this.relativePathPrefix = relativePathPrefix;

		extractValues(elementName, pullParser);
	}

	@Override
	public void destroy() {
		if (this.bitmap != null) {
			this.bitmap.decrementRefCount();
		}
	}

	@Override
	public void renderNode(RenderCallback renderCallback, PointOfInterest poi, Tile tile) {
		// do nothing
	}

	@Override
	public void renderWay(RenderCallback renderCallback, PolylineContainer way) {
		if (this.bitmap == null && !this.bitmapInvalid) {
			try {
				this.bitmap = createBitmap(relativePathPrefix, src);
			} catch (IOException ioException) {
				this.bitmapInvalid = true;
			}
		}
		if (this.bitmap != null) {
			renderCallback.renderWaySymbol(way, this.priority, this.bitmap, this.dy, this.alignCenter,
					this.repeat, this.repeatGap, this.repeatStart, this.rotate);
		}
	}

	@Override
	public void scaleStrokeWidth(float scaleFactor) {
		// do nothing
	}

	@Override
	public void scaleTextSize(float scaleFactor) {
		// do nothing
	}

	private void extractValues(String elementName, XmlPullParser pullParser) throws IOException, XmlPullParserException {

		this.repeatGap = REPEAT_GAP_DEFAULT * displayModel.getScaleFactor();
		this.repeatStart = REPEAT_START_DEFAULT * displayModel.getScaleFactor();

		for (int i = 0; i < pullParser.getAttributeCount(); ++i) {
			String name = pullParser.getAttributeName(i);
			String value = pullParser.getAttributeValue(i);

			if (SRC.equals(name)) {
				this.src = value;
			} else if (DY.equals(name)) {
				this.dy = Float.parseFloat(value) * displayModel.getScaleFactor();
			} else if (ALIGN_CENTER.equals(name)) {
				this.alignCenter = Boolean.parseBoolean(value);
			} else if (CAT.equals(name)) {
				this.category = value;
			} else if (PRIORITY.equals(name)) {
				this.priority = Integer.parseInt(value);
			} else if (REPEAT.equals(name)) {
				this.repeat = Boolean.parseBoolean(value);
			} else if (REPEAT_GAP.equals(name)) {
				this.repeatGap = Float.parseFloat(value) * displayModel.getScaleFactor();
			} else if (REPEAT_START.equals(name)) {
				this.repeatStart = Float.parseFloat(value) * displayModel.getScaleFactor();
			} else if (ROTATE.equals(name)) {
				this.rotate = Boolean.parseBoolean(value);
			} else {
				throw XmlUtils.createXmlPullParserException(elementName, name, value, i);
			}
		}

	}

}
