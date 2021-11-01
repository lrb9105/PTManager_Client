package com.teamnova.ptmanager.test;

import com.github.piasy.biv.loader.glide.GlideModel;

public interface CustomImageSizeModel extends GlideModel {
    String requestCustomSizeUrl(Integer width, Integer height);
}