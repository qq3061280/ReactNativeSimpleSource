/**
 * Created by lijie on 16/7/13.
 */
import React, { Component } from 'react';
import {
    View,
    ProgressBarAndroid,
} from 'react-native';

class CommonComponents {
    static renderLoadingView() {
            return (
                <View style={{flex:1,justifyContent:'center',alignItems:'center'}}>
                    <ProgressBarAndroid styleAttr="Inverse"/>
                </View>
            )
    }
}
module.exports = CommonComponents;

