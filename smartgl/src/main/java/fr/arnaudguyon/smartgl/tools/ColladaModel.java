/*
    Copyright 2019 Mj Mendoza IV

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package fr.arnaudguyon.smartgl.tools;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import fr.arnaudguyon.smartgl.opengl.ColorList;
import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.NormalList;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;
import fr.arnaudguyon.smartgl.tools.Assert;

/**
 * Created by creek23 on 01.05.19.
 * Helper to load Collada objects and convert them to Object3D
 */

public class ColladaModel extends BaseModel {

    private static final String TAG = "ColladaModel";

    public static class Builder {
        private Context mContext;
        private int mRawResourceId;
        private boolean mOptimizeModel = true;
        private HashMap<String, Texture> mTextures = new HashMap<>();
        private float[] mColor = {1, 1, 1};

        public Builder(Context context, int rawFileResourceId) {
            mContext = context;
            mRawResourceId = rawFileResourceId;
        }
        public Builder optimize(boolean optimizeModel) {
            mOptimizeModel = optimizeModel;
            return this;
        }
        public Builder addTexture(String textureName, Texture texture) {
            mTextures.put(textureName, texture);
            return this;
        }
        public Builder setColor(@FloatRange(from=0, to=1) float red, @FloatRange(from=0, to=1) float green, @FloatRange(from=0, to=1) float blue) {
            mColor[0] = red;
            mColor[1] = green;
            mColor[2] = blue;
            return this;
        }

        public ColladaModel create() {
            ColladaModel collada = new ColladaModel();
            collada.loadObject(mContext, mRawResourceId);
            if (mOptimizeModel) {
                //wavefront.mergeStrips();
            }
            collada.mTextures = mTextures;
            collada.mColor = mColor;
            return collada;
        }
    }

