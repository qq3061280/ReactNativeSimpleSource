/**
 * Created by lijie on 16/7/13.
 */
'use strict';
import React, { Component } from 'react';
import {
    Navigator,
    TouchableOpacity,
    StyleSheet,
    PixelRatio,
    Text,
    TextInput,
    View,
    BackAndroid,
} from 'react-native';
import HomePage from './HomePage';
import Constants from './Constants';
import EntPage from './EntPage';
const routes = {
    navigator(initialRoute){
        return (
            <Navigator
                initialRoute={{id: initialRoute}}
                renderScene={this.renderScene}
                configureScene={(route)=>{
                    if(route.sceneConfig){
                        return route.sceneConfig;
                    }
                    return Navigator.SceneConfigs.FloatFromRight;
                }}
                tabLabel={this._tabObjForRoute(initialRoute)}
            />
        );
    },
    _tabObjForRoute(routeName){
        let tab=Constants.TAB_ARRAYS[0];
        switch (routeName){
            case 'tab_1':
                tab=Constants.TAB_ARRAYS[0];
                break;
            case 'tab_2':
                tab=Constants.TAB_ARRAYS[1];
                break;
            case 'tab_3':
                tab=Constants.TAB_ARRAYS[2];
                break;
            case 'tab_4':
                tab=Constants.TAB_ARRAYS[3];
                break;
        }
        return tab;
    },
    renderScene(route,navigator){
        BackAndroid.addEventListener('hardwareBackPress', () => {
            if (navigator && navigator.getCurrentRoutes().length > 1) {
                navigator.pop();
                return true;
            }
            return false;
        });
        switch (route.id){
            case 'tab_1':
                return <HomePage />;
            case 'tab_2':
                return (
                    <View style={{flex:1}}>
                        <Text>游戏</Text>
                    </View>
                );
            case 'tab_3':
                return (
                    <View style={{flex:1}}>
                        <Text>随拍</Text>
                    </View>
                );
            case 'tab_4':
                return <EntPage />;
            default:
                return null;
        }
    }
};

module.exports=routes;