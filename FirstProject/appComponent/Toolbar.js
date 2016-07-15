/**
 * Created by lijie on 16/7/12.
 */
'use strict';

import React, { Component } from 'react';
import {
    StyleSheet,
    Text,
    View,
    Image,
    Dimensions
} from 'react-native';

export default class Toolbar extends Component {

    render(){
        let tabName=this.props.tabArray[this.props.selectedTab].name;
        return (
            <View style={styles.container}>
                <View style={styles.roundView}>
                    <Image
                        style={{width:28,height:28,marginRight:6}}
                        source={{uri:'ic_top_avatar_focus'}}
                        resizeMode={Image.resizeMode.contain}
                    />
                </View>

                <Text style={{flex:1,textAlign:'center',fontSize:18 }}>{tabName}</Text>
                <View style={styles.roundView}>
                    <Image
                        style={{width:6,height:44,marginRight:12}}
                        source={{uri:'pic_cutline'}}
                        resizeMode={Image.resizeMode.contain}
                    />
                    <Image
                        style={{width:20,height:20,marginRight:6}}
                        source={{uri:'tab_search'}}
                        resizeMode={Image.resizeMode.contain}
                    />
                </View>
            </View>
        );
    }
}

let styles=StyleSheet.create({
    container:{
        height:50,
        backgroundColor:'#FFFFFF',
        flexDirection:'row',
        width:Dimensions.get('window').width,
        alignItems:'center',
        elevation:1
    },
    roundView:{
        flexDirection:'row',
        width:50,
        alignItems:'center',
        justifyContent:'center',
    }
});

