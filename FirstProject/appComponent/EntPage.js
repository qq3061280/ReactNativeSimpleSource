/**
 * Created by lijie on 16/7/14.
 */
'use strict';
import React, { Component } from 'react';
import {
    StyleSheet,
    Text,
    View,
    TouchableNativeFeedback,
    TouchableOpacity,
    Alert,

} from 'react-native';

export default class EntPage extends Component {
    render(){
        return (
            <View style={{flex:1,justifyContent:'center',alignItems:'center'}}>
                <TouchableOpacity onPress={()=>{
                    }}>
                    <View style={{width:140,height:60,justifyContent:'center',alignItems:'center',backgroundColor:'red',borderRadius:4}}>
                        <Text style={{color:'white',fontSize:18}}>更新</Text>
                    </View>
                </TouchableOpacity>
            </View>
        );
    }
}