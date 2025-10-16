package com.nova.fnfjava.flxanimate;

import com.badlogic.gdx.utils.Array;

public class AnimateData {
    public AnimationNode AN;
    public SymbolDictionary SD;
    public MetaData MD;

    public static class AnimationNode {
        public String N; // Name
        public StageInstance STI;
        public String SN; // Symbol Name
        public Timeline TL;
        public int FF; // First Frame (optional)
        public String LP; // Loop type (optional)
        public TransformPoint TRP; // Transform point (optional)
        public float[] M3D; // 4x4 transformation matrix
    }

    public static class StageInstance {
        public SymbolInstance SI;
    }

    public static class Timeline {
        public Array<Layer> L; // Layers
    }

    public static class Layer {
        public String LN; // Layer Name
        public Array<Frame> FR; // Frames
    }

    public static class Frame {
        public int I; // Index
        public int DU; // Duration
        public Array<Element> E; // Elements
        public String N; // Name/Label (optional - for labeled frames)
    }

    public static class Element {
        public SymbolInstance SI; // Symbol Instance
        public AtlasSymbolInstance ASI; // Atlas Symbol Instance
    }

    public static class SymbolInstance {
        public String SN; // Symbol Name
        public String IN; // Instance Name
        public String ST; // Symbol Type (MC = MovieClip, G = Graphic)
        public TransformPoint TRP; // Transform point
        public float[] M3D; // 4x4 transformation matrix
        public Integer FF; // First Frame (optional)
        public String LP; // Loop type (optional)
    }

    public static class AtlasSymbolInstance {
        public String N; // Name (sprite name from atlas)
        public float[] M3D; // 4x4 transformation matrix
        public TransformPoint TRP; // Transform point (optional)
    }

    public static class TransformPoint {
        public float x;
        public float y;
    }

    public static class SymbolDictionary {
        public Array<Symbol> S; // Symbols
    }

    public static class Symbol {
        public String SN; // Symbol Name
        public Timeline TL;
    }

    public static class MetaData {
        public float FRT; // Frame Rate
    }
}
