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

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import fr.arnaudguyon.smartgl.opengl.NormalList;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;
import fr.arnaudguyon.smartgl.tools.Assert;

/**
 * Created by creek23 on 18.05.19.
 * Helper to load Collada XML file for use of ColladaModel
 */
public class ColladaParser {
    XmlPullParser parser;
    private String xmlItem = "";
    public ColladaParser(XmlPullParser p_parser) throws IOException, XmlPullParserException {
        parser = p_parser;
        processCollada(parser);
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
                                Log.d("COLLADA!","library_controllers IN");
                                parseColladaLibraryControllers();
                                Log.d("COLLADA!","library_controllers OUT");
                            } else if ("library_visual_scenes".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","library_visual_scenes IN");
                                parseColladaLibraryVisualScenes();
                                Log.d("COLLADA!","library_visual_scenes OUT");
                            } else if ("scene".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                scene = new SScene();
                                parser.next();
                                if ("instance_visual_scene".equals(parser.getName())) {
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
                            parseColladaLibraryAnimationsSource(l_animation);
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
        private void parseColladaLibraryAnimationsSource(SAnimation p_animation) {
            //       -- float_array
            //       -- [Name_array]
            //       -- technique_common
            //            -- accessor
            //                 -- param
            //       -- [technique]
            //            -- pre_infinity
            //            -- post_infinity
            try {
                if (p_animation.sources == null) { p_animation.sources = new Vector<>(); }
                SAnimationSource l_source = new SAnimationSource();
                    l_source.id = parser.getAttributeValue(null,"id");
                p_animation.sources.add(l_source);
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    eventType = parser.next();
                    xmlItem = parser.getName();
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
                            parseColladaLibraryCamerasCameraOptics(l_camera);
                            Log.d("COLLADA!","optics OUT");
                        } else if ("extra".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","extra IN");
                            parseColladaLibraryCamerasCameraExtra(l_camera);
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
        private void parseColladaLibraryCamerasCameraOptics(SCamera p_camera) {
        //       -- technique_common
        //            -- perspective
        //                 -- xfov
        //                 -- aspect_ratio
        //                 -- znear
        //                 -- zfar
        try {
            p_camera.optics = new SOptic();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    p_camera.optics.technique_common = new STechniqueCommon();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //            -- perspective
                        if ("perspective".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","perspective IN");
                            parseColladaLibraryCamerasCameraOpticsTechniqueCommonPerspective(p_camera.optics.technique_common);
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
            private void parseColladaLibraryCamerasCameraOpticsTechniqueCommonPerspective(STechniqueCommon p_techniqueCommon) {
        //                 -- xfov
        //                 -- aspect_ratio
        //                 -- znear
        //                 -- zfar
        try {
            p_techniqueCommon.perspective = new SPerspective();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("xfov".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    p_techniqueCommon.perspective.xfov = new SPerspectiveParam();
                    p_techniqueCommon.perspective.xfov.sid = parser.getAttributeValue(null,"sid");
                    parser.next();
                    p_techniqueCommon.perspective.xfov.value = parser.getText();
                } else if ("aspect_ratio".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    parser.next();
                    p_techniqueCommon.perspective.aspect_ratio = parser.getText();
                } else if ("znear".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    p_techniqueCommon.perspective.znear = new SPerspectiveParam();
                    p_techniqueCommon.perspective.znear.sid = parser.getAttributeValue(null,"sid");
                    parser.next();
                    p_techniqueCommon.perspective.znear.value = parser.getText();
                } else if ("zfar".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    p_techniqueCommon.perspective.zfar = new SPerspectiveParam();
                    p_techniqueCommon.perspective.zfar.sid = parser.getAttributeValue(null,"sid");
                    parser.next();
                    p_techniqueCommon.perspective.zfar.value = parser.getText();
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
        private void parseColladaLibraryCamerasCameraExtra(SCamera p_camera) {
        //       -- technique
        //            -- shiftx
        //            -- shifty
        //            -- YF_dofdis
        try {
            p_camera.extra = new SExtra();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("technique".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    p_camera.extra.technique = new STechnique();
                    p_camera.extra.technique.profile = parser.getAttributeValue(null,"profile");

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //            -- shiftx
                        //            -- shifty
                        //            -- YF_dofdis
                        if ("shiftx".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            p_camera.extra.technique.shiftx = new STechniqueParam();
                            p_camera.extra.technique.shiftx.sid = parser.getAttributeValue(null,"sid");
                            p_camera.extra.technique.shiftx.type = parser.getAttributeValue(null,"type");
                            parser.next();
                            p_camera.extra.technique.shiftx.value = parser.getText();
                        } else if ("shifty".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            p_camera.extra.technique.shifty = new STechniqueParam();
                            p_camera.extra.technique.shifty.sid = parser.getAttributeValue(null,"sid");
                            p_camera.extra.technique.shifty.type = parser.getAttributeValue(null,"type");
                            parser.next();
                            p_camera.extra.technique.shifty.value = parser.getText();
                        } else if ("YF_dofdist".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            p_camera.extra.technique.YF_dofdist = new STechniqueParam();
                            p_camera.extra.technique.YF_dofdist.sid = parser.getAttributeValue(null,"sid");
                            p_camera.extra.technique.YF_dofdist.type = parser.getAttributeValue(null,"type");
                            parser.next();
                            p_camera.extra.technique.YF_dofdist.value = parser.getText();
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
                            l_image.init_from = parser.getText();
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
                            l_material.instance_effect = new SInstanceEffect();
                            l_material.instance_effect.url = parser.getAttributeValue(null,"url");
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
                    if (library_geometries.geometry ==null) { library_geometries.geometry = new Vector<>(); }
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
                            parseColladaLibraryGeometriesMesh(l_geometry);
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
        private void parseColladaLibraryGeometriesMesh(SGeometry p_geometry) {
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
                p_geometry.mesh = new SMesh();
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    eventType = parser.next();
                    xmlItem = parser.getName();
                    if ("source".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        if (p_geometry.mesh.source == null) { p_geometry.mesh.source = new Vector<>(); }
                        SSource l_source = new SSource();
                        l_source.id = parser.getAttributeValue(null,"id");
                        p_geometry.mesh.source.add(l_source);
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- float_array
                            //            -- technique_common
                            if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","technique_common IN");
                                parseColladaLibraryGeometriesMeshTechniqueCommon(l_source);
                                Log.d("COLLADA!","technique_common OUT");
                            } else if ("float_array".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                l_source.float_array = new SFloatArray();
                                l_source.float_array.id = parser.getAttributeValue(null,"id");
                                l_source.float_array.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                                parser.next();
                                l_source.float_array.value = new Vector<>();
                                String[] l_float = parser.getText().split(" ");
                                for (int i = 0; i < l_float.length; ++i) {
                                    if (l_float[i].isEmpty()) continue;
                                    l_source.float_array.value.add(Float.parseFloat(l_float[i]));
                                }
                            } else if ("source".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                break;
                            }
                        }
                    } else if ("vertices".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        if (p_geometry.mesh.vertices == null) { p_geometry.mesh.vertices = new SVertices(); }
                        p_geometry.mesh.vertices.id = parser.getAttributeValue(null,"id");
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- float_array
                            //            -- technique_common
                            if ("input".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_geometry.mesh.vertices.input = new SVerticesInput();
                                p_geometry.mesh.vertices.input.semantic = parser.getAttributeValue(null,"semantic");
                                p_geometry.mesh.vertices.input.source = parser.getAttributeValue(null,"source");
                            } else if ("vertices".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                break;
                            }
                        }
                    } else if ("polylist".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        Log.e("COLLADA", "Please 'Triangulate' all quads in your model.");
                        Assert.assertTrue(false);
                    } else if ("triangles".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        if (p_geometry.mesh.triangles == null) { p_geometry.mesh.triangles = new Vector<>(); }
                        STriangles l_triangles = new STriangles();
                            l_triangles.material = parser.getAttributeValue(null,"material");
                            l_triangles.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                        p_geometry.mesh.triangles.add(l_triangles);
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- float_array
                            //            -- technique_common
                            if ("input".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                if (l_triangles.input == null) { l_triangles.input = new Vector<>(); }
                                STrianglesInput l_STrianglesInput = new STrianglesInput();
                                    l_STrianglesInput.semantic = parser.getAttributeValue(null,"semantic");
                                    l_STrianglesInput.source = parser.getAttributeValue(null,"source");
                                    l_STrianglesInput.offset = Integer.parseInt(parser.getAttributeValue(null,"offset"));
                                l_triangles.input.add(l_STrianglesInput);
                            } else if ("p".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                l_triangles.p = new SP();
                                parser.next();
                                l_triangles.p.value = new Vector<>();
                                String[] l_value = parser.getText().split(" ");
                                Log.d("COLLADA", "l_value.length " + l_value.length);
                                for (int i = 0; i < l_value.length; ++i) {
                                    if (l_value[i].isEmpty()) continue;
                                    l_triangles.p.value.add(Integer.parseInt(l_value[i]));
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
            private void parseColladaLibraryGeometriesMeshTechniqueCommon(SSource p_source) {
                //                 -- accessor
                //                      -- param (X)
                //                      -- param (Y)
                //                      -- param (Z)
                try {
                    p_source.technique_common = new SGeometryTechniqueCommon();
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        if ("accessor".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            p_source.technique_common.accessor = new SAccessor();
                            p_source.technique_common.accessor.source = parser.getAttributeValue(null,"source");
                            p_source.technique_common.accessor.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                            p_source.technique_common.accessor.stride = Integer.parseInt(parser.getAttributeValue(null,"stride"));
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
                                        p_source.technique_common.accessor.paramX = new SAccessorParam();
                                        p_source.technique_common.accessor.paramX.name = l_name;
                                        p_source.technique_common.accessor.paramX.type = l_type;
                                    } else if ("Y".equals(l_name)) {
                                        p_source.technique_common.accessor.paramY = new SAccessorParam();
                                        p_source.technique_common.accessor.paramY.name = l_name;
                                        p_source.technique_common.accessor.paramY.type = l_type;
                                    } else if ("Z".equals(l_name)) {
                                        p_source.technique_common.accessor.paramZ = new SAccessorParam();
                                        p_source.technique_common.accessor.paramZ.name = l_name;
                                        p_source.technique_common.accessor.paramZ.type = l_type;
                                    } else if ("S".equals(l_name)) {
                                        p_source.technique_common.accessor.paramX = new SAccessorParam(); //reuse of S
                                        p_source.technique_common.accessor.paramX.name = l_name;
                                        p_source.technique_common.accessor.paramX.type = l_type;
                                    } else if ("T".equals(l_name)) {
                                        p_source.technique_common.accessor.paramY = new SAccessorParam(); //reuse of T
                                        p_source.technique_common.accessor.paramY.name = l_name;
                                        p_source.technique_common.accessor.paramY.type = l_type;
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

    private void parseColladaLibraryControllers() {
        //controller[]
        //  -- skin
        //       -- bind_shape_matrix
        //       -- source[]
        //            -- Name_array |or| float_array
        //            -- technique_common
        //                 -- accessor
        //                      -- param
        //       -- joints
        //            -- input[]
        //       -- vertex_weights
        //            -- input[]
        //            -- vcount
        //            -- v
        try {
            library_controllers = new SLibraryControllers();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("controller".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_controllers.controller == null) { library_controllers.controller = new Vector<>(); }
                    SController l_controller = new SController();
                    l_controller.id = parser.getAttributeValue(null,"id");
                    l_controller.name = parser.getAttributeValue(null,"name");
                    library_controllers.controller.add(l_controller);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //  -- skin
                        if ("skin".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","skin IN");
                            parseColladaLibraryControllersSkin(l_controller);
                            Log.d("COLLADA!","skin OUT");
                        } else if ("controller".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("library_controllers".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        private void parseColladaLibraryControllersSkin(SController p_controller) {
            //       -- bind_shape_matrix
            //       -- source[]
            //            -- Name_array |or| float_array
            //            -- technique_common
            //                 -- accessor
            //                      -- param
            //       -- joints
            //            -- input[]
            //       -- vertex_weights
            //            -- input[]
            //            -- vcount
            //            -- v
            try {
                p_controller.skin = new SSkin();
                p_controller.skin.source = parser.getAttributeValue(null,"source");
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    eventType = parser.next();
                    xmlItem = parser.getName();
                    if ("bind_shape_matrix".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        p_controller.skin.bind_shape_matrix = new SBindShapeMatrix();
                        p_controller.skin.bind_shape_matrix.value = parser.getText();
                    } else if ("source".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        if (p_controller.skin.sources == null) { p_controller.skin.sources = new Vector<>(); }
                        SControllerSource l_controllerSource = new SControllerSource();
                        l_controllerSource.id = parser.getAttributeValue(null,"id");
                        p_controller.skin.sources.add(l_controllerSource);
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- Name_array |or| float_array
                            //            -- technique_common
                            if ("Name_array".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                l_controllerSource.Name_array_id = parser.getAttributeValue(null,"id");
                                l_controllerSource.Name_array_count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                                l_controllerSource.Name_array_value = parser.getText();
                            } else if ("float_array".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                l_controllerSource.float_array_id = parser.getAttributeValue(null,"id");
                                l_controllerSource.float_array_count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                                l_controllerSource.float_array_value = parser.getText();
                            } else if ("technique_common".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                Log.d("COLLADA!","technique_common IN");
                                parseColladaLibraryControllersSkinTechniqueCommon(l_controllerSource);
                                Log.d("COLLADA!","technique_common OUT");
                            } else if ("source".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                break;
                            }
                        }
                    } else if ("joints".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        library_controllers.controller.get(library_controllers.controller.size()-1).skin.joints = new SJoints();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- input[]
                            if ("input".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                if (library_controllers.controller.get(library_controllers.controller.size()-1).skin.joints.input == null) {
                                    library_controllers.controller.get(library_controllers.controller.size()-1).skin.joints.input = new Vector<>();
                                }
                                SJointsInput l_jointsInput = new SJointsInput();
                                l_jointsInput.semantic = parser.getAttributeValue(null,"semantic");
                                l_jointsInput.source = parser.getAttributeValue(null,"source");
                                library_controllers.controller.get(library_controllers.controller.size()-1).skin.joints.input.add(l_jointsInput);
                            } else if ("joints".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                break;
                            }
                        }
                    } else if ("vertex_weights".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        p_controller.skin.vertex_weights = new SVertexWeights();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            //            -- input[]
                            //            -- vcount
                            //            -- v
                            if ("input".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                if (p_controller.skin.vertex_weights.input == null) { p_controller.skin.vertex_weights.input = new Vector<>(); }
                                SVertexWeightsInput l_vertexWeightsInput = new SVertexWeightsInput();
                                l_vertexWeightsInput.semantic = parser.getAttributeValue(null,"semantic");
                                l_vertexWeightsInput.source = parser.getAttributeValue(null,"source");
                                l_vertexWeightsInput.offset = Integer.parseInt(parser.getAttributeValue(null,"offset"));
                                p_controller.skin.vertex_weights.input.add(l_vertexWeightsInput);
                            } else if ("vcount".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_controller.skin.vertex_weights.vcount  = parser.getText();
                            } else if ("v".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_controller.skin.vertex_weights.v = parser.getText();
                            } else if ("vertex_weights".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                break;
                            }
                        }
                    } else if (xmlItem == null) {
                        //do nothing
                    } else if ("skin".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                        break;
                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            private void parseColladaLibraryControllersSkinTechniqueCommon(SControllerSource p_sources) {
                //                 -- accessor
                //                      -- param
                try {
                    p_sources.technique_common = new SControllerSourceTechniqueCommon();
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        if ("accessor".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            p_sources.technique_common.accessor = new SControllerSourceTechniqueCommonAccessor();
                            p_sources.technique_common.accessor.source = parser.getAttributeValue(null,"source");
                            p_sources.technique_common.accessor.count = Integer.parseInt(parser.getAttributeValue(null,"count"));
                            p_sources.technique_common.accessor.stride = Integer.parseInt(parser.getAttributeValue(null,"stride"));
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                eventType = parser.next();
                                xmlItem = parser.getName();
                                //  -- param
                                if ("param".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                    p_sources.technique_common.accessor.param_name = parser.getAttributeValue(null,"name");
                                    p_sources.technique_common.accessor.param_type = parser.getAttributeValue(null,"type");
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

    private void parseColladaLibraryVisualScenes() {
        //visual_scene
        //  -- node (Camera)
        //       -- matrix
        //       -- instance_camera
        //  -- node (Lamp)
        //       -- matrix
        //       -- instance_light
        //  -- node (Cube)
        //       -- matrix
        //       -- instance_controller (or instance_geometry?)
        //            -- bind_material [optional]
        //                 -- technique_common
        //                      -- instance_material[]
        try {
            library_visual_scenes = new SLibraryVisualScenes();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                xmlItem = parser.getName();
                if ("visual_scene".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                    if (library_visual_scenes.visual_scene == null) { library_visual_scenes.visual_scene = new Vector<>(); }
                    SVisualScene l_visualScene = new SVisualScene();
                        l_visualScene.id = parser.getAttributeValue(null,"id");
                        l_visualScene.name = parser.getAttributeValue(null,"name");
                    library_visual_scenes.visual_scene.add(l_visualScene);
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        //  -- node
                        if ("node".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            Log.d("COLLADA!","node IN");
                            parseColladaLibraryVisualScenesNode(l_visualScene);
                            Log.d("COLLADA!","node OUT");
                        } else if ("visual_scene".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } else if (xmlItem == null) {
                    //do nothing
                } else if ("library_visual_scenes".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        private void parseColladaLibraryVisualScenesNode(SVisualScene p_visualScene) {
            //  -- node (Camera)
            //       -- matrix
            //       -- instance_camera
            //  -- node (Lamp)
            //       -- matrix
            //       -- instance_light
            //  -- node (Cube)
            //       -- matrix
            //       -- instance_controller (or instance_geometry?)
            //            -- bind_material [optional]
            //                 -- technique_common
            //                      -- instance_material[]
            try {
                if (p_visualScene.node == null) { p_visualScene.node = new Vector<>(); }
                SNode l_node = new SNode();
                    l_node.id = parser.getAttributeValue(null,"id");
                    l_node.name = parser.getAttributeValue(null,"name");
                    l_node.type = parser.getAttributeValue(null,"type");
                    Log.d("COLLADA?", "id " + l_node.id + " name " + l_node.name + " type " + l_node.type);
                p_visualScene.node.add(l_node);
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    eventType = parser.next();
                    xmlItem = parser.getName();
                    if ("translate".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        l_node.translate = new STranslate();
                        l_node.translate.sid = parser.getAttributeValue(null, "sid");
                        l_node.translate.values = parser.getText();
                    } else if ("rotate".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        if (l_node.rotate == null) { l_node.rotate = new Vector<>(); }
                        SRotate l_rotate = new SRotate();
                            l_rotate.sid = parser.getAttributeValue(null,"sid");
                            l_rotate.values = parser.getText();
                        l_node.rotate.add(l_rotate);
                    } else if ("instance_camera".equals(xmlItem) && eventType == XmlPullParser.START_TAG
                                || "instance_light".equals(xmlItem) && eventType == XmlPullParser.START_TAG
                                || "instance_geometry".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        l_node.instance_url = parser.getAttributeValue(null,"url");
                    } else if ("node".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                        Log.d("COLLADA!","node-inception IN");
                        l_node.node = new SVisualSceneNode();
                        parseColladaLibraryVisualScenesNodeInception(l_node.node);
                        Log.d("COLLADA!","node-inception OUT");
                    } else if (xmlItem == null) {
                        //do nothing
                    } else if ("node".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                        break;
                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            private void parseColladaLibraryVisualScenesNodeInception(SVisualSceneNode p_node) {
                //       -- matrix
                //       -- node[]
                //       -- extra
                try {
//                    p_node = new SVisualSceneNode();
                    p_node.id = parser.getAttributeValue(null,"id");
                    p_node.name = parser.getAttributeValue(null,"name");
                    p_node.sid = parser.getAttributeValue(null,"sid");
                    p_node.type = parser.getAttributeValue(null,"type");
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = parser.next();
                        xmlItem = parser.getName();
                        if ("matrix".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            p_node.matrix = new SMatrix();
                            p_node.matrix.sid = parser.getAttributeValue(null, "sid");
                            p_node.matrix.values = parser.getText();
                        } else if ("extra".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            p_node.extra = new SVisualSceneNodeExtra();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                eventType = parser.next();
                                xmlItem = parser.getName();
                                //  -- node
                                if ("technique".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                    parseColladaLibraryVisualScenesNodeInceptionTechnique(p_node.extra);
                                } else if ("extra".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                                    break;
                                }
                            }
                        } else if ("node".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                            if (p_node.node == null) { p_node.node = new Vector<>(); }
                            SVisualSceneNode l_node = new SVisualSceneNode();
                            p_node.node.add(l_node);
                            Log.d("COLLADA!","  node-inception IN");
                                parseColladaLibraryVisualScenesNodeInception(l_node);
                            Log.d("COLLADA!","  node-inception OUT");
                        } else if (xmlItem == null) {
                            //do nothing
                        } else if ("node".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
                            break;
                        }
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                private void parseColladaLibraryVisualScenesNodeInceptionTechnique(SVisualSceneNodeExtra p_extra) {
                    //       -- layer
                    //       -- roll
                    //       -- tip_x
                    //       -- tip_y
                    //       -- tip_z
                    try {
                        p_extra.technique = new SVisualSceneNodeExtraTechnique();
                        p_extra.technique.profile = parser.getAttributeValue(null,"profile");
                        int eventType = parser.getEventType();
                        String l_tmp = "";
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            eventType = parser.next();
                            xmlItem = parser.getName();
                            if ("layer".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_extra.technique.layer_sid = parser.getAttributeValue(null, "sid");
                                p_extra.technique.layer_type = parser.getAttributeValue(null, "type");
                                l_tmp = parser.getText();
                                if (l_tmp == null) { continue; }
                                p_extra.technique.layer_value = Integer.parseInt(l_tmp);
                            } else if ("roll".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_extra.technique.roll_sid = parser.getAttributeValue(null, "sid");
                                p_extra.technique.roll_type = parser.getAttributeValue(null, "type");
                                if (l_tmp == null) { continue; }
                                p_extra.technique.roll_value = Float.parseFloat(l_tmp);
                            } else if ("tip_x".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_extra.technique.tip_x_sid = parser.getAttributeValue(null, "sid");
                                p_extra.technique.tip_x_type = parser.getAttributeValue(null, "type");
                                if (l_tmp == null) { continue; }
                                p_extra.technique.tip_x_value = Float.parseFloat(l_tmp);
                            } else if ("tip_y".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_extra.technique.tip_y_sid = parser.getAttributeValue(null, "sid");
                                p_extra.technique.tip_y_type = parser.getAttributeValue(null, "type");
                                if (l_tmp == null) { continue; }
                                p_extra.technique.tip_y_value = Float.parseFloat(l_tmp);
                            } else if ("tip_z".equals(xmlItem) && eventType == XmlPullParser.START_TAG) {
                                p_extra.technique.tip_z_sid = parser.getAttributeValue(null, "sid");
                                p_extra.technique.tip_z_type = parser.getAttributeValue(null, "type");
                                if (l_tmp == null) { continue; }
                                p_extra.technique.tip_z_value = Float.parseFloat(l_tmp);
                            } else if (xmlItem == null) {
                                //do nothing
                            } else if ("technique".equals(xmlItem) && eventType == XmlPullParser.END_TAG) {
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
    public SLibraryVisualScenes library_visual_scenes;
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
        Vector<SController> controller;
    }
        class SController {
            String id;
            String name;
            SSkin skin;
        }
            class SSkin {
                String source;
                SBindShapeMatrix bind_shape_matrix;
                Vector<SControllerSource> sources;
                SJoints joints;
                SVertexWeights vertex_weights;
            }
                class SBindShapeMatrix {
                    String value;
                }
                class SControllerSource {
                    String id;
                    String Name_array_id;
                    int Name_array_count;
                    String Name_array_value;
                    String float_array_id;
                    int float_array_count;
                    String float_array_value;
                    SControllerSourceTechniqueCommon technique_common;
                }
                    class SControllerSourceTechniqueCommon {
                        SControllerSourceTechniqueCommonAccessor accessor;
                    }
                        class SControllerSourceTechniqueCommonAccessor  {
                            String source;
                            int count;
                            int stride;
                            String param_name;
                            String param_type;
                        }
                class SJoints {
                    Vector<SJointsInput> input;
                }
                    class SJointsInput {
                        String semantic;
                        String source;
                    }
                class SVertexWeights {
                    int count;
                    Vector<SVertexWeightsInput> input;
                    String vcount;
                    String v;
                }
                    class SVertexWeightsInput {
                        String semantic;
                        String source;
                        int offset;
                    }
    class SLibraryVisualScenes {
        Vector<SVisualScene> visual_scene;
    }
        class SVisualScene {
            String id;
            String name;
            Vector<SNode> node;
        }
            class SNode {
                String id;
                String name;
                String type;
                STranslate translate;
                Vector<SRotate> rotate;
                SScale scale;
                String instance_url; //for use of camera/light/geometry
//                SInstanceGeometry instance_geometry; //for use of geometry
                SInstanceController instance_controller; //the skeleton thingy
                SVisualSceneNode node;
            }
                class SInstanceController {
                    String skeleton_value;
                    SBindMaterial bind_material;
                }
                class SVisualSceneNode {
                    String id;
                    String name;
                    String sid;
                    String type;
                    SMatrix matrix;
                    Vector<SVisualSceneNode> node; //TODO: make sure recursive parsing is handled correctly
                    SVisualSceneNodeExtra extra;
                }
                    class SVisualSceneNodeExtra {
                        SVisualSceneNodeExtraTechnique technique;
                    }
                        class SVisualSceneNodeExtraTechnique {
                            String profile;
                            String layer_sid;
                            String layer_type;
                            int layer_value;
                            String roll_sid;
                            String roll_type;
                            float roll_value;
                            String tip_x_sid;
                            String tip_x_type;
                            float tip_x_value;
                            String tip_y_sid;
                            String tip_y_type;
                            float tip_y_value;
                            String tip_z_sid;
                            String tip_z_type;
                            float tip_z_value;
                        }
                class STranslate {
                    String sid;
                    String values;
                }
                class SRotate {
                    String sid;
                    String values;
                }
                class SScale {
                    String sid;
                    String values;
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
                            Vector<SInstanceMaterial> instance_material;
                        }
                            class SInstanceMaterial {
                                String symbol;
                                String target;
                            }
    class SScene {
        String instance_visual_scene_url;
    }

    public String generateXML(Object3D object3D) {
        String l_xml = "\n";
        l_xml += "  <library_geometries>\n";
        SGeometry l_geometry = library_geometries.geometry.get(0);
        l_xml += "    <geometry id=\"" + l_geometry.id + "\" name=\"" + l_geometry.name + "\">\n";
        l_xml += "      <mesh>\n";

        ArrayList<Integer> l_p_new_indices_position = new ArrayList<>();
        ArrayList<Integer> l_p_new_indices_normal = new ArrayList<>();
        ArrayList<Integer> l_p_new_indices_map = new ArrayList<>();
        VertexList vertexList;
        NormalList normalList;
        UVList uvList;

        Log.d("COLLADA", "mesh size " + l_geometry.mesh.source.size());
        for (int i = 0; i < l_geometry.mesh.source.size(); ++i) {
            Log.d("COLLADA", "Faces size " + object3D.getFaces().size());
            for (Face3D face3D : object3D.getFaces()) {
                vertexList = face3D.getVertexList();
                normalList = face3D.getNormalList();
                uvList = face3D.getUVList();
                SSource l_source = l_geometry.mesh.source.get(i);
                float[] float_array;
//                Log.d("COLLADA", "TAG l_source.id[" + i + "] " + l_source.id + " size: " + l_source.float_array.count + " ID:" + l_geometry.id);
                if (l_source.id.equals((l_geometry.id + "-map-0"))) {
                    float_array = uvList.getInternalBuffer();
                } else if (l_source.id.equals((l_geometry.id + "-positions"))) {
                    float_array = vertexList.getInternalBuffer();
                } else if (l_source.id.equals((l_geometry.id + "-normals"))) {
                    float_array = normalList.getInternalBuffer();
                } else {
                    Log.d("COLLADA", "here!!! l_source.id[" + i + "] " + l_source.id + " size: " + l_source.float_array.count + " ID:" + l_geometry.id);
                    continue;
                }
                Log.d("COLLADA", "l_source.id[" + i + "] " + l_source.id + " size: " + l_source.float_array.count);
                for (int k = 0; k < float_array.length; k += l_source.technique_common.accessor.stride) {
                    if (l_source.id.equals((l_geometry.id + "-map-0"))) {
                        for (int j = 0; j < l_source.float_array.count; j += l_source.technique_common.accessor.stride) {
                            if (float_array[k] == l_source.float_array.value.get(j)
                                    && (1-float_array[k + 1]) == l_source.float_array.value.get(j + 1)
                                || float_array[k] == l_source.float_array.value.get(j)
                                    && float_array[k + 1] == (1-(l_source.float_array.value.get(j + 1)))) {
                                l_p_new_indices_map.add(j/l_source.technique_common.accessor.stride);
                                break;
                            }
                        }
                    } else if (l_source.id.equals((l_geometry.id + "-positions"))) {
                        for (int j = 0; j < l_source.float_array.count; j += l_source.technique_common.accessor.stride) {
                            if (float_array[k] == l_source.float_array.value.get(j)
                                    && float_array[k + 1] == l_source.float_array.value.get(j + 1)
                                    && float_array[k + 2] == l_source.float_array.value.get(j + 2)) {
                                l_p_new_indices_position.add(j/l_source.technique_common.accessor.stride);
                                break;
                            }
                        }
                    } else if (l_source.id.equals((l_geometry.id + "-normals"))) {
                        for (int j = 0; j < l_source.float_array.count; j += l_source.technique_common.accessor.stride) {
                            if (float_array[k] == l_source.float_array.value.get(j)
                                    && float_array[k + 1] == l_source.float_array.value.get(j + 1)
                                    && float_array[k + 2] == l_source.float_array.value.get(j + 2)) {
                                l_p_new_indices_normal.add(j/l_source.technique_common.accessor.stride);
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < l_geometry.mesh.source.size(); ++i) {
            SSource l_source = l_geometry.mesh.source.get(i);
            l_xml += "        <source id=\"" + l_source.id + "\">\n";
            String l_float_array = "";/*
            for (int j = 0; j < l_source.float_array.count; j += l_source.technique_common.accessor.stride) {
                if (l_source.id.equals((l_geometry.id + "-map-0"))) {
                    l_float_array += l_source.float_array.value.get(j) + " " + l_source.float_array.value.get(j + 1) + " ";
                } else {
                    l_float_array += l_source.float_array.value.get(j) + " " + l_source.float_array.value.get(j + 1) + " " + l_source.float_array.value.get(j + 2) + " ";
                }
            }*/
            l_xml += "          <float_array id=\"" + l_source.float_array.id + "\" count=\"" + l_source.float_array.value.size() + "\">" + l_float_array + "</float_array>\n";
            l_xml += "          <technique_common>\n";
            SAccessor l_accessor = l_source.technique_common.accessor;
            l_xml += "            <accessor source=\"" + l_accessor.source + "\" count=\"" + l_accessor.count + "\" stride=\"" + l_accessor.stride + "\">\n";
            l_xml += "              <param name=\"" + l_accessor.paramX.name + "\" type=\"" + l_accessor.paramX.type + "\"/>\n";
            l_xml += "              <param name=\"" + l_accessor.paramY.name + "\" type=\"" + l_accessor.paramY.type + "\"/>\n";
            if (!l_source.id.equals((l_geometry.id + "-map-0"))) {
                l_xml += "              <param name=\"" + l_accessor.paramZ.name + "\" type=\"" + l_accessor.paramZ.type + "\"/>\n";
            }
            l_xml += "            </accessor>\n";
            l_xml += "          </technique_common>\n";
            l_xml += "        </source>\n";
        }
        l_xml += "        <vertices id=\"" + l_geometry.mesh.vertices.id + "\">\n";
        l_xml += "          <input semantic=\"" + l_geometry.mesh.vertices.input.semantic + "\" source=\"" + l_geometry.mesh.vertices.input.source + "\"/>\n";
        l_xml += "        </vertices>\n";
        for (int i = 0; i < l_geometry.mesh.triangles.size(); ++i) {
            STriangles l_triangle = l_geometry.mesh.triangles.get(i);
            l_xml += "        <triangles material=\"" + l_triangle.material + "\" count=\"" + l_triangle.count + "\">\n";
            int INPUT_VERTEX = -1;
            int INPUT_NORMAL = -1;
            int INPUT_TEXCOORD = -1;
            for (int j = 0; j < l_triangle.input.size(); ++j) {
                if (l_triangle.input.get(j).semantic.equals("VERTEX")) {
                    INPUT_VERTEX = l_triangle.input.get(j).offset;
                } else if (l_triangle.input.get(j).semantic.equals("NORMAL")) {
                    INPUT_NORMAL = l_triangle.input.get(j).offset;
                } else if (l_triangle.input.get(j).semantic.equals("TEXCOORD")) {
                    INPUT_TEXCOORD = l_triangle.input.get(j).offset;
                }
                l_xml += "          <input semantic=\"" + l_triangle.input.get(j).semantic + "\" source=\"" + l_triangle.input.get(j).source + "\" offset=\"" + l_triangle.input.get(j).offset + "\"/>\n";
            }
            int INPUT_increment = INPUT_VERTEX + INPUT_NORMAL + INPUT_TEXCOORD;
            if (INPUT_increment == -2) {
                INPUT_increment = 1;
            } else if (INPUT_increment == 0) {
                INPUT_increment = 2;
            } else {
                INPUT_increment = 3;
            }
            String l_p_indices = "";
            Log.d("COLLADA", "INPUT_increment " + INPUT_increment);
            Log.d("COLLADA", "INPUT_VERTEX    " + INPUT_VERTEX);
            Log.d("COLLADA", "INPUT_NORMAL    " + INPUT_NORMAL);
            Log.d("COLLADA", "INPUT_TEXCOORD  " + INPUT_TEXCOORD);
            for (int j = 0; j < l_triangle.p.value.size(); j += INPUT_increment) {
                if (INPUT_VERTEX != -1) { l_p_indices += " " + l_triangle.p.value.get(j + INPUT_VERTEX); }
                if (INPUT_NORMAL != -1) { l_p_indices += " " + l_triangle.p.value.get(j + INPUT_NORMAL); }
                if (INPUT_TEXCOORD != -1) { l_p_indices += " " + l_triangle.p.value.get(j + INPUT_TEXCOORD); }
            }
            Log.d("COLLADA", "l_p_new_indices_position " + l_p_new_indices_position.size() + " " + Arrays.toString(l_p_new_indices_position.toArray()));
            Log.d("COLLADA", "l_p_new_indices_normal   " + l_p_new_indices_normal.size() + " " + Arrays.toString(l_p_new_indices_normal.toArray()));
            Log.d("COLLADA", "l_p_new_indices_map      " + l_p_new_indices_map.size() + " " + Arrays.toString(l_p_new_indices_map.toArray()));
            String l_p_new_indices = "";
            for (int j = 0; j < l_p_new_indices_position.size(); ++j) {
                l_p_new_indices += (" " + l_p_new_indices_position.get(j) + " " + l_p_new_indices_normal.get(j) + " " + l_p_new_indices_map.get(j));
            }
            l_xml += "          <p>" + l_p_new_indices + "</p>\n";
            l_xml += "        </triangles>\n";
        }
        l_xml += "      </mesh>\n";
        l_xml += "    </geometry>\n";
        l_xml += "  </library_geometries>";
        Log.d("COLLADA", "l_xml\n" + l_xml);
        /*
        */
        return l_xml;
    }
}
