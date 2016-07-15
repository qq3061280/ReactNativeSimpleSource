/**
 * Created by lijie on 16/7/13.
 */
'use strict';

import React, { Component } from 'react';
import {
    StyleSheet,
    Text,
    View,
    Image,
    TouchableOpacity
} from 'react-native';

const propTypes = {
    goToPage: React.PropTypes.func,
    activeTab: React.PropTypes.number,
    tabs: React.PropTypes.array
};

class TabBar extends Component{
    renderTabOption(tab,page){
        var isTabActive = this.props.activeTab === page;
        const tabImg=isTabActive ? tab.selected : tab.normal;
        const tabName=tab.name;
        return (
            <TouchableOpacity
                style={styles.tab}
                onPress={() => this.props.goToPage(page)}
                key={tabName}
            >
                <View style={styles.tab}>
                    <Image
                        style={styles.itemImage}
                        source={{uri:tabImg}}
                        resizeMode={Image.resizeMode.contain}
                    />
                    <Text style={styles.itemText}>{tabName}</Text>
                </View>
            </TouchableOpacity>
        );
    }

    render(){
        return (
                <View style={{flexDirection:'row',height:52,backgroundColor:'white',elevation:1}}>
                    {this.props.tabs.map((tab, i) => this.renderTabOption(tab, i))}
                </View>
        );
    }
}
let styles = StyleSheet.create({
    tab: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    itemImage:{
        width:22,
        height:22
    },
    itemText:{
        fontSize:12,
        marginTop:4
    }
});

TabBar.propTypes = propTypes;
export default TabBar;