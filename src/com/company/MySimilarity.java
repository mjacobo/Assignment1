package com.company;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

public class MySimilarity extends DefaultSimilarity {
    @Override
    public float coord(int overlap, int maxOverlap) {
        return super.coord(overlap, maxOverlap);
    }
    @Override
    public float idf(long docFreq, long numDocs) {
        return super.idf(docFreq, numDocs);
    }
    @Override
    public float lengthNorm(FieldInvertState state) {
        return super.lengthNorm(state);
    }
    @Override
    public float queryNorm(float sumOfSquaredWeights) {
        return super.queryNorm(sumOfSquaredWeights);
    }
    @Override
    public float scorePayload(int doc, int start, int end,
                              BytesRef payload) {
        return super.scorePayload(doc, start, end, payload);
    }
    @Override
    public float sloppyFreq(int distance) {
        return super.sloppyFreq(distance);
    }
    @Override
    public float tf(float freq) {
        return super.tf(freq);
    }
}
