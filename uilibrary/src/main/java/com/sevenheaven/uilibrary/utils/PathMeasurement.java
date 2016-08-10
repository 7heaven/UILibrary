package com.sevenheaven.uilibrary.utils;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.FloatRange;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Convenient way for Path calculation when Path contain multiple contours
 *
 * Created by 7heaven on 16/8/8.
 */
public class PathMeasurement {

    /**
     * Main path for measurement
     */
    private Path mPath;

    /**
     * Sub paths and stored parameters for calculation
     */
    private Path[] mSubPaths;
    private Path[] mSubPathsOutputStore;
    private Path[] mSubPathsOutput;
    private float[] mSubPathLengths;
    private float[] mSubPathPercentageRanges;

    /**
     * Total length of the main path
     */
    private float mTotalLength;

    /**
     * PathMeasure instance for all calculations
     */
    private PathMeasure mPathMeasure;

    public PathMeasurement(Path path){
        mPath = path;

        mPathMeasure = new PathMeasure();

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
            List<Path> subPaths = new ArrayList<>();

            do{
                Path path = new Path();
                float sublength = mPathMeasure.getLength();
                mTotalLength += sublength;
                mPathMeasure.getSegment(0, sublength, path, true);
                subPathLengths.add(sublength);
                subPaths.add(path);
            }while(mPathMeasure.nextContour());

            final int totalContour = subPathLengths.size();

            mSubPathsOutputStore = new Path[totalContour];
            mSubPaths = new Path[totalContour];
            mSubPathLengths = new float[totalContour];
            mSubPathsOutput = new Path[totalContour];
            mSubPathPercentageRanges = new float[totalContour * 3];

            float percentageStep = 0;
            for(int i = 0; i < totalContour; i++){
                mSubPaths[i] = subPaths.get(i);
                mSubPathsOutputStore[i] = new Path();
                mSubPathLengths[i] = subPathLengths.get(i);

                float singlePercentage = mSubPathLengths[i] / mTotalLength;

                //sub path start position
                mSubPathPercentageRanges[i * 3] = percentageStep;
                //sub path end position
                mSubPathPercentageRanges[i * 3 + 1] = percentageStep + singlePercentage;
                mSubPathPercentageRanges[i * 3 + 2] = mTotalLength / mSubPathLengths[i];

                percentageStep += singlePercentage;
            }


            mPathMeasure.setPath(mPath, false);
        }
    }

    /**
     * Get all the sub paths of the main Path
     * @return
     */
    public Path[] getAllSubPaths(){
        return mSubPaths;
    }

    /**
     * Get position and tangent values based on percentage mapping to the entire Path
     * @param percentage
     * @param pos
     * @param tan
     * @return
     */
    public float getPosTan(@FloatRange(from=0, to=1.0) float percentage, float[] pos, float[] tan){
        return getPosTan(percentage, pos, tan, false, 0);
    }

    /**
     * Get position and tangent values based on percentage, this is useful when Path contains more than one contour
     * @param percentage
     * @param pos
     * @param tan
     * @param async indicate whether percentage should apply to the entire Path length or apply to each sub path
     * @param contourNum this parameter will work only when asyn == true, indicate which contour should be the target to return position and tangent
     * @return
     */
    public float getPosTan(@FloatRange(from=0, to=1.0) float percentage, float[] pos, float[] tan, final boolean async, int contourNum){
        float distance = percentage * mTotalLength;

        if(pos != null && tan != null && pos.length == 2 && tan.length == 2){
            mPathMeasure.setPath(mPath, false);

            int i = 0;
            do{
                if(async){
                    if(i == contourNum){
                        mPathMeasure.getPosTan(mSubPathLengths[i] * percentage, pos, tan);
                        break;
                    }
                }else{
                    float subPathStart = mSubPathPercentageRanges[i * 3];
                    float subPathEnd = mSubPathPercentageRanges[i * 3 + 1];
                    float multiples = mSubPathPercentageRanges[i * 3 + 2];

                    if(subPathStart < percentage && subPathEnd >= percentage){
                        float subDistance = subPathEnd >= percentage ? (percentage - subPathStart) * multiples * mSubPathLengths[i] : mSubPathLengths[i];

                        mPathMeasure.getPosTan(subDistance, pos, tan);
                    }else if(subPathStart >= percentage){
                        break;
                    }
                }

                i++;
            }while(i < mSubPathsOutputStore.length && mPathMeasure.nextContour());

            mPathMeasure.setPath(mPath, false);
        }

        return distance;
    }

    /**
     * Return sub paths based on percentage, notice that the return array with always be same length which is the sub-path count of the main path,
     * so for those sub-paths not covered by percentage will return null;
     * @param percentage
     * @param async indicate whether percentage should apply to the entire Path length or apply to each sub path
     * @return
     */
    public Path[] updatePhare(@FloatRange(from=0, to=1.0) float percentage, final boolean async){
        if(mSubPathsOutputStore != null){

            mPathMeasure.setPath(mPath, false);

            int i = 0;
            do{
                mSubPathsOutputStore[i].reset();

                if(async){
                    boolean success = mPathMeasure.getSegment(0, mSubPathLengths[i] * percentage, mSubPathsOutputStore[i], true);
                    if(success){
                        // On KITKAT and earlier releases, the resulting path may not display on a hardware-accelerated Canvas. A simple workaround is to add a single operation to this path
                        // @see https://developer.android.com/reference/android/graphics/PathMeasure.html#getSegment(float, float, android.graphics.Path, boolean)
                        mSubPathsOutputStore[i].rLineTo(0, 0);
                        mSubPathsOutput[i] = mSubPathsOutputStore[i];
                    }else{
                        mSubPathsOutput[i] = null;
                    }
                }else{
                    float subPathStart = mSubPathPercentageRanges[i * 3];
                    float subPathEnd = mSubPathPercentageRanges[i * 3 + 1];
                    float multiples = mSubPathPercentageRanges[i * 3 + 2];

                    if(subPathStart >= percentage){
                        mSubPathsOutput[i] = null;
                    }else{

                        float endD = subPathEnd >= percentage ? (percentage - subPathStart) * multiples * mSubPathLengths[i] : mSubPathLengths[i];

                        boolean success = mPathMeasure.getSegment(0, endD, mSubPathsOutputStore[i], true);
                        if(success){
                            // On KITKAT and earlier releases, the resulting path may not display on a hardware-accelerated Canvas. A simple workaround is to add a single operation to this path
                            // @see https://developer.android.com/reference/android/graphics/PathMeasure.html#getSegment(float, float, android.graphics.Path, boolean)
                            mSubPathsOutputStore[i].rLineTo(0, 0);
                            mSubPathsOutput[i] = mSubPathsOutputStore[i];
                        }else{
                            mSubPathsOutput[i] = null;
                        }
                    }
                }

                i++;

            }while(i < mSubPathsOutputStore.length && mPathMeasure.nextContour());

            mPathMeasure.setPath(mPath, false);

            return mSubPathsOutput;
        }

        return null;
    }
}
