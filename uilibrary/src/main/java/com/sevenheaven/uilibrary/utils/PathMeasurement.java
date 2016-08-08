package com.sevenheaven.uilibrary.utils;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * convenient way to calculate Path and return segment
 *
 * Created by 7heaven on 16/8/8.
 */
public class PathMeasurement {

    private Path mPath;
    private Path[] mSubPaths;
    private Path[] mSubPathsOutput;
    private float[] mSubPathLengths;
    private float[] mSubPathPercentageRanges;
    private float mTotalLength;

    private PathMeasure mPathMeasure;

    public PathMeasurement(Path path){
        mPath = path;

        mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mPath, false);

        initPathCalculation();
    }

    public void setPath(Path path){
        mPath = path;

        initPathCalculation();
    }

    private void initPathCalculation(){
        if(mPath != null){
            //ensure PathMeasure position at the first contour
            mPathMeasure.setPath(mPath, false);
            mTotalLength = 0;

            List<Float> subPathLengths = new ArrayList<>();

            do{
                Path path = new Path();
                float sublength = mPathMeasure.getLength();
                mTotalLength += sublength;
                mPathMeasure.getSegment(0, sublength, path, true);
                subPathLengths.add(sublength);
            }while(mPathMeasure.nextContour());

            final int totalContour = subPathLengths.size();

            mSubPaths = new Path[totalContour];
            mSubPathLengths = new float[totalContour];
            mSubPathsOutput = new Path[totalContour];
            mSubPathPercentageRanges = new float[totalContour * 3];

            float percentageStep = 0;
            for(int i = 0; i < totalContour; i++){
                mSubPaths[i] = new Path();
                mSubPathLengths[i] = subPathLengths.get(i);

                float singlePercentage = mSubPathLengths[i] / mTotalLength;

                mSubPathPercentageRanges[i * 3] = percentageStep;
                mSubPathPercentageRanges[i * 3 + 1] = percentageStep + singlePercentage;
                mSubPathPercentageRanges[i * 3 + 2] = mTotalLength / mSubPathLengths[i];

                percentageStep += singlePercentage;
            }


            mPathMeasure.setPath(mPath, false);
        }
    }

    /**
     * return sub paths based on percentage, notice that the return array with always be same length which is the sub-path count of the main path,
     * so for those sub-paths not covered by percentage will return null;
     * @param percentage
     * @return
     */
    public Path[] updatePhare(float percentage){
        if(mSubPaths != null){

            mPathMeasure.setPath(mPath, false);

            int i = 0;
            do{
                mSubPaths[i].reset();

                float subPathStart = mSubPathPercentageRanges[i * 3];
                float subPathEnd = mSubPathPercentageRanges[i * 3 + 1];
                float mutiples = mSubPathPercentageRanges[i * 3 + 2];

                if(subPathStart >= percentage){
                    mSubPathsOutput[i] = null;
                }else{

                    float endD = subPathEnd >= percentage ? (percentage - subPathStart) * mutiples * mSubPathLengths[i] : mSubPathLengths[i];

                    boolean success = mPathMeasure.getSegment(0, endD, mSubPaths[i], true);
                    if(success){
                        //On KITKAT and earlier releases, the resulting path may not display on a hardware-accelerated Canvas. A simple workaround is to add a single operation to this path
                        //see https://developer.android.com/reference/android/graphics/PathMeasure.html#getSegment(float, float, android.graphics.Path, boolean)
                        mSubPaths[i].rLineTo(0, 0);
                        mSubPathsOutput[i] = mSubPaths[i];
                    }else{
                        mSubPathsOutput[i] = null;
                    }
                }

                i++;

            }while(i < mSubPaths.length && mPathMeasure.nextContour());

            mPathMeasure.setPath(mPath, false);

            return mSubPathsOutput;
        }

        return null;
    }
}
