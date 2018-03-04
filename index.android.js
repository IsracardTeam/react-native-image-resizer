import React from 'react-native';

const ImageResizerAndroid = React.NativeModules.ImageResizerAndroid;
export default {
  createResizedImage: (imagePath, newWidth, newHeight, compressFormat, quality, rotation = 0, outputPath) => {
    return new Promise((resolve, reject) => {
      ImageResizerAndroid.createResizedImage(imagePath, newWidth, newHeight,
        compressFormat, quality, rotation, outputPath, resolve, reject);
    });
  },
  resizeImageToCertainSize: (imagePath, targetSizeInBytes, newWidth, compressFormat, quality, rotation = 0, outputPath) => {
    return new Promise((resolve, reject) => {
      ImageResizerAndroid.resizeImageToCertainSize(imagePath, targetSizeInBytes,newWidth,
        compressFormat, quality, rotation, outputPath, resolve, reject);
    });
  }
};
