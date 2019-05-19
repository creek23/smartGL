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
package com.quixiegames.krixwarestudios.covertops;

import java.util.ArrayList;
import java.util.HashMap;

import fr.arnaudguyon.smartgl.math.Vector3D;
import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Texture;

/**
 * Created by creek23 on 19.05.19.
 * For use of WavefrontModel and ColladaModel
 */
public class BaseModel {
    protected float[] mColor = {1,0,0};
    protected ArrayList<Face3D> mFaces = new ArrayList<>();
    protected ArrayList<Vertex> mVertex = new ArrayList<>();
    protected ArrayList<UV> mUVs = new ArrayList<>(); //DAE's TEXCOORD (Texture Coordinate)
    protected ArrayList<Normal> mNormals = new ArrayList<>();
    protected ArrayList<Strip> mStrips = new ArrayList<>();
    
    protected HashMap<String, Texture> mTextures = new HashMap<>();
    
    protected static class IndexInfo {
        int mVertexIndex;
        int mUVIndex;
        int mNormalIndex;

        static IndexInfo create(Integer vertexIndex, Integer uvIndex, Integer normalIndex) {
            if (vertexIndex == null) {
                return null;
            }
            IndexInfo indexInfo = new IndexInfo();
            indexInfo.mVertexIndex = vertexIndex;
            indexInfo.mUVIndex = (uvIndex != null) ? uvIndex: -1;
            indexInfo.mNormalIndex = (normalIndex != null) ? normalIndex: -1;
            return indexInfo;
        }
        static IndexInfo create(int vertexIndex, int uvIndex, int normalIndex) {
            IndexInfo indexInfo = new IndexInfo();
            indexInfo.mVertexIndex = vertexIndex;
            indexInfo.mUVIndex = uvIndex;
            indexInfo.mNormalIndex = normalIndex;
            return indexInfo;
        }
    }

    protected static class Strip {
        String mTextureName;
        ArrayList<IndexInfo> mIndexes = new ArrayList<>();

        Strip(String textureName) {
            mTextureName = textureName;
        }
        void addIndex(IndexInfo indexInfo) { mIndexes.add(indexInfo); }
        void addAll(ArrayList<IndexInfo> indexes) {
            mIndexes.addAll(indexes);
        }
    }
    
    protected class Vertex {
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
    protected class Normal {
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
    protected class UV {
        float mU;
        float mV;
        UV(float u, float v) {
            mU = u;
            mV = v;
        }
    }
}
