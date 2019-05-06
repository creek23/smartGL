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
import java.util.Vector;

import fr.arnaudguyon.smartgl.math.Vector3D;
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

public class ColladaModel {

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

    private String xmlItem = "";

    XmlPullParser parser;
    private ColladaModel() {
        //
    }
    private void loadObject(Context context, int rawResId) throws RuntimeException {
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        BufferedReader reader = null;
        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser .FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);

            processCollada(parser);
        } catch (XmlPullParserException e) {
            Log.d("COLLADA!", "Error " + e.getMessage());
        } catch (IOException e) {
            //log the exception
            Log.d("COLLADA!!", "Error " + e.getMessage() + " " + e.getCause());
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
	
    private float[] mColor = {1,0,0};
    private ArrayList<Face3D> mFaces = new ArrayList<>();
    private ArrayList<Vertex> mVertex = new ArrayList<>();
    private ArrayList<UV> mUVs = new ArrayList<>(); //DAE's TEXCOORD (Texture Coordinate)
    private ArrayList<Normal> mNormals = new ArrayList<>();

    private void validate3D() {
        /*TODO: current implementation is meant for `randomshape.dae'; improve validation further with other DAEs
            ideally, any single-geometry DAE without Texture (UV Map) can be loaded and displayed
            Still need to implement textured DAE rendering; already confirmed that it's loading
        */
        Assert.assertNotNull(library_geometries);
        Assert.assertNotNull(library_geometries.geometry);
        Assert.assertNotNull(library_geometries.geometry.get(0).mesh);
        Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source);
        Assert.assertNotNull(library_geometries.geometry.get(0).mesh.triangles);

            for (int i = 0; i < library_geometries.geometry.get(0).mesh.source.size(); ++i) {
                Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source);
                Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i));
                Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).float_array);
                Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).float_array.value);
                if (library_geometries.geometry.get(0).mesh.source.get(i).id.equals((library_geometries.geometry.get(0).id + "-positions"))) {
                    Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).technique_common);
                    Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor);
                    Log.i("COLLADA", "populating Vertex count " + library_geometries.geometry.get(0).mesh.source.get(i).float_array.count + " stride " + library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride);
                    for (int j = 0; j < library_geometries.geometry.get(0).mesh.source.get(i).float_array.count; j += library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride) {
                        Vertex l_vertex = new Vertex(
                                library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j),
                                library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 1),
                                library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 2)
                        );
                        mVertex.add(l_vertex);
                    }
                } else if (library_geometries.geometry.get(0).mesh.source.get(i).id.equals((library_geometries.geometry.get(0).id + "-normals"))) {
                    Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).technique_common);
                    Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor);
                    Log.i("COLLADA", "populating Normals count " + library_geometries.geometry.get(0).mesh.source.get(i).float_array.count + " stride " + library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride);
                    for (int j = 0; j < library_geometries.geometry.get(0).mesh.source.get(i).float_array.count; j += library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride) {
                        Normal l_normal = new Normal(
                                library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j),
                                library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 1),
                                library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 2)
                        );
                        mNormals.add(l_normal);
                    }
                } else if (library_geometries.geometry.get(0).mesh.source.get(i).id.equals((library_geometries.geometry.get(0).id + "-map-0"))) {
                    Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).technique_common);
                    Assert.assertNotNull(library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor);
                    Log.i("COLLADA", "populating UVs count " + library_geometries.geometry.get(0).mesh.source.get(i).float_array.count + " stride " + library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride);
                    for (int j = 0; j < library_geometries.geometry.get(0).mesh.source.get(i).float_array.count; j += library_geometries.geometry.get(0).mesh.source.get(i).technique_common.accessor.stride) {
                        UV l_uv = new UV(
                                library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j),
                                1-library_geometries.geometry.get(0).mesh.source.get(i).float_array.value.get(j + 1)
                        );
                        mUVs.add(l_uv);
                    }
                }
            }
        triangleCount = 0;
        for (triangleIndex = 0; triangleIndex < library_geometries.geometry.get(0).mesh.triangles.size(); ++triangleIndex) {
            Assert.assertNotNull(library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input);

            triangleCount += library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).count;
            for (int i = 0; i < library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).count; ++i) {
                mFaces.add(new Face3D());
            }

            int INPUT_VERTEX = -1;
            int INPUT_NORMAL = -1;
            int INPUT_TEXCOORD = -1;
            for (int i = 0; i < library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.size(); ++i) {
                if (library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).semantic.equals("VERTEX")) {
                    INPUT_VERTEX = library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).offset;
                } else if (library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).semantic.equals("NORMAL")) {
                    INPUT_NORMAL = library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).offset;
                } else if (library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).semantic.equals("TEXCOORD")) {
                    INPUT_TEXCOORD = library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).input.get(i).offset;
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

            for (int i = 0; i < library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.size(); i += INPUT_increment) {
                if (INPUT_VERTEX != -1) { vertexIndex.add(library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.get(i + INPUT_VERTEX)); }
                if (INPUT_NORMAL != -1) { normalIndex.add(library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.get(i + INPUT_NORMAL)); }
                if (INPUT_TEXCOORD != -1) { uvIndex.add(library_geometries.geometry.get(0).mesh.triangles.get(triangleIndex).p.value.get(i + INPUT_TEXCOORD)); }
            }
        }
    }

    Vector<Integer> vertexIndex = new Vector<>();
    Vector<Integer> normalIndex = new Vector<>();
    Vector<Integer> uvIndex = new Vector<>();
    private int triangleCount;
    private int triangleIndex = 0;

    public Object3D toObject3D() {
        final boolean hasUV = (mUVs.size() > 0);
        final boolean hasNormals = (mNormals.size() > 0);

        Object3D object3D = new Object3D();
        for (int i = 0; i < triangleCount; ++i) {
            Face3D face3D = new Face3D();

            int nbIndex = 3;//triangleCount*3;

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

            for (int j = 0; j < 3; ++j) {
//                Log.d("COLLADA", "i " + i + "  j " + j + "  INPUT_increment " + 3 + "  ? = " + ((i*3) + j));
                Vertex vertex = mVertex.get(vertexIndex.get((i*3) + j));
                vertexList.add(vertex.mX, vertex.mY, vertex.mZ);

                if (hasUV) {
                    UV uv = mUVs.get(uvIndex.get((i*3) + j));
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
                    Normal normal = mNormals.get(normalIndex.get((i*3) + j));
                    normalList.add(normal.mX, normal.mY, normal.mZ);
                }
            }

            vertexList.finalizeBuffer();
            face3D.setVertexList(vertexList);

            if (hasUV) {
                uvList.finalizeBuffer();
                face3D.setUVList(uvList);
                Assert.assertNotNull(library_images);
                Assert.assertNotNull(library_images.image);
                Assert.assertNotNull(library_images.image.get(0));
                Texture texture = mTextures.get(library_images.image.get(0).id);
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

    private HashMap<String, Texture> mTextures = new HashMap<>();

    private class Vertex {
        float mX;
        float mY;
        float mZ;

        float mR, mG, mB;
        boolean mHasColors = false;

        Vertex(float x, float y, float z) {
            mX = x;
            mY = y;
            mZ = z;
        }

        void setColors(float r, float g, float b) {
            mHasColors = true;
            mR = r;
            mG = g;
            mB = b;
        }
    }
    private class Normal {
        float mX;
        float mY;
        float mZ;

        Normal(float x, float y, float z) {
            mX = x;
            mY = y;
            mZ = z;
        }

        Normal(Vector3D vector) {
            float[] values = vector.getArray();
            mX = values[0];
            mY = values[1];
            mZ = values[2];
        }
    }

    private class UV {
        float mU;
        float mV;
        UV(float u, float v) {
            mU = u;
            mV = v;
        }
    }

    public void processCollada(XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    xmlItem = parser.getName();
                    Log.d("COLLADA?","COLLADA " + xmlItem);
                    if ("COLLADA".equals(xmlItem)) {
                        xmlns = parser.getAttributeValue(null,"xmlns");
                        _version = parser.getAttributeValue(null,"version");
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            if ("asset".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","asset IN");
                                parseColladaAsset();
                                Log.d("COLLADA!","asset OUT");
                            } else if ("library_animations".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","library_animations IN");
                                parseColladaLibraryAnimations();
                                Log.d("COLLADA!","library_animations OUT");
                            } else if ("library_cameras".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","library_cameras IN");
                                parseColladaLibraryCameras();
                                Log.d("COLLADA!","library_cameras OUT");
                            } else if ("library_lights".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!", "library_lights " + xmlItem);
                                Log.d("COLLADA!", "      getAttributeCount " + parser.getAttributeCount());
                                Log.d("COLLADA!", "      getNamespace      " + parser.getNamespace());
                                Log.d("COLLADA!", "      getAttributeValue " + parser.getAttributeValue(null,"value"));
                                Log.d("COLLADA!", "      getText           " + parser.getText());
                                if (parser.getAttributeCount() > 0) {
                                    Log.d("COLLADA!", "      getAttributeValue " + parser.getAttributeValue(0));
                                    Log.d("COLLADA!", "      getAttributeName " + parser.getAttributeName(0));
                                }
                                //TODO: implement lights parsing
                                //light
                                //  -- technique_common
                                //       -- point
                                //            -- color
                                //            -- constant_attenuation
                                //            -- linear_attenuation
                                //            -- quadratic_attenuation
                                //  -- extra
                                //       -- technique
                                //            -- type
                                //            -- flag
                                //            -- mode
                                //            -- gamma
                                //            -- red
                                //            -- green
                                //            -- blue
                                //            -- shadow_r
                                //            -- shadow_g
                                //            -- shadow_b
                                //            -- energy
                                //            -- dist
                                //            -- spotsize
                                //            -- spotblend
                                //            -- halo_intensity
                                //            -- att1
                                //            -- att2
                                //            -- falloff_type
                                //            -- clipsta
                                //            -- clipend
                                //            -- bias
                                //            -- soft
                                //            -- compressthresh
                                //            -- bufsize
                                //            -- samp
                                //            -- buffers
                                //            -- filtertype
                                //            -- bufflag
                                //            -- buftype
                                //            -- ray_samp
                                //            -- ray_sampy
                                //            -- ray_sampz
                                //            -- ray_samp_type
                                //            -- area_shape
                                //            -- area_size
                                //            -- area_sizey
                                //            -- area_sizez
                                //            -- adapt_thresh
                                //            -- ray_samp_method
                                //            -- shadhalostep
                                //            -- sun_effect_type
                                //            -- skyblendtype
                                //            -- horizon_brightness
                                //            -- spread
                                //            -- sun_brightness
                                //            -- sun_size
                                //            -- backscattered_light
                                //            -- sun_intensity
                                //            -- atm_turbidity
                                //            -- atm_extinction_factor
                                //            -- atm_distance_factor
                                //            -- skyblendfac
                                //            -- sky_exposure
                                //            -- sky_colorspace
                            } else if ("library_images".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","library_images IN");
                                parseColladaLibraryImages();
                                Log.d("COLLADA!","library_images OUT");
                            } else if ("library_effects".equals(xmlItem) && eventType == XmlPullParser.START_TAG) { //[optional?]
                                //TODO: implement effects parsing
                                //effect
                                //  -- profile_COMMON
                                //       -- technique
                                //            -- phong
                                //                 -- emission
                                //                      -- color
                                //                 -- ambient
                                //                      -- color
                                //                 -- diffuse
                                //                      -- color
                                //                 -- specular
                                //                      -- color
                                //                 -- shininess
                                //                      -- color
                                //                 -- index_of_refraction
                                //                      -- float
                            } else if ("library_materials".equals(xmlItem) && eventType == XmlPullParser.START_TAG) { //[optional?]
                                Log.d("COLLADA!","library_materials IN");
                                parseColladaLibraryMaterials();
                                Log.d("COLLADA!","library_materials OUT");
                            } else if ("library_geometries".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","library_geometries IN");
                                parseColladaLibraryGeometries();
                                Log.d("COLLADA!","library_geometries OUT");
                            } else if ("library_controllers".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            } else if ("library_visual_scenes".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                //visual_scene
                                //  -- node (Camera)
                                //       -- matrix
                                //       -- instance_camera
                                //  -- node (Lamp)
                                //       -- matrix
                                //       -- instance_light
                                //  -- node (Cube)
                                //       -- matrix
                                //       -- instance_geometry
                                //            -- bind_material [optional]
                                //                 -- technique_common
                                //                      -- instance_material
                            } else if ("scene".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                scene = new SScene();
                                parser.next();
                                if (parser.getName() == "instance_visual_scene") {
                                    scene.instance_visual_scene_url = parser.getAttributeValue(null, "url");
                                }
                            }
                            Log.d("COLLADA","COLLADA loop " + xmlItem);
                        }
                        Log.d("COLLADA","COLLADA exit " + xmlItem);
                    }
                    break;
            }

            eventType = parser.next();
            Log.d("COLLADA","eventType " + eventType);
        }

    }

    private void parseColladaAsset() {
        //contributor
        //  -- author
        //  -- authoring_tool
        //  -- comments
        //  -- copyright
        //created
        //modified
        //unit
        //up_axis
        try {
            asset = new SAsset();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("contributor".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    asset.contributor = new SContributor();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        if ("author".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            eventType = parser.next();
                            asset.contributor.author = parser.getText();
                        } else if ("authoring_tool".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            eventType = parser.next();
                            asset.contributor.authoring_tool = parser.getText();
                        } else if ("comments".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            eventType = parser.next();
                            asset.contributor.comments = parser.getText();
                        } else if ("copyright".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            eventType = parser.next();
                            asset.contributor.copyright = parser.getText();
                        } else if ("contributor".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if ("created".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    eventType = parser.next();
                    asset.created = parser.getText();
                } else if ("modified".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    eventType = parser.next();
                    asset.modified = parser.getText();
                } else if ("unit".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    eventType = parser.next();
                    asset.unit = new SUnit();
                    asset.unit.name = parser.getAttributeValue(null,"name");
                    asset.unit.unit_value = parser.getAttributeValue(null,asset.unit.name);
                } else if ("up_axis".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    eventType = parser.next();
                    asset.up_axis = parser.getText();
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("asset".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseColladaLibraryAnimations() {
        //animation
        //  -- source
        //       -- float_array
        //       -- technique_common
        //            -- accessor
        //                 -- param
        //       -- technique
        //            -- pre_infinity
        //            -- post_infinity
        //  -- sampler
        //       -- input
        //  -- channel
        try {
            library_animations = new SLibraryAnimations();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("animation".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_animations.animation == null) { library_animations.animation = new Vector<>(); }
                    SAnimation l_animation = new SAnimation();
                        l_animation.id = parser.getAttributeValue(null,"id");
                    library_animations.animation.add(l_animation);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //  -- source
                        //  -- sampler
                        //  -- channel
                        if ("source".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","source IN");
                            parseColladaLibraryAnimationsSource();
                            Log.d("COLLADA!","source OUT");
                        } else if ("sampler".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
//                            SAnimation l_animation = new SAnimation();
                            l_animation.sampler = new SSampler();
                            l_animation.sampler.id = parser.getAttributeValue(null,"id");
                            //       -- input
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                eventType = parser.next();
                                xmlItem = parser.getName();
                                if ("input".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                    if (l_animation.sampler.input == null) { l_animation.sampler.input = new Vector<>(); }
                                    SSamplerInput l_input = new SSamplerInput();
                                        l_input.semantic = parser.getAttributeValue(null,"semantic");
                                        l_input.source = parser.getAttributeValue(null,"source");
                                    l_animation.sampler.input.add(l_input);
                                    Log.d("COLLADA!xxx", "input exit " + xmlItem);
                                } else if (xmlItem == null) {
                                    //do nothing
                                } else if ("sampler".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                    Log.d("COLLADA!f", "sampler legit exiting " + xmlItem);
                                    break;
                                }
                            }
                        } else if ("channel".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","channel IN");
                            l_animation.channel = new SChannel();
                            l_animation.channel.source = parser.getAttributeValue(null,"source");
                            l_animation.channel.target = parser.getAttributeValue(null,"target");
                            Log.d("COLLADA!","channel OUT");
                        } else if (xmlItem == null) {
                            //do nothing
                        } else if ("animation".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("library_animations".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    Log.d("COLLADA!f", "library_animations legit exiting " + xmlItem);
                    break;
                }
                Log.d("COLLADA!f", "library_animations loop " + xmlItem);
            }
            Log.d("COLLADA!g", "library_animations exit " + xmlItem);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("COLLADA", "library_animations.animation " + library_animations.animation.size());
        Log.d("COLLADA", "library_animations.animation.get(0).sources.size() " + library_animations.animation.get(0).sources.size());
/*        for (int i = 0; i < 999999999; i++){
            for (int j = 0; j < 999999999; j++) {
                Math.abs(i*j/10);
            }
        }
        Assert.assertTrue(false);*/
    }

        private void parseColladaLibraryAnimationsSource() {
            //       -- float_array
            //       -- [Name_array]
            //       -- technique_common
            //            -- accessor
            //                 -- param
            //       -- [technique]
            //            -- pre_infinity
            //            -- post_infinity
            try {
                if (library_animations.animation.get(library_animations.animation.size()-1).sources == null) {
                    library_animations.animation.get(library_animations.animation.size()-1).sources = new Vector<>();
                }
                SAnimationSource l_source = new SAnimationSource();
                    l_source.id = parser.getAttributeValue(null,"id");
                    Log.d("COLLADA", "l_source.id " + l_source.id);
                library_animations.animation.get(library_animations.animation.size()-1).sources.add(l_source);
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    eventType = parser.next();
                    xmlItem = parser.getName();
                    Log.d("COLLADA","source() xmlItem " + xmlItem + " eventType " + eventType);
                    if ("float_array".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
//                        Assert.assertNotNull(l_source.float_array);
                        l_source.float_array = new SFloatArray();
                        l_source.float_array.id = parser.getAttributeValue(null,"id");
                        l_source.float_array.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                        l_source.float_array.value = new Vector<>();
                        parser.next();
                        String[] l_float = parser.getText().split(" ");
                        for (int i = 0; i < l_float.length; ++i) {
                            if (l_float[i].isEmpty()) continue;
                            l_source.float_array.value.add(Float.parseFloat(l_float[i]));
                        }
                        Log.d("COLLADA!xxx", "float_array exit " + xmlItem);
                    } else if ("Number_array".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        l_source.Name_array = new SAnimationSourceNameArray();
                        l_source.Name_array.id = parser.getAttributeValue(null,"id");
                        l_source.Name_array.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                        l_source.Name_array.value = new Vector<>();
                        String[] l_Name = parser.getText().split(" ");
                        parser.next();
                        for (int i = 0; i < l_Name.length; ++i) {
                            if (l_Name[i].isEmpty()) continue;
                            l_source.Name_array.value.add(l_Name[i]);
                        }
                        Log.d("COLLADA!xxx", "Number_array exit " + xmlItem);
                    } else if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        l_source.technique_common = new SAnimationSourceTechniqueCommon();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- accessor
                            if ("accessor".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                l_source.technique_common.accessor = new SAnimationSourceTechniqueCommonAccessor();
                                l_source.technique_common.accessor.source = parser.getAttributeValue(null,"source");
                                l_source.technique_common.accessor.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                                l_source.technique_common.accessor.stride = Integer.parseInt(parser.getAttributeValue(null,"stride"));
                                while (eventType != XmlPullParser.END_DOCUMENT) {
                                    eventType = parser.next();
                                    xmlItem = parser.getName();
                                    //                 -- param
                                    if ("param".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                        if (l_source.technique_common.accessor.params == null) { l_source.technique_common.accessor.params = new Vector<>(); }
                                        SAnimationSourceTechniqueCommonAccessorParam l_param = new SAnimationSourceTechniqueCommonAccessorParam();
                                            l_param.name = parser.getAttributeValue(null,"name");
                                            l_param.type = parser.getAttributeValue(null,"type");
                                        l_source.technique_common.accessor.params.add(l_param);
                                    } else if (xmlItem == null) {
                                        //do nothing
                                    } else if ("accessor".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                        break;
                                    }
                                }
                                Log.d("COLLADA!xxx", "accessor exit " + xmlItem);
                            } else if (xmlItem == null) {
                                //do nothing
                            } else if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                break;
                            }
                        }
                        Log.d("COLLADA!xxx", "technique_common exit " + xmlItem);
                    } else if ("technique".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        l_source.technique = new SAnimationSourceTechnique();
                        l_source.technique.profile = parser.getAttributeValue(null,"profile");
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- pre_infinity
                            //            -- post_infinity
                            if ("pre_infinity".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                l_source.technique.pre_infinity = parser.getText();
                            } else if ("post_infinity".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                l_source.technique.post_infinity = parser.getText();
                            } else if (xmlItem == null) {
                                //do nothing
                            } else if ("technique".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                break;
                            }
                        }
                        Log.d("COLLADA!xxx", "technique exit " + xmlItem);
                    } else if (xmlItem == null) {
                        //do nothing
                    } else if ("source".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                        Log.d("COLLADA!f", "source legit exiting " + xmlItem);
                        break;
                    }
                    //Log.d("COLLADA!f", "source loop " + xmlItem);
                }
                Log.d("COLLADA!g", "source exit " + xmlItem);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void parseColladaLibraryCameras() {
        //camera
        //  -- optics
        //       -- technique_common
        //            -- perspective
        //                 -- xfov
        //                 -- aspect_ratio
        //                 -- znear
        //                 -- zfar
        //  -- extra
        //       -- technique
        //            -- shiftx
        //            -- shifty
        //            -- YF_dofdis
        try {
            library_cameras = new SLibraryCameras();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("camera".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_cameras.camera == null) { library_cameras.camera = new Vector<>(); }
                    SCamera l_camera = new SCamera();
                        l_camera.id = parser.getAttributeValue(null,"id");
                        l_camera.name = parser.getAttributeValue(null,"name");
                    library_cameras.camera.add(l_camera);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //  -- optics
                        //  -- extra
                        if ("optics".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","optics IN");
                            parseColladaLibraryCamerasCameraOptics();
                            Log.d("COLLADA!","optics OUT");
                        } else if ("extra".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","extra IN");
                            parseColladaLibraryCamerasCameraExtra();
                            Log.d("COLLADA!","extra OUT");
                        } else if ("camera".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("library_cameras".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    Log.d("COLLADA!f", "library_cameras legit exiting " + xmlItem);
                    break;
                }
                Log.d("COLLADA!f", "library_cameras loop " + xmlItem);
            }
            Log.d("COLLADA!g", "library_cameras exit " + xmlItem);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseColladaLibraryCamerasCameraOptics() {
        //       -- technique_common
        //            -- perspective
        //                 -- xfov
        //                 -- aspect_ratio
        //                 -- znear
        //                 -- zfar
        try {
            library_cameras.camera.get(library_cameras.camera.size()-1).optics = new SOptic();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common = new STechniqueCommon();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //            -- perspective
                        if ("perspective".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","perspective IN");
                            parseColladaLibraryCamerasCameraOpticsTechniqueCommonPerspective();
                            Log.d("COLLADA!","perspective OUT");
                        } else if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                    Log.d("COLLADA!xxx","technique_common exit " + xmlItem);
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("optics".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    Log.d("COLLADA!f", "optics legit exiting " + xmlItem);
                    break;
                }
                Log.d("COLLADA!f", "optics loop " + xmlItem);
            }
            Log.d("COLLADA!g", "optics exit " + xmlItem);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseColladaLibraryCamerasCameraOpticsTechniqueCommonPerspective() {
        //                 -- xfov
        //                 -- aspect_ratio
        //                 -- znear
        //                 -- zfar
        try {
            library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective = new SPerspective();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("xfov".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.xfov = new SPerspectiveParam();
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.xfov.sid = parser.getAttributeValue(null,"sid");
                    parser.next();
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.xfov.value = parser.getText();
                } else if ("aspect_ratio".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    parser.next();
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.aspect_ratio = parser.getText();
                } else if ("znear".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.znear = new SPerspectiveParam();
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.znear.sid = parser.getAttributeValue(null,"sid");
                    parser.next();
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.znear.value = parser.getText();
                } else if ("zfar".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.zfar = new SPerspectiveParam();
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.zfar.sid = parser.getAttributeValue(null,"sid");
                    parser.next();
                    library_cameras.camera.get(library_cameras.camera.size()-1).optics.technique_common.perspective.zfar.value = parser.getText();
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("perspective".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseColladaLibraryCamerasCameraExtra() {
        //       -- technique
        //            -- shiftx
        //            -- shifty
        //            -- YF_dofdis
        try {
            library_cameras.camera.get(library_cameras.camera.size()-1).extra = new SExtra();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("technique".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique = new STechnique();
                    library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.profile = parser.getAttributeValue(null,"profile");

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //            -- shiftx
                        //            -- shifty
                        //            -- YF_dofdis
                        if ("shiftx".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shiftx = new STechniqueParam();
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shiftx.sid = parser.getAttributeValue(null,"sid");
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shiftx.type = parser.getAttributeValue(null,"type");
                            parser.next();
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shiftx.value = parser.getText();
                        } else if ("shifty".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shifty = new STechniqueParam();
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shifty.sid = parser.getAttributeValue(null,"sid");
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shifty.type = parser.getAttributeValue(null,"type");
                            parser.next();
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.shifty.value = parser.getText();
                        } else if ("YF_dofdist".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.YF_dofdist = new STechniqueParam();
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.YF_dofdist.sid = parser.getAttributeValue(null,"sid");
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.YF_dofdist.type = parser.getAttributeValue(null,"type");
                            parser.next();
                            library_cameras.camera.get(library_cameras.camera.size()-1).extra.technique.YF_dofdist.value = parser.getText();
                        } else if ("technique".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("extra".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseColladaLibraryImages() {
        //image
        //  -- init_from
        try {
            library_images = new SLibraryImages();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("image".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_images.image == null) { library_images.image = new Vector<>(); }
                    SImage l_image = new SImage();
                        l_image.id = parser.getAttributeValue(null,"id");
                        l_image.name = parser.getAttributeValue(null,"name");
                    library_images.image.add(l_image);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //  -- init_from
                        if ("init_from".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_images.image.get(library_images.image.size()-1).init_from = parser.getText();
                        } else if ("image".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("library_images".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseColladaLibraryMaterials() {
        //material
        //  -- instance_effect
        try {
            library_materials = new SLibraryMaterials();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("material".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_materials.material == null) { library_materials.material = new Vector<>(); }
                    SMaterial l_material = new SMaterial();
                        l_material.id = parser.getAttributeValue(null,"id");
                        l_material.name = parser.getAttributeValue(null,"name");
                    library_materials.material.add(l_material);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //  -- mesh
                        if ("instance_effect".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_materials.material.get(library_materials.material.size()-1).instance_effect = new SInstanceEffect();
                            library_materials.material.get(library_materials.material.size()-1).instance_effect.url = parser.getAttributeValue(null,"url");
                        } else if ("material".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("library_materials".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseColladaLibraryGeometries() {
        //geometry
        //  -- mesh
        //       -- source (Cube-mesh-positions)
        //            -- float_array
        //            -- technique_common
        //                 -- accessor
        //                      -- param (X)
        //                      -- param (Y)
        //                      -- param (Z)
        //       -- source (Cube-mesh-normals)
        //            -- float_array
        //            -- technique_common
        //                 -- accessor
        //                      -- param (X)
        //                      -- param (Y)
        //                      -- param (Z)
        //       -- source (Cube-mesh-map) [optional?]
        //            -- float_array
        //            -- technique_common
        //                 -- accessor
        //                      -- param (S)
        //                      -- param (T)
        //       -- vertices
        //            -- input
        //       -- triangles
        //            -- input (VERTEX)
        //            -- input (NORMAL)
        //            -- input (TEXCOORD) [optional?]
        //            -- p
        try {
            library_geometries = new SLibraryGeometries();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("geometry".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    library_geometries.geometry = new Vector<>();
                    SGeometry l_geometry = new SGeometry();
                        l_geometry.id = parser.getAttributeValue(null,"id");
                        l_geometry.name = parser.getAttributeValue(null,"name");
                    library_geometries.geometry.add(l_geometry);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //  -- mesh
                        if ("mesh".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","mesh IN");
                            parseColladaLibraryGeometriesMesh();
                            Log.d("COLLADA!","mesh OUT");
                        } else if ("geometry".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("library_geometries".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseColladaLibraryGeometriesMesh() {
        //       -- source (Cube-mesh-positions)
        //            -- float_array
        //            -- technique_common
        //                 -- accessor
        //                      -- param (X)
        //                      -- param (Y)
        //                      -- param (Z)
        //       -- source (Cube-mesh-normals)
        //            -- float_array
        //            -- technique_common
        //                 -- accessor
        //                      -- param (X)
        //                      -- param (Y)
        //                      -- param (Z)
        //       -- source (Cube-mesh-map) [optional?]
        //            -- float_array
        //            -- technique_common
        //                 -- accessor
        //                      -- param (S)
        //                      -- param (T)
        //       -- vertices
        //            -- input
        //       -- triangles
        //            -- input (VERTEX)
        //            -- input (NORMAL)
        //            -- input (TEXCOORD) [optional?]
        //            -- p
        try {
            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh = new SMesh();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("source".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source == null) {
                        library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.source = new Vector<>();
                    }
                    SSource l_sourcce = new SSource();
                    l_sourcce.id = parser.getAttributeValue(null,"id");
                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.add(l_sourcce);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //            -- float_array
                        //            -- technique_common
                        if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","technique_common IN");
                            parseColladaLibraryGeometriesMeshTechniqueCommon();
                            Log.d("COLLADA!","technique_common OUT");
                        } else if ("float_array".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                            ).float_array = new SFloatArray();
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                            ).float_array.id = parser.getAttributeValue(null,"id");
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                            ).float_array.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                            parser.next();
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                            ).float_array.value = new Vector<>();
                            String[] l_float = parser.getText().split(" ");
                            for (int i = 0; i < l_float.length; ++i) {
                                if (l_float[i].isEmpty()) continue;
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).float_array.value.add(Float.parseFloat(l_float[i]));
                            }
                        } else if ("source".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if ("vertices".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.vertices == null) {
                        library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.vertices = new SVertices();
                    }
                    library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.vertices.id = parser.getAttributeValue(null,"id");
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //            -- float_array
                        //            -- technique_common
                        if ("input".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.vertices.input = new SVerticesInput();
                            library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.vertices.input.semantic = parser.getAttributeValue(null,"semantic");
                            library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.vertices.input.source = parser.getAttributeValue(null,"source");
                        } else if ("vertices".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if ("polylist".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    Log.e("COLLADA", "Please 'Triangulate' all quads in your model.");
                    Assert.assertTrue(false);
                } else if ("triangles".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.triangles == null) {
                        library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles = new Vector<>();
                    }
                    STriangles l_triangles = new STriangles();
                        l_triangles.material = parser.getAttributeValue(null,"material");
                        l_triangles.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                    library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.add(l_triangles);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //            -- float_array
                        //            -- technique_common
                        if ("input".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            if (library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.size()-1
                            ).input == null) {
                                library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.size() - 1
                                ).input = new Vector<>();
                            }
                            STrianglesInput l_STrianglesInput = new STrianglesInput();
                                l_STrianglesInput.semantic = parser.getAttributeValue(null,"semantic");
                                l_STrianglesInput.source = parser.getAttributeValue(null,"source");
                                l_STrianglesInput.offset = Integer.parseInt(parser.getAttributeValue(null,"offset"));
                            library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.size()-1
                            ).input.add(l_STrianglesInput);
                        } else if ("p".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.size()-1
                            ).p = new SP();
                            parser.next();
                            library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.get(
                                    library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.size()-1
                            ).p.value = new Vector<>();
                            String[] l_value = parser.getText().split(" ");
                            Log.d("COLLADA", "l_value.length " + l_value.length);
                            for (int i = 0; i < l_value.length; ++i) {
                                if (l_value[i].isEmpty()) continue;
                                library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size() - 1).mesh.triangles.size()-1
                                ).p.value.add(Integer.parseInt(l_value[i]));
                            }
                        } else if ("triangles".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("mesh".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseColladaLibraryGeometriesMeshTechniqueCommon() {
        //                 -- accessor
        //                      -- param (X)
        //                      -- param (Y)
        //                      -- param (Z)
        try {
            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
            ).technique_common = new SGeometryTechniqueCommon();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("accessor".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                    ).technique_common.accessor = new SAccessor();
                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                    ).technique_common.accessor.source = parser.getAttributeValue(null,"source");
                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                    ).technique_common.accessor.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                    library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                            library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                    ).technique_common.accessor.stride = Integer.parseInt(parser.getAttributeValue(null,"stride"));
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //                      -- param (X)
                        //                      -- param (Y)
                        //                      -- param (Z)
                        //                      ---- or ----
                        //                      -- param (S)
                        //                      -- param (Y)
                        if ("param".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            String l_name = parser.getAttributeValue(null,"name");
                            String l_type = parser.getAttributeValue(null,"type");
                            if ("X".equals(l_name)) {
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramX = new SAccessorParam();
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramX.name = l_name;
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramX.name = l_type;
                            } else if ("Y".equals(l_name)) {
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramY = new SAccessorParam();
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramY.name = l_name;
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramY.name = l_type;
                            } else if ("Z".equals(l_name)) {
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramZ = new SAccessorParam();
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramZ.name = l_name;
                                library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.get(
                                        library_geometries.geometry.get(library_geometries.geometry.size()-1).mesh.source.size()-1
                                ).technique_common.accessor.paramZ.name = l_type;
                            }
                        } else if ("accessor".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //==============================================================================================
    //==============================================================================================
    //==============================================================================================
    public String xmlns, _version, xmlns_xsi;
    public SAsset asset;
    public SLibraryAnimations library_animations;
    public SLibraryCameras library_cameras;
    public SLibraryLights library_lights;
    public SLibraryImages library_images;
    public SLibraryEffects library_effects;
    public SLibraryMaterials library_materials;
    public SLibraryGeometries library_geometries;
    public SLibraryControllers library_controllers;
    public SScene scene;

    class SAsset {
        SContributor contributor;
        String created;
        String modified;
        SUnit unit;
        String up_axis;
    }
        class SContributor {
            String author;
            String authoring_tool;
            String comments;
            String copyright;
        }
        class SUnit {
            String name;
            String unit_value;
        }
    class SLibraryAnimations {
        Vector<SAnimation> animation;
    }
        class SAnimation {
            String id;
            Vector<SAnimationSource> sources;
            SSampler sampler;
            SChannel channel;
        }
            class SAnimationSource {
                String id;
                SFloatArray float_array;
                SAnimationSourceNameArray Name_array;
                SAnimationSourceTechniqueCommon technique_common;
                SAnimationSourceTechnique technique;
            }
                class SAnimationSourceTechniqueCommon {
                    SAnimationSourceTechniqueCommonAccessor accessor;
                }
                    class SAnimationSourceTechniqueCommonAccessor {
                        String source;
                        int count;
                        int stride;
                        Vector<SAnimationSourceTechniqueCommonAccessorParam> params;
                    }
                        class SAnimationSourceTechniqueCommonAccessorParam {
                            String name;
                            String type;
                        }
                class SAnimationSourceTechnique {
                    String profile;
                    String pre_infinity;
                    String post_infinity;
                }
                class SAnimationSourceNameArray {
                    String id;
                    int count;
                    Vector<String> value;
                }
            class SSampler {
                String id;
                Vector<SSamplerInput> input;
            }
                class SSamplerInput {
                    String semantic;
                    String source;
                }
            class SChannel {
                String source;
                String target;
            }

    class SLibraryCameras {
        Vector<SCamera> camera;
    }
        class SCamera {
            String id;
            String name;
            SOptic optics;
            SExtra extra;
        }
            class SOptic {
                STechniqueCommon technique_common;
            }
                class STechniqueCommon {
                    SPerspective perspective;
                }
                    class SPerspective {
                        SPerspectiveParam xfov;
                        String aspect_ratio;
                        SPerspectiveParam znear;
                        SPerspectiveParam zfar;
                    }
                        class SPerspectiveParam {
                            String sid;
                            String value;
                        }
            class SExtra {
                STechnique technique;
            }
                class STechnique {
                    String profile;
                    STechniqueParam shiftx;
                    STechniqueParam shifty;
                    STechniqueParam YF_dofdist;
                }
                    class STechniqueParam {
                        String sid;
                        String type;
                        String value;
                    }
    class SLibraryLights {
        Vector<SLight> light;
    }
        class SLight {
            String id;
            String name;
            SLightTechniqueCommon technique_common;
            SLightExtra extra;
        }
            class SLightTechniqueCommon {
                SPoint point;
            }
                class SPoint {
                    SColor color;
                    String constant_attenuation;
                    String linear_attenuation;
                    String quadratic_attenuation;
                }
                    class SColor {
                        String sid;
                        String value;
                    }
            class SLightExtra {
                SLightTechnique technique;
            }
                class SLightTechnique {
                    String profile;
                    STechniqueParam type;
                    STechniqueParam flag;
                    STechniqueParam mode;
                    STechniqueParam gamma;
                    STechniqueParam red;
                    STechniqueParam green;
                    STechniqueParam blue;
                    STechniqueParam shadow_r;
                    STechniqueParam shadow_g;
                    STechniqueParam shadow_b;
                    STechniqueParam energy;
                    STechniqueParam dist;
                    STechniqueParam spotsize;
                    STechniqueParam spotblend;
                    STechniqueParam halo_intensity;
                    STechniqueParam att1;
                    STechniqueParam att2;
                    STechniqueParam falloff;
                    STechniqueParam clipsta;
                    STechniqueParam clipend;
                    STechniqueParam bias;
                    STechniqueParam soft;
                    STechniqueParam compressthresh;
                    STechniqueParam buffsize;
                    STechniqueParam samp;
                    STechniqueParam buffers;
                    STechniqueParam filtertype;
                    STechniqueParam bufflag;
                    STechniqueParam buftype;
                    STechniqueParam ray_samp;
                    STechniqueParam ray_sampy;
                    STechniqueParam ray_sampz;
                    STechniqueParam ray_samp_type;
                    STechniqueParam area_shape;
                    STechniqueParam area_size;
                    STechniqueParam area_sizey;
                    STechniqueParam area_sizez;
                    STechniqueParam adapt_thresh;
                    STechniqueParam ray_samp_method;
                    STechniqueParam shadhalostep;
                    STechniqueParam sun_effect_type;
                    STechniqueParam skyblendedtype;
                    STechniqueParam horizon_brightness;
                    STechniqueParam sun_rise;
                    STechniqueParam backscattered_light;
                    STechniqueParam sun_intensity;
                    STechniqueParam atm_turbidity;
                    STechniqueParam atm_extinction_factor;
                    STechniqueParam atm_distance_factor;
                    STechniqueParam skyblendfac;
                    STechniqueParam sky_exposure;
                    STechniqueParam sky_colorspace;
                }
    class SLibraryImages {
        Vector<SImage> image;
    }
        class SImage {
            String id;
            String name;
            String init_from;
            SImageExtra extra;
        }
            class SImageExtra {
                SImageTechnique technique;
            }
                class SImageTechnique {
                    String profile;
                    String dgnode_type;
                    int image_sequence;
                }
    class SLibraryEffects {
        Vector<SEffect> effect;
    }
        class SEffect {
            String id;
            SProfileCommon profile_COMMON;
        }
            class SProfileCommon {
                SEffectsTechnique technique;
            }
                class SEffectsTechnique {
                    String sid;
                    SPhong phong;
                }
                    class SPhong {
                        SPongParam emmission;
                        SPongParam ambient;
                        SPongParam diffuse;
                        SPongParam specular;
                        SPongParamFloat shininess;
                        SPongParamFloat index_of_refraction;
                    }
                        class SPongParam {
                            SPhongColor color;
                        }
                            class SPhongColor {
                                String sid;
                                float r;
                                float g;
                                float b;
                            }
                        class SPongParamFloat {
                            SPhongFloat _float;
                        }
                            class SPhongFloat {
                                String sid;
                                String value;
                            }
    class SLibraryMaterials {
        Vector<SMaterial> material;
    }
        class SMaterial {
            String id;
            String name;
            SInstanceEffect instance_effect;
        }
            class SInstanceEffect {
                String url;
            }
    class SLibraryGeometries {
        Vector<SGeometry> geometry;
    }
        class SGeometry {
            String id;
            String name;
            SMesh mesh;
        }
            class SMesh {
                Vector<SSource> source;
                SVertices vertices;
                Vector<STriangles> triangles;
            }
                class SSource {
                    String id;
                    SFloatArray float_array;
                    SGeometryTechniqueCommon technique_common;
                }
                    class SFloatArray {
                        String id;
                        int count;
                        Vector<Float> value;
                    }
                    class SGeometryTechniqueCommon {
                        SAccessor accessor;
                    }
                        class SAccessor {
                            String source;
                            int count;
                            int stride;
                            SAccessorParam paramX;
                            SAccessorParam paramY;
                            SAccessorParam paramZ;
                        }
                            class SAccessorParam {
                                String name;
                                String type;
                            }
                class SVertices {
                    String id;
                    SVerticesInput input;
                }
                    class SVerticesInput {
                        String semantic;
                        String source;
                    }
                class STriangles {
                    String material;
                    int count;
                    Vector<STrianglesInput> input;
                    SP p;
                }
                    class STrianglesInput extends SVerticesInput {
                        int offset;
                    }
                    class SP {
                        Vector<Integer> value;
                    }
    class SLibraryControllers {
        //TODO: implement later
    }
    class SLibraryVisualScenes{
        Vector<SVisualScene> visual_scene;
    }
        class SVisualScene {
            String id;
            String name;
            Vector<SNode> node;
            SVisualScene() {
                node = new Vector<>();
            }
        }
            class SNode {
                String id;
                String name;
                String type;
                SMatrix matrix;
                String instance_url; //for use of camera/light/geometry
                SInstanceGeometry instance_geometry; //for use of geometry
            }
                class SMatrix {
                    String sid;
                    String values;
                }
                class SInstanceGeometry {
                    String url;
                    String name;
                    SBindMaterial bind_material;
                }
                    class SBindMaterial {
                        SVisualSceneTechniqueCommon technique_common;
                    }
                        class SVisualSceneTechniqueCommon {
                            SInstanceMaterial instance_material;
                        }
                            class SInstanceMaterial {
                                String symbol;
                                String target;
                            }
    class SScene {
        String instance_visual_scene_url;
    }
}