    XmlPullParser xmlPullParser;
    ColladaParser colladaParser;
    private ColladaModel() {
        //
    }
    private void loadObject(Context context, int rawResId) throws RuntimeException {
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        BufferedReader reader = null;
        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            xmlPullParser = parserFactory.newPullParser();
            xmlPullParser.setFeature(XmlPullParser .FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            colladaParser = new ColladaParser(xmlPullParser);
        } catch (XmlPullParserException e) {
            Log.d("COLLADA!", "Error " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    Log.d("COLLADA!!!", "Error " + e.getMessage());
                }
            }
        }
        Log.d("@COLLADA", "done but did it load?");
        validate3D();
    }
	
    private void validate3D() {
        /*TODO: current implementation is meant for `randomshape.dae'; improve validation further with other DAEs
            ideally, any single-geometry DAE with/without Texture (UV Map) can be loaded and displayed
            FPS got boosted but currently have issues with rendering tho.
        */
        Assert.assertNotNull(colladaParser.library_geometries);
        Assert.assertNotNull(colladaParser.library_geometries.geometry);
        Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh);
        Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source);
        Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.triangles);

            for (int i = 0; i < colladaParser.library_geometries.geometry.get(0).mesh.source.size(); ++i) {
                Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source);
                Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i));
                Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array);
                Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value);
                if (colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).id.equals((colladaParser.library_geometries.geometry.get(0).id + "-positions"))) {
                    Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common);
                    Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor);
                    Log.i("COLLADA", "populating Vertex count " + colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.count + " stride " + colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride);
                    for (int j = 0; j < colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.count; j += colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride) {
                        Vertex l_vertex = new Vertex(
                                colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j),
                                colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 1),
                                colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 2)
                        );
                        mVertex.add(l_vertex);
                    }
                } else if (colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).id.equals((colladaParser.library_geometries.geometry.get(0).id + "-normals"))) {
                    Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common);
                    Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor);
                    Log.i("COLLADA", "populating Normals count " + colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.count + " stride " + colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride);
                    for (int j = 0; j < colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.count; j += colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride) {
                        Normal l_normal = new Normal(
                                colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j),
                                colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 1),
                                colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 2)
                        );
                        mNormals.add(l_normal);
                    }
                } else if (colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).id.equals((colladaParser.library_geometries.geometry.get(0).id + "-map-0"))) {
                    Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common);
                    Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor);
                    Log.i("COLLADA", "populating UVs count " + colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.count + " stride " + colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride);
                    for (int j = 0; j < colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.count; j += colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride) {
                        UV l_uv = new UV(
                                colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j),
                                1-colladaParser.library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 1)
                        );
                        mUVs.add(l_uv);
                    }
                }
            }

        for (triangleIndex = 0; triangleIndex < colladaParser.library_geometries.geometry.get(0).mesh.triangles.size(); ++triangleIndex) {
            Assert.assertNotNull(colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input);

            for (int i = 0; i < colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).count; ++i) {
                mFaces.add(new Face3D());
            }

            int INPUT_VERTEX = -1;
            int INPUT_NORMAL = -1;
            int INPUT_TEXCOORD = -1;
            for (int i = 0; i < colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.size(); ++i) {
                if (colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).semantic.equals("VERTEX")) {
                    INPUT_VERTEX = colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).offset;
                } else if (colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).semantic.equals("NORMAL")) {
                    INPUT_NORMAL = colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).offset;
                } else if (colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).semantic.equals("TEXCOORD")) {
                    INPUT_TEXCOORD = colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).offset;
                }
            }

            int INPUT_increment = INPUT_VERTEX + INPUT_NORMAL + INPUT_TEXCOORD;
            Assert.assertTrue((INPUT_increment != -3));
            if (INPUT_increment == -2) {
                INPUT_increment = 1;
            } else if (INPUT_increment == 0) {
                INPUT_increment = 2;
            } else {
                INPUT_increment = 3;
            }
            int numberOfVertex = colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.size();
            ArrayList<IndexInfo> indexInfos = new ArrayList<>(numberOfVertex);
            for (int i = 0; i < colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.size(); i += INPUT_increment) {
                Integer l_vertexIndex = null;
                Integer l_normalIndex = null;
                Integer l_uvIndex = null;
                if (INPUT_VERTEX != -1) { l_vertexIndex = colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.get(i + INPUT_VERTEX); }
                if (INPUT_NORMAL != -1) { l_normalIndex = colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.get(i + INPUT_NORMAL); }
                if (INPUT_TEXCOORD != -1) { l_uvIndex = colladaParser.library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.get(i + INPUT_TEXCOORD); }
                IndexInfo indexInfo = IndexInfo.create(l_vertexIndex, l_uvIndex, l_normalIndex);
                indexInfos.add(indexInfo);
            }
            Strip strip = new Strip("");
            for(IndexInfo indexInfo : indexInfos) {
                strip.addIndex(indexInfo);
            }
            mStrips.add(strip);
        }
    }

    private int triangleIndex = 0;

    public Object3D toObject3D() {
        final boolean hasUV = (mUVs.size() > 0);
        final boolean hasNormals = (mNormals.size() > 0);

        Object3D object3D = new Object3D();
        for(Strip strip : mStrips) {

            Face3D face3D = new Face3D();
            int nbIndex = strip.mIndexes.size(); //triangleCount * 3

            VertexList vertexList = new VertexList();
            vertexList.init(nbIndex);

            UVList uvList = null;
            ColorList colorList = null;
            if (hasUV) {
                uvList = new UVList();
                uvList.init(nbIndex);
            } else {
                colorList = new ColorList();
                colorList.init(nbIndex);
            }

            NormalList normalList = null;
            if (hasNormals) {
                normalList = new NormalList();
                normalList.init(nbIndex);
            }

            for(IndexInfo indexInfo : strip.mIndexes) {
                int vertexIndex = indexInfo.mVertexIndex;
                Vertex vertex = mVertex.get(vertexIndex);
                vertexList.add(vertex.mX, vertex.mY, vertex.mZ);

                if (hasUV) {
                    int uvIndex = indexInfo.mUVIndex;
                    UV uv = mUVs.get(uvIndex);
                    uvList.add(uv.mU, uv.mV);
                } else if (vertex.mHasColors) {
                    colorList.add(vertex.mR, vertex.mG, vertex.mB, 1);
                } else {
                    Random r = new Random();
                    mColor[0] = r.nextFloat();
                    mColor[1] = r.nextFloat();
                    mColor[2] = r.nextFloat();
                    vertex.setColors(mColor[0], mColor[1], mColor[2]);
                    colorList.add(vertex.mR, vertex.mG, vertex.mB, 1);
                }

                if (hasNormals) {
                    int normalIndex = indexInfo.mNormalIndex;
                    Normal normal = mNormals.get(normalIndex);
                    normalList.add(normal.mX, normal.mY, normal.mZ);
                }
            }
            vertexList.finalizeBuffer();
            face3D.setVertexList(vertexList);

            if (hasUV) {
                uvList.finalizeBuffer();
                face3D.setUVList(uvList);
                Assert.assertNotNull(colladaParser.library_images);
                Assert.assertNotNull(colladaParser.library_images.image);
                Assert.assertNotNull(colladaParser.library_images.image.get(0));
                Texture texture = mTextures.get(colladaParser.library_images.image.get(0).id);
                face3D.setTexture(texture);
            } else {
                colorList.finalizeBuffer();
                face3D.setColorList(colorList);
            }

            if (hasNormals) {
                normalList.finalizeBuffer();
                face3D.setNormalList(normalList);
            }

            object3D.addFace(face3D);
        }
        return object3D;
    }
}
