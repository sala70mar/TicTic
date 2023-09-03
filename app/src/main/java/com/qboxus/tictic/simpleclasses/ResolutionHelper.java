package com.qboxus.tictic.simpleclasses;

public class ResolutionHelper {

    public ResolutionHelper() {
    }

    public ResolutionHelper(double height, double width) {
        this.height = height;
        this.width = width;
    }

    private double height;
    private double width;

    public double calculateRatio() {
        return height / width;
    }

    public double calculateWidthFromRatio(double givenHeight, double ratio) {
        return givenHeight / ratio;
    }

    public double calculateHeightFromRatio(double givenWidth, double ratio) {
        return givenWidth * ratio;
    }
}